document.addEventListener("DOMContentLoaded", () => {
    "use strict";

    const idInput = document.getElementById("id");
    const usernameMsg = document.getElementById("usernameMsg");
    const btnCheckUsername = document.getElementById("btnCheckUsername");
    const signupForm = document.getElementById("signupForm");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirmPassword");
    const passwordError = document.getElementById("passwordError");
    const config = document.getElementById("signup-config");

    if (
        !idInput ||
        !usernameMsg ||
        !btnCheckUsername ||
        !signupForm ||
        !passwordInput ||
        !confirmPasswordInput ||
        !passwordError
    ) {
        console.error("회원가입 화면의 필수 요소가 누락되었습니다.");
        return;
    }

    const checkIdUrl = config?.dataset.checkIdUrl;
    const ID_PATTERN = /^[a-zA-Z0-9_]{4,30}$/;
    const REQUEST_TIMEOUT_MS = 15000;

    // 중복 확인을 통과한 아이디
    let checkedUsername = null;

    // 현재 진행 중인 요청
    let activeRequest = null;
	
	// 재요청 가능한 시각
	let retryAllowedAt = 0;
	
	const remainingSeconds = Math.ceil(
	    (retryAllowedAt - Date.now()) / 1000
	);

	if (remainingSeconds > 0) {
	    showUsernameMessage(
	        `${remainingSeconds}초 후 다시 시도해주세요.`,
	        "error"
	    );
	    return;
	}
	
	
	

    function showUsernameMessage(message = "", type = "") {
        usernameMsg.textContent = message;
        usernameMsg.className = type
            ? `check-message ${type}`
            : "check-message";
    }

    function showPasswordError(message = "") {
        passwordError.textContent = message;
        passwordError.hidden = !message;
    }

    function cancelCheck() {
        activeRequest?.abort();
        activeRequest = null;
        checkedUsername = null;
        btnCheckUsername.disabled = false;
    }

    // 아이디 변경 시 기존 확인 결과와 요청 취소
    idInput.addEventListener("input", () => {
        cancelCheck();
        showUsernameMessage();
    });

    passwordInput.addEventListener("input", () => {
        showPasswordError();
    });

    confirmPasswordInput.addEventListener("input", () => {
        showPasswordError();
    });

    // 중복 확인 버튼이 폼을 제출하지 않도록 설정
    btnCheckUsername.type = "button";

    usernameMsg.setAttribute("aria-live", "polite");
    passwordError.setAttribute("aria-live", "polite");

    // 아이디 중복 확인
    btnCheckUsername.addEventListener("click", async (event) => {
        event.preventDefault();

        if (activeRequest) return;

        const id = idInput.value.trim();
        idInput.value = id;
        checkedUsername = null;

        if (!id || !ID_PATTERN.test(id)) {
            showUsernameMessage(
                !id
                    ? "아이디를 입력해주세요."
                    : "아이디는 영문 대소문자, 숫자, 밑줄(_)로 4~30자여야 합니다.",
                "error"
            );

            idInput.focus();
            return;
        }

        if (!checkIdUrl) {
            showUsernameMessage(
                "아이디 중복 확인 주소가 설정되지 않았습니다.",
                "error"
            );
            return;
        }

        const controller = new AbortController();
        activeRequest = controller;

        btnCheckUsername.disabled = true;
        showUsernameMessage("아이디를 확인하고 있습니다.");

        // 요청 도중 아이디가 바뀌었는지 확인
        const isCurrentRequest = () =>
            activeRequest === controller && idInput.value === id;

        const timeoutId = setTimeout(
            () => controller.abort(),
            REQUEST_TIMEOUT_MS
        );

        try {
            const url = new URL(checkIdUrl, document.baseURI);
            url.searchParams.set("id", id);

            const response = await fetch(url, {
                headers: {
                    Accept: "application/json"
                },
                cache: "no-store",
                signal: controller.signal
            });

            if (!isCurrentRequest()) return;

            // 요청 횟수 제한: 본문을 읽기 전에 처리
			if (response.status === 429) {
			    const seconds = Number(
			        response.headers.get("Retry-After")
			    );

			    if (Number.isFinite(seconds) && seconds > 0) {
			        retryAllowedAt = Date.now() + Math.ceil(seconds) * 1000;

			        showUsernameMessage(
			            `요청이 너무 많습니다. ${Math.ceil(seconds)}초 후 다시 시도해주세요.`,
			            "error"
			        );
			    } else {
			        showUsernameMessage(
			            "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.",
			            "error"
			        );
			    }

			    return;
			}

            const data = await response.json();

            if (!isCurrentRequest()) return;

            if (!response.ok) {
                const message =
                    typeof data?.message === "string" &&
                        data.message.trim()
                        ? data.message
                        : "아이디 중복 확인 요청을 처리하지 못했습니다.";

                showUsernameMessage(message, "error");
                return;
            }

            // 잘못된 응답을 '사용 가능'으로 판단하지 않도록 검사
            if (typeof data?.isDuplicate !== "boolean") {
                throw new Error(
                    "아이디 중복 확인 응답 형식이 올바르지 않습니다."
                );
            }

            if (data.isDuplicate) {
                showUsernameMessage(
                    "이미 사용 중인 아이디입니다.",
                    "error"
                );
                return;
            }

            checkedUsername = id;

            showUsernameMessage(
                "사용 가능한 아이디입니다.",
                "success"
            );
        } catch (error) {
            // 취소된 이전 요청은 현재 화면을 변경하지 않음
            if (!isCurrentRequest()) return;

            showUsernameMessage(
                controller.signal.aborted
                    ? "응답 시간이 초과되었습니다. 다시 시도해주세요."
                    : "중복 확인 중 오류가 발생했습니다. 다시 시도해주세요.",
                "error"
            );
        } finally {
            clearTimeout(timeoutId);

            // 이전 요청이 새 요청의 버튼 상태를 바꾸지 않도록 검사
            if (activeRequest === controller) {
                activeRequest = null;
                btnCheckUsername.disabled = false;
            }
        }
    });

    // 회원가입 폼 검사
    signupForm.addEventListener("submit", (event) => {
        if (
            activeRequest ||
            checkedUsername === null ||
            idInput.value !== checkedUsername
        ) {
            event.preventDefault();

            showUsernameMessage(
                activeRequest
                    ? "아이디 중복 확인이 끝날 때까지 기다려주세요."
                    : "아이디 중복 확인을 진행해주세요.",
                "error"
            );

            idInput.focus();
            return;
        }

        // 검사만 수행하고 실제 비밀번호 값은 변경하지 않음
        if (!passwordInput.value.trim()) {
            event.preventDefault();

            showPasswordError("비밀번호를 입력해주세요.");
            passwordInput.focus();
            return;
        }

        if (passwordInput.value !== confirmPasswordInput.value) {
            event.preventDefault();

            showPasswordError("비밀번호가 일치하지 않습니다.");
            confirmPasswordInput.focus();
            return;
        }

        showPasswordError();
    });

    // 폼 초기화 시 확인 상태도 초기화
    signupForm.addEventListener("reset", () => {
        cancelCheck();
        showUsernameMessage();
        showPasswordError();
    });
});
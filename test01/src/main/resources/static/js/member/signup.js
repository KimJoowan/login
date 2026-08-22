document.addEventListener("DOMContentLoaded", function() {
    const idInput = document.getElementById("id");
    const usernameMsg = document.getElementById("usernameMsg");
    const btnCheckUsername = document.getElementById("btnCheckUsername");
    const signupForm = document.getElementById("signupForm");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirmPassword");
    const passwordError = document.getElementById("passwordError");

    let isUsernameChecked = false;

    // 아이디 정규식 (영문 소문자, 숫자, 언더바(_), 하이픈(-), 4~30
    const idRegex = /^[a-zA-Z0-9_]{4,30}$/;

    // 비밀번호 에러 메시지 초기화 함수
    function clearPasswordError() {
        passwordError.textContent = "";
        passwordError.hidden = true;
    }

    // 아이디가 바뀌면 중복확인을 다시 해야 함
    idInput.addEventListener("input", function() {
        isUsernameChecked = false;
        usernameMsg.textContent = "";
        usernameMsg.className = "check-message";
    });

    // 비밀번호 입력 시 실시간 에러 메시지 초기화
    passwordInput.addEventListener("input", clearPasswordError);
    confirmPasswordInput.addEventListener("input", clearPasswordError);

    // 아이디 중복확인
    btnCheckUsername.addEventListener("click", async function() {
        const id = idInput.value.trim();
		idInput.value = id;
		
        if (!id) {
            usernameMsg.textContent = "아이디를 입력해주세요.";
            usernameMsg.className = "check-message error";
            idInput.focus();
            return;
        }

        if (!idRegex.test(id)) {
            usernameMsg.textContent = "아이디는 영문 소문자, 숫자, special문자(_,-) 4~30자여야 합니다.";
            usernameMsg.className = "check-message error";
            idInput.focus();
            return;
        }

        btnCheckUsername.disabled = true;

        try {
			const config = document.getElementById("signup-config");
			const checkIdUrl = config.dataset.checkIdUrl;

			const response = await fetch(
			    `${checkIdUrl}?id=${encodeURIComponent(id)}`
			);

			const data = await response.json();
			
			if (!response.ok) {
			    usernameMsg.textContent =
			        data.message || "올바른 아이디를 입력해주세요.";
			    usernameMsg.className = "check-message error";
			    isUsernameChecked = false;
			    return;
			}

            if (data.isDuplicate) {
                usernameMsg.textContent = "이미 사용 중인 아이디입니다.";
                usernameMsg.className = "check-message error";
                isUsernameChecked = false;
            } else {
                usernameMsg.textContent = "사용 가능한 아이디입니다.";
                usernameMsg.className = "check-message success";
                isUsernameChecked = true;
            }
        } catch (error) {
            console.error("아이디 중복 확인 오류:", error);
            usernameMsg.textContent = "중복 확인 중 오류가 발생했습니다.";
            usernameMsg.className = "check-message error";
            isUsernameChecked = false;
        } finally {
            btnCheckUsername.disabled = false;
        }
    });

    // 회원가입 폼 검사
    signupForm.addEventListener("submit", function(event) {
        if (!isUsernameChecked) {
            event.preventDefault();
            usernameMsg.textContent = "아이디 중복 확인을 진행해주세요.";
            usernameMsg.className = "check-message error";
            idInput.focus();
            return;
        }

        // 1. 비밀번호 필수 입력 검증
        if (!passwordInput.value.trim()) {
            event.preventDefault();
            passwordError.textContent = "비밀번호를 입력해주세요.";
            passwordError.hidden = false;
            passwordInput.focus();
            return;
        }

        // 2. 비밀번호 일치 여부 검증
        if (passwordInput.value !== confirmPasswordInput.value) {
            event.preventDefault();
            passwordError.textContent = "비밀번호가 일치하지 않습니다.";
            passwordError.hidden = false;
            confirmPasswordInput.focus();
            return;
        }

        clearPasswordError();
    });
});

document.addEventListener("DOMContentLoaded", function() {
    const idInput =
        document.getElementById("id");

    const usernameMsg =
        document.getElementById("usernameMsg");

    const btnCheckUsername =
        document.getElementById("btnCheckUsername");

    const signupForm =
        document.getElementById("signupForm");

    const passwordInput =
        document.getElementById("password");

    const confirmPasswordInput =
        document.getElementById("confirmPassword");

    const passwordError =
        document.getElementById("passwordError");

    let isUsernameChecked = false;

    // 아이디가 바뀌면 중복확인을 다시 해야 함
    idInput.addEventListener("input", function() {
        isUsernameChecked = false;
        usernameMsg.textContent = "";
        usernameMsg.className = "check-message";
    });

    // 아이디 중복확인
    btnCheckUsername.addEventListener("click", async function() {
        const id = idInput.value.trim();

        if (!id) {
            usernameMsg.textContent =
                "아이디를 입력해주세요.";

            usernameMsg.className =
                "check-message error";

            idInput.focus();
            return;
        }

        btnCheckUsername.disabled = true;

        try {
            const requestUrl =
                "/member/check-id?id="
                + encodeURIComponent(id);

            const response = await fetch(requestUrl, {
                method: "GET",
                headers: {
                    "Accept": "application/json"
                }
            });

            if (!response.ok) {
                throw new Error(
                    "요청 실패: " + response.status
                );
            }

            const data = await response.json();

            if (data.isDuplicate) {
                usernameMsg.textContent =
                    "이미 사용 중인 아이디입니다.";

                usernameMsg.className =
                    "check-message error";

                isUsernameChecked = false;
            } else {
                usernameMsg.textContent =
                    "사용 가능한 아이디입니다.";

                usernameMsg.className =
                    "check-message success";

                isUsernameChecked = true;
            }
        } catch (error) {
            console.error(
                "아이디 중복 확인 오류:",
                error
            );

            usernameMsg.textContent =
                "중복 확인 중 오류가 발생했습니다.";

            usernameMsg.className =
                "check-message error";

            isUsernameChecked = false;
        } finally {
            btnCheckUsername.disabled = false;
        }
    });

    // 회원가입 폼 검사
    signupForm.addEventListener("submit", function(event) {
        if (!isUsernameChecked) {
            event.preventDefault();

            usernameMsg.textContent =
                "아이디 중복 확인을 진행해주세요.";

            usernameMsg.className =
                "check-message error";

            idInput.focus();
            return;
        }

        if (
            passwordInput.value
            !== confirmPasswordInput.value
        ) {
            event.preventDefault();

            passwordError.textContent =
                "비밀번호가 일치하지 않습니다.";

            passwordError.style.display = "block";
            confirmPasswordInput.focus();
            return;
        }

        passwordError.style.display = "none";
    });
});
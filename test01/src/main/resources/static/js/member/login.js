document.getElementById("loginForm")
    .addEventListener("submit", function(event) {
        const idInput = document.getElementById("id");
        const passwordInput = document.getElementById("password");
        const loginError = document.getElementById("login-error");

        const id = idInput.value.trim();
        const password = passwordInput.value;

        idInput.value = id;

        if (!id || !password) {
            event.preventDefault();
            loginError.textContent = "아이디와 비밀번호를 입력해주세요.";
			loginError.hidden = false;
            return;
        }

        loginError.textContent = "";
		loginError.hidden = true;
    });
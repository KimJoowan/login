document.getElementById("loginForm")
    .addEventListener("submit", function(event) {
        const id = document.getElementById("id").value.trim();
        const password = document.getElementById("password").value;
		const loginError = document.getElementById("login-error");

		if (!id || !password) {
		    loginError.textContent = "아이디와 비밀번호를 입력해주세요.";
		    return;
		}

    });
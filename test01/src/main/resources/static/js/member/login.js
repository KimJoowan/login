document.getElementById("loginForm")
    .addEventListener("submit", function(event) {
        const id = document.getElementById("id").value.trim();
        const password = document.getElementById("password").value;

        if (!id || !password) {
            event.preventDefault();
            alert("");
        }
    });
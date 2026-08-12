document.getElementById("deleteForm")
    .addEventListener("submit", function(event) {
        const confirmed = confirm(
            "정말 회원 탈퇴하시겠습니까?\n"
            + "탈퇴하면 모든 회원 정보가 삭제됩니다."
        );

        if (!confirmed) {
            event.preventDefault();
        }
    });
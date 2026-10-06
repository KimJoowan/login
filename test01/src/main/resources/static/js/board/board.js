"use strict";
document.querySelectorAll("[data-delete-form]").forEach(form => {
    form.addEventListener("submit", event => {
        if (!window.confirm("이 게시글을 삭제할까요? 삭제한 글은 되돌릴 수 없습니다.")) event.preventDefault();
    });
});
const editor = document.querySelector("[data-editor]");
if (editor) {
    const title = editor.querySelector("#title");
    const content = editor.querySelector("#content");
    const update = () => {
        document.querySelector("#title-count").textContent = title.value.length + " / 200";
        document.querySelector("#content-count").textContent = content.value.length.toLocaleString("ko-KR") + " / 10,000";
        title.setCustomValidity(title.value.trim() ? "" : "제목을 입력해 주세요.");
        content.setCustomValidity(!content.value.trim() ? "내용을 입력해 주세요."
            : content.value.length > 10000 ? "내용은 10,000자 이하로 입력해 주세요." : "");
    };
    title.addEventListener("input", update);
    content.addEventListener("input", update);
    update();
}


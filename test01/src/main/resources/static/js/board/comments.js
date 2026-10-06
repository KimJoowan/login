"use strict";
(() => {
    const root = document.querySelector("[data-comments]");
    if (!root) return;
    const list = root.querySelector("[data-comment-list]");
    const status = root.querySelector("[data-comment-status]");
    const retry = root.querySelector("[data-comment-retry]");
    const pagination = root.querySelector("[data-comment-pagination]");
    const form = root.querySelector("[data-comment-form]");
    const input = form.querySelector("textarea");
    const counter = root.querySelector("#comment-count");
    let page = 1;
    let loading = false;
    let pendingPage = null;
    const amount = 10;
    const element = (tag, className, text) => {
        const node = document.createElement(tag);
        if (className) node.className = className;
        if (text !== undefined) node.textContent = text;
        return node;
    };
    const message = (text, error = false) => {
        status.textContent = text;
        status.classList.toggle("is-error", error);
    };
    async function request(path = "", options = {}) {
        const headers = { Accept: "application/json", ...options.headers };
        if (options.body) headers["Content-Type"] = "application/json";
        if (options.method && root.dataset.csrfHeader) headers[root.dataset.csrfHeader] = root.dataset.csrfToken;
        const response = await fetch(root.dataset.api + path, { ...options, headers, credentials: "same-origin" });
        if (!response.ok) {
            if (response.status === 401) throw new Error("로그인이 만료되었습니다. 다시 로그인한 뒤 시도해 주세요.");
            if (response.status === 403) throw new Error("요청을 처리할 수 없습니다. 페이지를 새로고침한 뒤 시도해 주세요.");
            if (response.status === 404) throw new Error("댓글이나 게시글이 없거나 수정·삭제 권한이 없습니다.");
            const problem = await response.json().catch(() => ({}));
            throw new Error(problem.errors?.join(" ") || problem.detail || "요청을 처리하지 못했습니다. 잠시 후 다시 시도해 주세요.");
        }
        if (response.status === 204) return null;
        return response.json();
    }
    function validate(textarea) {
        textarea.setCustomValidity(!textarea.value.trim() ? "댓글 내용을 입력해 주세요."
            : textarea.value.length > 2000 ? "댓글은 2,000자 이하로 입력해 주세요." : "");
        return textarea.reportValidity();
    }
    const button = (text, className, action) => {
        const node = element("button", className, text);
        node.type = "button";
        node.addEventListener("click", action);
        return node;
    };
    function renderComment(comment) {
        const item = element("article", "comment-item");
        const meta = element("div", "comment-meta");
        const author = comment.authorName?.trim() || "이름 없는 회원";
        meta.append(element("span", "avatar", author.slice(0, 1)), element("span", "comment-author", author));
        const own = root.dataset.viewer !== "0" && String(comment.idNumber) === root.dataset.viewer;
        if (own) meta.append(element("span", "comment-own", "내 댓글"));
        const time = element("time");
        const date = new Date(comment.createdAt);
        if (!Number.isNaN(date.getTime())) {
            time.dateTime = comment.createdAt;
            time.textContent = date.toLocaleString("ko-KR", { year: "numeric", month: "2-digit", day: "2-digit", hour: "2-digit", minute: "2-digit" });
        }
        meta.append(time);
        const body = element("p", "comment-body", comment.content);
        item.append(meta, body);
        if (!own) return item;
        const tools = element("div", "comment-tools");
        const edit = button("수정", "", () => {
            tools.hidden = true;
            body.hidden = true;
            const editor = element("form", "comment-edit");
            const textarea = element("textarea");
            textarea.id = "edit-comment-" + comment.ccode;
            textarea.value = comment.content;
            textarea.maxLength = 2000;
            textarea.required = true;
            const label = element("label", "", "댓글 수정");
            label.htmlFor = textarea.id;
            const actions = element("div", "comment-edit-actions");
            const cancel = button("취소", "button button-outline button-small", () => {
                editor.remove(); body.hidden = false; tools.hidden = false; edit.focus();
            });
            const save = element("button", "button button-green button-small", "수정 저장");
            save.type = "submit";
            actions.append(cancel, save);
            editor.append(label, textarea, actions);
            textarea.addEventListener("input", () => textarea.setCustomValidity(""));
            editor.addEventListener("submit", async event => {
                event.preventDefault();
                if (!validate(textarea)) return;
                save.disabled = cancel.disabled = true;
                try {
                    await request("/" + comment.ccode, { method: "PUT", body: JSON.stringify({ content: textarea.value }) });
                    message("댓글을 수정했습니다.");
                    await load(page);
                } catch (error) { message(error.message, true); }
                finally { save.disabled = cancel.disabled = false; }
            });
            item.append(editor);
            textarea.focus();
        });
        const remove = button("삭제", "comment-delete", async () => {
            if (!window.confirm("이 댓글을 삭제할까요?")) return;
            remove.disabled = edit.disabled = true;
            try {
                await request("/" + comment.ccode, { method: "DELETE" });
                message("댓글을 삭제했습니다.");
                await load(page);
            } catch (error) { message(error.message, true); }
            finally { remove.disabled = edit.disabled = false; }
        });
        tools.append(edit, remove);
        item.append(tools);
        return item;
    }
    async function load(targetPage) {
        if (loading) { pendingPage = targetPage; return; }
        loading = true;
        list.setAttribute("aria-busy", "true");
        pagination.querySelectorAll("button").forEach(node => node.disabled = true);
        retry.hidden = true;
        try {
            const query = new URLSearchParams({ bcode: root.dataset.bcode, pageNum: targetPage, amount });
            const result = await request("?" + query);
            page = result.pageNum;
            root.querySelector("[data-comment-total]").textContent = result.total.toLocaleString("ko-KR");
            list.replaceChildren(...result.comments.map(renderComment));
            if (!result.comments.length) list.append(element("p", "comment-empty", "아직 댓글이 없어요. 첫 번째 이야기를 남겨보세요."));
            const last = Math.max(1, Math.ceil(result.total / result.amount));
            const prev = button("← 이전", "button button-outline button-small", () => load(page - 1));
            const next = button("다음 →", "button button-outline button-small", () => load(page + 1));
            prev.disabled = page <= 1;
            next.disabled = page >= last;
            pagination.replaceChildren(prev, element("span", "", page + " / " + last), next);
            pagination.hidden = last <= 1;
        } catch (error) {
            message(error.message, true);
            retry.hidden = false;
            pagination.hidden = true;
        } finally {
            loading = false;
            list.setAttribute("aria-busy", "false");
            if (pendingPage !== null) {
                const target = pendingPage;
                pendingPage = null;
                await load(target);
            }
        }
    }
    input.addEventListener("input", () => {
        input.setCustomValidity("");
        counter.textContent = input.value.length.toLocaleString("ko-KR") + " / 2,000";
    });
    form.addEventListener("submit", async event => {
        event.preventDefault();
        if (!validate(input)) return;
        const submit = form.querySelector("button");
        submit.disabled = true;
        input.readOnly = true;
        try {
            await request("", { method: "POST", body: JSON.stringify({ bcode: Number(root.dataset.bcode), content: input.value }) });
            input.value = "";
            counter.textContent = "0 / 2,000";
            message("댓글을 등록했습니다.");
            // The API clamps a large page number to the last page containing the new comment.
            await load(2147483647);
        } catch (error) { message(error.message, true); }
        finally { submit.disabled = false; input.readOnly = false; }
    });
    retry.addEventListener("click", () => { message(""); load(page); });
    load(1);
})();

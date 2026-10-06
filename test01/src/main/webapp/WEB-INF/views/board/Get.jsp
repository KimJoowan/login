<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><c:out value="${BoardDto.title}" /> · MyService</title>
<link rel="stylesheet" href="<c:url value='/css/common.css'/>">
<link rel="stylesheet" href="<c:url value='/css/board/board.css'/>">
<script src="<c:url value='/js/board/board.js'/>" defer></script>
<link rel="stylesheet" href="<c:url value='/css/board/comments.css'/>">
<script src="<c:url value='/js/board/comments.js'/>" defer></script>
</head>
<body>
	<%@ include file="header.jspf"%>
	<main class="shell board-main reading-main" id="main">
		<a class="back-link" href="<c:url value='/board/list'/>">← 전체 이야기</a>
		<c:if test="${not empty message}">
			<p class="notice" role="status">
				<c:out value="${message}" />
			</p>
		</c:if>
		<article class="article-card">
			<p class="eyebrow">
				COMMUNITY / NO.
				<c:out value="${BoardDto.bcode}" />
			</p>
			<h1 class="article-title">
				<c:out value="${BoardDto.title}" />
			</h1>
			<div class="article-meta">
				<span class="avatar" aria-hidden="true">m.</span><span><c:out value="${empty fn:trim(BoardDto.authorName) ? '이름 없는 회원' : BoardDto.authorName}" /></span>
				<time datetime="<c:out value='${BoardDto.createdAt}'/>">
					<c:out value="${fn:substring(BoardDto.createdAt, 0, 10)}" />
				</time>
			</div>
			<div class="article-content"><c:out value="${BoardDto.content}" /></div>
			<div class="article-actions">
				<a class="button button-outline" href="<c:url value='/board/list'/>">목록으로</a>
				<c:if test="${canEdit}">
					<div class="action-group">
						<c:url var="editUrl" value="/board/modify">
							<c:param name="bcode" value="${BoardDto.bcode}" />
						</c:url>
						<a class="button button-green" href="${editUrl}">수정하기</a>
						<form action="<c:url value='/board/remove'/>" method="post" data-delete-form>
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><input type="hidden" name="bcode" value="${BoardDto.bcode}">
							<button class="button button-danger" type="submit">삭제</button>
						</form>
					</div>
				</c:if>
			</div>
		</article>
		<section class="comments-panel" aria-labelledby="comments-title" data-comments
			data-api="<c:url value='/api/comments'/>" data-bcode="${BoardDto.bcode}"
			data-viewer="${commentViewerNumber}" data-csrf-header="<c:out value='${_csrf.headerName}'/>"
			data-csrf-token="<c:out value='${_csrf.token}'/>">
			<div class="comments-heading">
				<div><p class="eyebrow">KEEP THE CONVERSATION GOING</p>
					<h2 id="comments-title">함께 나누는 이야기 <span class="count" data-comment-total>0</span></h2>
				</div><span class="comments-symbol" aria-hidden="true">✳</span>
			</div>
			<p class="comments-description">공감한 순간이나 새로운 생각을 들려주세요.</p>
			<form class="comment-composer" data-comment-form>
				<label for="comment-content">댓글 남기기</label>
				<textarea id="comment-content" name="content" rows="3" maxlength="2000" required
					placeholder="서로를 존중하는 따뜻한 말로 대화를 이어가요." aria-describedby="comment-count"></textarea>
				<div class="comment-composer-footer"><span id="comment-count">0 / 2,000</span>
					<button class="button button-green button-small" type="submit">댓글 등록 <span aria-hidden="true">↗</span></button>
				</div>
			</form>
			<p class="comment-status" data-comment-status role="status" aria-live="polite"></p>
			<button class="button button-outline button-small" type="button" data-comment-retry hidden>다시 불러오기</button>
			<div class="comment-list" data-comment-list aria-busy="true"><p class="comment-empty">댓글을 불러오고 있어요.</p></div>
			<nav class="comment-pagination" aria-label="댓글 페이지" data-comment-pagination hidden></nav>
			<noscript><p class="comment-empty">댓글을 보거나 작성하려면 JavaScript를 활성화해 주세요.</p></noscript>
		</section>
	</main>
	<footer class="board-footer shell">
		<span>MyService.</span><span>서로의 생각이 만나는 작은 공간.</span><small>© 2026 MyService</small>
	</footer>
</body>
</html>

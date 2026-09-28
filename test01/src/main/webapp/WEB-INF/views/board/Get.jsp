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
				<span class="avatar" aria-hidden="true">m.</span><span>회원 <c:out value="${BoardDto.idNumber}" /></span>
				<time datetime="<c:out value='${BoardDto.createdAt}'/>">
					<c:out value="${fn:substring(BoardDto.createdAt, 0, 10)}" />
				</time>
			</div>
			<div class="article-content">
				<c:out value="${BoardDto.content}" />
			</div>
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
	</main>
	<footer class="board-footer shell">
		<span>MyService.</span><span>서로의 생각이 만나는 작은 공간.</span><small>© 2026 MyService</small>
	</footer>
</body>
</html>

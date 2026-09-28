<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${editing ? '글 수정' : '새 이야기'}· MyService</title>
<link rel="stylesheet" href="<c:url value='/css/common.css'/>">
<link rel="stylesheet" href="<c:url value='/css/board/board.css'/>">
<script src="<c:url value='/js/board/board.js'/>" defer></script>
</head>
<body>
	<%@ include file="header.jspf"%>
	<main class="shell board-main writing-main" id="main">
		<a class="back-link" href="<c:url value='/board/list'/>">← 전체 이야기</a>
		<div class="writing-heading">
			<p class="eyebrow">SHARE YOUR STORY</p>
			<h1>${editing ? '이야기 다듬기' : '어떤 이야기를 나눌까요?'}</h1>
			<p>작은 생각도 좋아요. 당신의 목소리로 자유롭게 적어 주세요.</p>
		</div>
		<c:if test="${not empty error}">
			<p class="notice error" role="alert">
				<c:out value="${error}" />
			</p>
		</c:if>
		<c:url var="saveUrl" value="${editing ? '/board/modify' : '/board/register'}" />
		<form class="editor-card" action="${saveUrl}" method="post" data-editor>
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
			<c:if test="${editing}">
				<input type="hidden" name="bcode" value="${BoardDto.bcode}">
			</c:if>
			<div class="field-heading">
				<label for="title">제목 <span aria-hidden="true">*</span></label><span id="title-count">최대 200자</span>
			</div>
			<input id="title" name="title" type="text" maxlength="200" required placeholder="이야기의 제목을 적어 주세요" value="<c:out value='${BoardDto.title}'/>" aria-describedby="title-count">
			<div class="field-heading">
				<label for="content">내용 <span aria-hidden="true">*</span></label><span>나누고 싶은 이야기를 자유롭게</span>
			</div>
			<textarea id="content" name="content" required rows="14" placeholder="오늘 떠오른 생각, 함께 나누고 싶은 질문…"><c:out value="${BoardDto.content}" /></textarea>
			<p class="editor-hint">서로를 존중하는 글을 작성해 주세요. 개인정보는 포함하지 않는 것이 좋아요.</p>
			<div class="article-actions">
				<c:choose>
					<c:when test="${editing}">
						<c:url var="cancelUrl" value="/board/get">
							<c:param name="bcode" value="${BoardDto.bcode}" />
						</c:url>
					</c:when>
					<c:otherwise>
						<c:url var="cancelUrl" value="/board/list" />
					</c:otherwise>
				</c:choose>
				<a class="button button-outline" href="${cancelUrl}">취소</a>
				<button class="button button-green" type="submit">${editing ? '수정 완료' : '이야기 등록'}
					<span aria-hidden="true">↗</span>
				</button>
			</div>
		</form>
	</main>
	<footer class="board-footer shell">
		<span>MyService.</span><span>서로의 생각이 만나는 작은 공간.</span><small>© 2026 MyService</small>
	</footer>
</body>
</html>

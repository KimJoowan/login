<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="<c:url value='/css/member/login.css' />">
<script src="<c:url value='/js/member/login.js'/>" defer></script>
<title>로그인</title>
</head>
<body>
	<div class="login-container">
		<h2>로그인</h2>
		<c:if test="${param.error != null}">
			<div class="login-error" role="alert">
				아이디 또는 비밀번호를 확인해 주세요.
			</div>
		</c:if>
		<form id="loginForm" action="${pageContext.request.contextPath}/member/login" method="post">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
			<div class="input-group">
				<label for="id">아이디</label> <input type="text" name="id" id="id" placeholder="아이디를 입력하세요" required>
			</div>
			<div class="input-group">
				<label for="password">비밀번호</label> <input type="password" name="password" id="password" placeholder="비밀번호를 입력하세요" required>
			</div>
			<button type="submit" class="btn-submit">로그인</button>
		</form>
	</div>
</body>
</html>

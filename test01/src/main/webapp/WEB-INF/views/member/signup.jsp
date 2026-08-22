<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:url var="loginUrl" value="/member/login" />
<c:url var="checkIdUrl" value="/member/check-id" />
<c:url var="signupUrl" value="/member/signup" />
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="<c:url value='/css/member/signup.css' />">
<script src="<c:url value='/js/member/signup.js'/>" defer></script>
<title>회원가입</title>
</head>
<body>
	<div class="signup-container">
		<h2>회원가입</h2>
		<form:form id="signupForm" modelAttribute="signupRequest" action="${signupUrl}" method="post">
			<!-- CSRF 토큰 -->
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
			<div class="input-group">
				<label for="id">아이디</label>
				<div class="input-with-btn">
					<input type="text" id="id" name="id" value="<c:out value='${signupRequest.id}'/>" minlength="4" maxlength="30" required>
					<form:errors path="id" cssClass="error-message" />
					<button type="button" id="btnCheckUsername" class="btn-check">중복 확인</button>
				</div>
				<div id="signup-config" data-check-id-url="<c:url value='/member/check-id'/>"></div>
				<div id="usernameMsg" class="check-message"></div>
			</div>
			<div class="input-group">
				<label for="password">비밀번호</label> <input type="password" id="password" name="password" minlength="10" maxlength="100" required>
				<form:errors path="password" cssClass="error-message" />
			</div>
			<div class="input-group">
				<label for="confirmPassword"> 비밀번호 확인 </label> <input type="password" id="confirmPassword" autocomplete="new-password" required>
				<div id="passwordError" class="error-message" hidden>비밀번호가 일치하지 않습니다.</div>
			</div>
			<div class="input-group">
				<label for="userName">닉네임</label> <input type="text" id="userName" name="userName" value="<c:out value='${signupRequest.userName}'/>" maxlength="30" required>
				<form:errors path="userName" cssClass="error-message" />
			</div>
			<div class="input-group">
				<label for="email">이메일</label> <input type="email" id="email" name="email" value="<c:out value='${signupRequest.email}'/>" maxlength="254" required>
				<form:errors path="email" cssClass="error-message" />
			</div>
			<button type="submit" class="btn-submit">가입하기</button>
		</form:form>
		<div class="footer">
			<p>
				이미 계정이 있으신가요? <a href="${loginUrl}">로그인</a>
			</p>
		</div>
	</div>
</body>
</html>

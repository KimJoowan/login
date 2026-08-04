<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="jakarta.tags.core" %>
		<c:url var="loginUrl" value="/member/login" />
		<c:url var="checkIdUrl" value="/member/check-id" />
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

				<form id="signupForm" action="${signupUrl}" method="post">
					<!-- CSRF 토큰 -->
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

					<div class="input-group">
						<label for="id">아이디</label>

						<div class="input-with-btn">
							<input type="text" id="id" name="id" placeholder="사용할 아이디를 입력하세요" autocomplete="username"
								required>

							<button type="button" id="btnCheckUsername" class="btn-check">
								중복 확인
							</button>
						</div>

						<div id="usernameMsg" class="check-message"></div>
					</div>

					<div class="input-group">
						<label for="password">비밀번호</label>

						<input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요"
							autocomplete="new-password" required>
					</div>

					<div class="input-group">
						<label for="confirmPassword">
							비밀번호 확인
						</label>

						<input type="password" id="confirmPassword" placeholder="비밀번호를 다시 입력하세요"
							autocomplete="new-password" required>

						<div id="passwordError" class="error-message" style="display: none">
							비밀번호가 일치하지 않습니다.
						</div>
					</div>

					<div class="input-group">
						<label for="userName">닉네임</label>

						<input type="text" id="userName" name="userName" placeholder="사용할 닉네임을 입력하세요" required>
					</div>

					<div class="input-group">
						<label for="email">이메일</label>

						<input type="email" id="email" name="email" placeholder="example@email.com" autocomplete="email"
							required>
					</div>

					<button type="submit" class="btn-submit">
						가입하기
					</button>
				</form>

				<div class="footer">
					<p>
						이미 계정이 있으신가요?
						<a href="${loginUrl}">로그인</a>
					</p>
				</div>
			</div>


		</body>

		</html>
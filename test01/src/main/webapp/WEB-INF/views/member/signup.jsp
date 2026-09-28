<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:url var="loginUrl" value="/member/login" />
<c:url var="checkIdUrl" value="/member/check-id" />
<c:url var="signupUrl" value="/member/signup" />
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="<c:url value='/css/common.css' />">
<link rel="stylesheet" href="<c:url value='/css/member/common.css' />">
<link rel="stylesheet" href="<c:url value='/css/member/signup.css' />">
<script src="<c:url value='/js/member/signup.js'/>" defer></script>
<title>회원가입</title>
</head>
<body>
    <header class="auth-header">
        <a class="brand" href="<c:url value='/'/>" aria-label="MyService 홈"><span class="brand-mark" aria-hidden="true">m<span>·</span></span>MyService<span class="brand-period">.</span></a>
        <a class="back-home" href="<c:url value='/'/>">← 홈으로 돌아가기</a>
    </header>
    <main class="auth-layout">
        <aside class="auth-story">
            <p class="eyebrow">A FRESH START</p>
            <h1>작은 시작,<br> 새로운 가능성.</h1>
            <p>복잡한 과정 없이, 필요한 것만 간결하게.<br> 지금 나만의 공간을 만들어 보세요.</p>
            <div class="story-art" aria-hidden="true"><span>m.</span></div>
            <span class="story-caption">A LITTLE SIMPLER. A LITTLE BETTER.</span>
        </aside>
	<div class="signup-container">
		<p class="form-kicker">CREATE AN ACCOUNT</p><h2 class="form-title">나만의 공간 만들기</h2><p class="form-description">아래 정보를 입력하면 새로운 시작이 준비됩니다.</p>
		<form:form id="signupForm" modelAttribute="signupRequest" action="${signupUrl}" method="post">
			<!-- CSRF 토큰 -->
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
			<div class="input-group">
				<label for="id">아이디</label>
				<div class="input-with-btn">
					<input type="text" id="id" name="id" autocomplete="username" placeholder="영문, 숫자, 밑줄 4~30자" value="<c:out value='${signupRequest.id}'/>" minlength="4" maxlength="30" required>
					
					<button type="button" id="btnCheckUsername" class="btn-check">중복 확인</button>
				</div>
				<form:errors path="id" cssClass="error-message" />
                <div id="signup-config" data-check-id-url="<c:url value='/member/check-id'/>"></div>
				<div id="usernameMsg" class="check-message"></div>
			</div>
			<div class="input-group">
				<label for="password">비밀번호</label> <input type="password" id="password" name="password" autocomplete="new-password" placeholder="10자 이상 입력해 주세요" minlength="10" maxlength="100" required>
				<form:errors path="password" cssClass="error-message" />
			</div>
			<div class="input-group">
				<label for="confirmPassword"> 비밀번호 확인 </label> <input type="password" id="confirmPassword" placeholder="비밀번호를 한 번 더 입력해 주세요" autocomplete="new-password" required>
				<div id="passwordError" class="error-message" hidden>비밀번호가 일치하지 않습니다.</div>
			</div>
			<div class="input-group">
				<label for="userName">닉네임</label> <input type="text" id="userName" name="userName" autocomplete="nickname" placeholder="어떻게 불러드릴까요?" value="<c:out value='${signupRequest.userName}'/>" maxlength="30" required>
				<form:errors path="userName" cssClass="error-message" />
			</div>
			<button type="submit" class="btn-submit">가입하기</button>
		</form:form>
		<div class="footer">
			<p>
				이미 계정이 있으신가요? <a href="${loginUrl}">로그인</a>
			</p>
		</div>
	</div>
    </main>
    <footer class="auth-copyright">© 2026 MyService · 조금 더 단순하게, 조금 더 나답게.</footer>
</body>
</html>



<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="<c:url value='/css/common.css' />">
<link rel="stylesheet" href="<c:url value='/css/member/common.css' />">
<link rel="stylesheet" href="<c:url value='/css/member/login.css' />">
<script src="<c:url value='/js/member/login.js'/>" defer></script>
<title>로그인</title>
</head>
<body>
    <header class="auth-header">
        <a class="brand" href="<c:url value='/'/>" aria-label="MyService 홈"><span class="brand-mark" aria-hidden="true">m<span>·</span></span>MyService<span class="brand-period">.</span></a>
        <a class="back-home" href="<c:url value='/'/>">← 홈으로 돌아가기</a>
    </header>
    <main class="auth-layout">
        <aside class="auth-story">
            <p class="eyebrow">WELCOME BACK</p>
            <h1>다시 만나서<br> 반가워요.</h1>
            <p>나만의 공간에서 이어지는 이야기.<br> 오늘도 편안하게 시작하세요.</p>
            <div class="story-art" aria-hidden="true"><span>m.</span></div>
            <span class="story-caption">A LITTLE SIMPLER. A LITTLE BETTER.</span>
        </aside>
	<div class="login-container">
		<p class="form-kicker">LOG IN</p><h2 class="form-title">로그인</h2><p class="form-description">계정에 로그인하고 나만의 공간으로 이동하세요.</p>
		<form id="loginForm" action="${pageContext.request.contextPath}/member/login" method="post">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
			<div class="input-group">
				<label for="id">아이디</label> <input type="text" name="id" id="id" autocomplete="username" placeholder="아이디를 입력하세요" required>
			</div>
			<div class="input-group">
				<label for="password">비밀번호</label> <input type="password" name="password" id="password" autocomplete="current-password" placeholder="비밀번호를 입력하세요" required>
			</div>
			<div id="login-error" class="error-message" role="alert"><c:if test="${param.error != null}">아이디 또는 비밀번호가 올바르지 않습니다.</c:if></div>
			<button type="submit" class="btn-submit">로그인</button>
		</form><div class="footer"><p>아직 계정이 없으신가요? <a href="<c:url value='/member/signup'/>">회원가입</a></p></div>
	</div>
    </main>
    <footer class="auth-copyright">© 2026 MyService · 조금 더 단순하게, 조금 더 나답게.</footer>
</body>
</html>



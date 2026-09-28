<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:url var="homeUrl" value="/" />
<c:url var="loginUrl" value="/member/login" />
<c:url var="signupUrl" value="/member/signup" />
<c:url var="logoutUrl" value="/member/logout" />
<c:url var="memberInfoUrl" value="/member/info" />
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="복잡함은 덜고, 나에게 필요한 것만. MyService에서 나만의 웹 서비스 경험을 시작하세요.">
    <title>MyService — 더 가벼운 시작</title>
    <link rel="stylesheet" href="<c:url value='/css/common.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/main/index.css'/>">
</head>
<body>
    <a class="skip-link" href="#main">본문 바로가기</a>
    <header class="site-header">
        <div class="shell header-inner">
            <a class="brand" href="${homeUrl}" aria-label="MyService 홈"><span class="brand-mark" aria-hidden="true">m<span>·</span></span>MyService<span class="brand-period">.</span></a>
            <nav class="main-nav" aria-label="주요 메뉴"><a href="<c:url value='/board/list'/>">게시판</a><a href="#features">서비스 소개</a><a href="#about">이용 방법</a><a href="#faq">자주 묻는 질문</a></nav>
            <div class="header-actions">
                <sec:authorize access="!isAuthenticated()"><a class="text-link" href="${loginUrl}">로그인</a><a class="button button-dark button-small" href="${signupUrl}">시작하기 <span aria-hidden="true">↗</span></a></sec:authorize>
                <sec:authorize access="isAuthenticated()">
                    <form action="${logoutUrl}" method="post"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><button class="text-link" type="submit">로그아웃</button></form>
                    <a class="button button-dark button-small" href="${memberInfoUrl}">내 정보 <span aria-hidden="true">↗</span></a>
                </sec:authorize>
            </div>
        </div>
    </header>
    <main id="main">
        <section class="hero shell" aria-labelledby="hero-title">
            <div class="hero-copy">
                <p class="eyebrow"><span class="status-dot"></span> LESS FRICTION, MORE POSSIBILITY</p>
                <h1 id="hero-title">복잡함은 덜고,<br>가능성은 <span class="hero-emphasis">더 넓게.</span></h1>
                <p class="hero-description">나에게 필요한 것만, 더 간결하게.<br>편안하고 자연스러운 웹 경험이 여기서 시작됩니다.</p>
                <div class="hero-actions">
                    <sec:authorize access="!isAuthenticated()"><a class="button button-green" href="${signupUrl}">나만의 공간 시작하기 <span aria-hidden="true">↗</span></a></sec:authorize>
                    <sec:authorize access="isAuthenticated()"><a class="button button-green" href="${memberInfoUrl}">나의 정보 관리하기 <span aria-hidden="true">↗</span></a></sec:authorize>
                    <a class="hero-secondary" href="#features">조금 더 알아보기 <span aria-hidden="true">↓</span></a>
                </div>
                <p class="hero-note"><span aria-hidden="true">✓</span> 간편한 가입 <span class="note-divider">/</span> 나를 위한 계정 관리</p>
            </div>
            <div class="hero-art" role="img" aria-label="나만의 공간을 표현한 프로필 카드 일러스트">
                <div class="art-grid"></div><div class="art-orbit orbit-one"></div><div class="art-orbit orbit-two"></div>
                <span class="art-spark spark-one">✳</span><span class="art-spark spark-two">✦</span>
                <div class="floating-label"><span class="status-dot"></span> A SPACE FOR YOU</div>
                <div class="preview-card">
                    <div class="preview-top"><span class="preview-logo">m<span>·</span></span><span>MY PERSONAL SPACE</span><span class="preview-dots">•••</span></div>
                    <div class="preview-greeting">반가워요! <span>✺</span><br><strong>오늘도, 나답게.</strong></div>
                    <div class="preview-profile"><div class="avatar-art">m.</div><div><strong>나만의 프로필</strong><span>작은 시작, 새로운 가능성</span></div><span class="profile-check">✓</span></div>
                    <div class="preview-tiles"><div><span class="tile-symbol">↗</span><strong>간편하게</strong><small>필요한 순간 바로</small></div><div><span class="tile-symbol">◎</span><strong>나에게 맞게</strong><small>내 정보 한눈에</small></div></div>
                    <div class="preview-bottom"><span>YOUR EVERYDAY, SIMPLIFIED</span><span>↗</span></div>
                </div>
                <div class="floating-security"><span class="security-icon">✓</span><div><strong>내 정보는 소중하니까</strong><span>안전하게, 편안하게.</span></div></div>
                <span class="art-caption">A LITTLE SIMPLER. A LITTLE BETTER.</span>
            </div>
        </section>
        <div class="principles shell"><span>일상에 자연스럽게 스며드는 서비스</span><div><span>01 <strong>간결한 경험</strong></span><span>02 <strong>안전한 연결</strong></span><span>03 <strong>나만의 공간</strong></span></div></div>
        <section class="features shell section-space" id="features" aria-labelledby="features-title">
            <div class="section-heading"><div><p class="eyebrow">THOUGHTFULLY SIMPLE</p><h2 id="features-title">기본에 충실해서,<br>더 편안한 경험.</h2></div><p>처음 만나는 순간부터 매일의 사용까지.<br>꼭 필요한 기능을 한곳에 담았습니다.</p></div>
            <div class="feature-grid">
                <article class="feature-card"><div class="feature-top"><span class="feature-icon" aria-hidden="true">↗</span><span>01 / EASY START</span></div><h3>시작은 가볍게</h3><p>아이디와 기본 정보로 계정을 만들고,<br>나만의 서비스를 만나보세요.</p><a href="${signupUrl}">회원가입 알아보기 <span aria-hidden="true">↗</span></a></article>
                <article class="feature-card"><div class="feature-top"><span class="feature-icon" aria-hidden="true">◇</span><span>02 / SAFE ACCESS</span></div><h3>접속은 안전하게</h3><p>로그인부터 로그아웃까지.<br>안전한 인증으로 내 공간에 접속하세요.</p><a href="${loginUrl}">로그인하기 <span aria-hidden="true">↗</span></a></article>
                <article class="feature-card feature-card-green"><div class="feature-top"><span class="feature-icon" aria-hidden="true">◎</span><span>03 / YOUR SPACE</span></div><h3>관리는 나답게</h3><p>닉네임부터 이메일까지.<br>내 정보를 한눈에 확인하고 수정하세요.</p><a href="${memberInfoUrl}">내 정보 관리하기 <span aria-hidden="true">↗</span></a></article>
            </div>
        </section>
        <section class="getting-started shell" id="about" aria-labelledby="about-title">
            <div><p class="eyebrow">MAKE YOURSELF AT HOME</p><h2 id="about-title">세 번의 작은 단계.<br>새로운 시작의 전부.</h2></div>
            <ol class="steps"><li><span>01</span><div><h3>계정 만들기</h3><p>아이디 중복 확인 후 기본 정보를 입력해 주세요.</p></div></li><li><span>02</span><div><h3>내 공간에 로그인</h3><p>가입한 아이디와 비밀번호로 접속하세요.</p></div></li><li><span>03</span><div><h3>나만의 정보 관리</h3><p>닉네임과 이메일을 언제든 변경할 수 있어요.</p></div></li></ol>
        </section>
        <section class="faq shell section-space" id="faq" aria-labelledby="faq-title"><div><p class="eyebrow">A FEW THINGS TO KNOW</p><h2 id="faq-title">궁금한 점이 있나요?</h2></div><div class="faq-list"><details><summary>가입하려면 어떤 정보가 필요한가요?</summary><p>아이디, 비밀번호, 닉네임, 이메일이 필요해요. 회원가입 화면에서 아이디 중복 확인을 먼저 진행해 주세요.</p></details><details><summary>가입한 정보를 변경할 수 있나요?</summary><p>로그인 후 ‘내 정보’에서 닉네임과 이메일을 수정할 수 있어요. 가입한 아이디는 변경할 수 없습니다.</p></details><details><summary>모바일에서도 사용할 수 있나요?</summary><p>네. 스마트폰, 태블릿, PC 화면에 맞춰 편리하게 이용할 수 있어요.</p></details></div></section>
        <section class="closing shell"><div><p class="eyebrow">YOUR NEXT CHAPTER</p><h2>좋은 시작은, 생각보다 간단해요.</h2><p>지금 나만의 공간을 만들어 보세요.</p></div><sec:authorize access="!isAuthenticated()"><a class="button button-dark" href="${signupUrl}">함께 시작하기 <span aria-hidden="true">↗</span></a></sec:authorize><sec:authorize access="isAuthenticated()"><a class="button button-dark" href="${memberInfoUrl}">내 공간으로 <span aria-hidden="true">↗</span></a></sec:authorize></section>
    </main>
    <footer class="site-footer shell"><a class="brand" href="${homeUrl}">MyService<span class="brand-period">.</span></a><p>조금 더 단순하게, 조금 더 나답게.</p><small>© 2026 MyService</small></footer>
</body>
</html>


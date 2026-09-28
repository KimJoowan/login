<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>게시판 · MyService</title>
<link rel="stylesheet" href="<c:url value='/css/common.css'/>">
<link rel="stylesheet" href="<c:url value='/css/board/board.css'/>">
<script src="<c:url value='/js/board/board.js'/>" defer></script>
</head>
<body>
	<%@ include file="header.jspf"%>
	<main class="shell board-main" id="main">
		<section class="board-hero">
			<div>
				<p class="eyebrow">
					<span class="status-dot"></span> OUR COMMUNITY
				</p>
				<h1>
					작은 생각도,<br>좋은 이야기의 시작.
				</h1>
				<p class="hero-description">
					일상의 발견부터 나누고 싶은 질문까지.<br>당신의 이야기를 들려주세요.
				</p>
			</div>
			<div class="hero-note-art" aria-hidden="true">
				<span>HELLO, COMMUNITY</span><strong>생각을 나누면<br>가능성이 자라나요<span>↗</span></strong><small>WRITE. SHARE. CONNECT.</small>
			</div>
		</section>
		<c:if test="${not empty message}">
			<p class="notice" role="status">
				<c:out value="${message}" />
			</p>
		</c:if>
		<div class="board-layout">
			<section class="posts-panel" aria-labelledby="posts-title">
				<div class="panel-heading">
					<div>
						<h2 id="posts-title">
							전체 이야기 <span class="count"><c:out value="${page.total}" /></span>
						</h2>
						<p>가장 최근에 올라온 이야기부터 만나보세요.</p>
					</div>
					<a class="button button-green button-small" href="<c:url value='/board/register'/>">글쓰기 <span aria-hidden="true">＋</span></a>
				</div>
				<div class="table-wrap">
					<table>
						<thead>
							<tr>
								<th class="number-col" scope="col">번호</th>
								<th scope="col">제목</th>
								<th class="author-col" scope="col">작성자</th>
								<th class="date-col" scope="col">작성일</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${list}" var="board">
								<c:url var="detailUrl" value="/board/get">
									<c:param name="bcode" value="${board.bcode}" />
								</c:url>
								<tr>
									<td class="number-col"><c:out value="${board.bcode}" /></td>
									<td><a class="post-title" href="${detailUrl}"><c:out value="${board.title}" /></a></td>
									<td class="author-col">회원 <c:out value="${board.idNumber}" /></td>
									<td class="date-col"><c:out value="${fn:substring(board.createdAt, 0, 10)}" /></td>
								</tr>
							</c:forEach>
							<c:if test="${empty list}">
								<tr>
									<td colspan="4"><div class="empty-state">
											<span aria-hidden="true">✳</span>
											<h3>첫 이야기를 기다리고 있어요.</h3>
											<p>가벼운 인사로 이 공간을 채워 주세요.</p>
											<a class="text-link" href="<c:url value='/board/register'/>">첫 글 작성하기 ↗</a>
										</div></td>
								</tr>
							</c:if>
						</tbody>
					</table>
				</div>
				<c:if test="${page.total > 0}">
					<nav class="pagination" aria-label="게시글 페이지">
						<c:if test="${page.prev}">
							<c:url var="prevUrl" value="/board/list">
								<c:param name="pageNum" value="${page.startPage - 1}" />
								<c:param name="amount" value="${page.cri.amount}" />
							</c:url>
							<a href="${prevUrl}" aria-label="이전 페이지 묶음">←</a>
						</c:if>
						<c:forEach begin="${page.startPage}" end="${page.endPage}" var="number">
							<c:url var="pageUrl" value="/board/list">
								<c:param name="pageNum" value="${number}" />
								<c:param name="amount" value="${page.cri.amount}" />
							</c:url>
							<c:choose>
								<c:when test="${number == page.cri.pageNum}">
									<span class="current" aria-current="page">${number}</span>
								</c:when>
								<c:otherwise>
									<a href="${pageUrl}" aria-label="${number} 페이지">${number}</a>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						<c:if test="${page.next}">
							<c:url var="nextUrl" value="/board/list">
								<c:param name="pageNum" value="${page.endPage + 1}" />
								<c:param name="amount" value="${page.cri.amount}" />
							</c:url>
							<a href="${nextUrl}" aria-label="다음 페이지 묶음">→</a>
						</c:if>
					</nav>
				</c:if>
			</section>
			<aside class="community-aside">
				<div class="aside-card">
					<p class="eyebrow">A LITTLE KINDNESS</p>
					<span class="aside-symbol" aria-hidden="true">✳</span>
					<h2>
						함께 만드는<br>편안한 공간.
					</h2>
					<p>
						서로 다른 생각을 존중하고,<br>따뜻한 말로 대화해 주세요.
					</p>
					<div class="aside-rule">
						01 <span>서로를 존중해요</span>
					</div>
					<div class="aside-rule">
						02 <span>개인정보는 소중히 해요</span>
					</div>
					<div class="aside-rule">
						03 <span>좋은 이야기를 나눠요</span>
					</div>
				</div>
				<p class="aside-caption">YOUR VOICE MATTERS.</p>
			</aside>
		</div>
	</main>
	<footer class="board-footer shell">
		<span>MyService.</span><span>서로의 생각이 만나는 작은 공간.</span><small>© 2026 MyService</small>
	</footer>
</body>
</html>

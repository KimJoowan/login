<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="jakarta.tags.core" %>
		<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
			<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

				<c:url var="mainUrl" value="/" />
				<c:url var="updateUrl" value="/member/update" />
				<c:url var="deleteUrl" value="/member/delete" />


				<head>
					<meta charset="UTF-8">
					<meta name="viewport" content="width=device-width, initial-scale=1.0">
					<link rel="stylesheet" href="<c:url value='/css/member/info.css' />">
					<script src="<c:url value='/js/member/info.js' />" defer></script>
					<title>회원 정보 수정</title>


				</head>

				<body>

					<div class="profile-container">

						<div class="profile-header">

							<div class="profile-avatar">
								<c:choose>
									<c:when test="${not empty memberUpdateRequest.userName}">
										<c:out value="${fn:substring(memberUpdateRequest.userName, 0, 1)}" />
									</c:when>
									<c:otherwise>U</c:otherwise>
								</c:choose>
							</div>

							<h2>
								<c:out value="${memberUpdateRequest.userName}" default="사용자" />님
							</h2>

							<p>
								<c:out value="${memberUpdateRequest.email}" default="이메일 정보 없음" />
							</p>

							<span class="badge">회원 정보</span>
						</div>

						<!-- 회원정보 수정 -->
						<form:form id="profileForm" modelAttribute="memberUpdateRequest" action="${updateUrl}"
							method="post">

							<div class="input-group">
								<label for="id">아이디</label>
								<input type="text" id="id" value="${fn:escapeXml(id)}" readonly>
							</div>

							<div class="input-group">
								<label for="userName">닉네임</label>

								<form:input path="userName" id="userName" maxlength="30" />

								<form:errors path="userName" cssClass="error-message" delimiter="<br>"/>
							</div>

							<div class="input-group">
								<label for="email">이메일</label>

								<form:input path="email" id="email" type="email" maxlength="254" />

								<form:errors path="email" cssClass="error-message" />
							</div>

							<div class="btn-group">
								<a href="${mainUrl}" class="btn-secondary">
									메인으로
								</a>

								<button type="submit" class="btn-submit">
									회원 정보 수정
								</button>
							</div>
						</form:form>

						<!-- 회원 탈퇴 -->
						<form id="deleteForm" action="${deleteUrl}" method="post">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

							<button type="submit" class="btn-danger-outline">
								회원 탈퇴
							</button>
						</form>

					</div>
				</body>

				</html>
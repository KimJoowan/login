<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="jakarta.tags.core" %>
		<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
			<c:url var="loginUrl" value="/member/login" />
			<c:url var="signupUrl" value="/member/signup" />
			<c:url var="logoutUrl" value="/member/logout" />
			<c:url var="memberInfoUrl" value="/member/info" />

			<!DOCTYPE html>
			<html lang="ko" xmlns:th="http://www.thymeleaf.org"
				xmlns:sec="http://www.thymeleaf.org/extras/spring-security">

			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>메인 - My Web Service</title>
				
				<!-- Local Tailwind CSS -->
				<link rel="stylesheet" href="<c:url value='/css/tailwind.css'/>">
				
				<!-- Custom CSS -->
				<link rel="stylesheet" href="<c:url value='/css/main/index.css'/>">
			</head>

			<body class="bg-gray-50 text-gray-800 flex flex-col min-h-screen">

				<!-- Navigation Header -->
				<header class="bg-white border-b border-gray-200 sticky top-0 z-50">
					<div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
					
						<!-- Logo -->
						<a href="/" th:href="@{/}"
							class="flex items-center gap-2 text-indigo-600 font-bold text-xl tracking-wide"> <svg
								class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
								<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
							</svg> <span>MyService</span>
						</a>
						
						<!-- Navigation Links -->
						<nav class="hidden md:flex items-center space-x-8 text-sm font-medium text-gray-600">
							<a href="#features" class="hover:text-indigo-600 transition">기능 소개</a> <a href="#about"
								class="hover:text-indigo-600 transition">서비스 안내</a> <a href="#contact"
								class="hover:text-indigo-600 transition">고객지원</a>
						</nav>
						
						<!-- 로그인하지 않은 사용자 -->
						<sec:authorize access="!isAuthenticated()">
							<div class="flex items-center gap-3">
								<a href="${loginUrl}"
									class="text-sm font-semibold text-gray-700 hover:text-indigo-600 px-3 py-2 transition">
									로그인 </a> <a href="${signupUrl}"
									class="text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 px-4 py-2 rounded-lg transition shadow-sm">
									회원가입 </a>
							</div>
						</sec:authorize>
						
						<!-- 로그인한 사용자 -->
						<sec:authorize access="isAuthenticated()">
							<div class="flex items-center gap-3">
								<span class="text-sm font-semibold text-gray-700">
									<sec:authentication property="name" />님
								</span>
								<form action="${logoutUrl}" method="post">
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
									<button type="submit"
										class="text-sm font-semibold text-gray-700 hover:text-indigo-600">로그아웃</button>
								</form>
								<a href="${memberInfoUrl}"
									class="text-sm font-semibold text-gray-700 hover:text-indigo-600 px-3 py-2 transition">
									내 정보 </a>
							</div>
						</sec:authorize>
					</div>
				</header>

				<!-- Hero Section -->
				<section class="bg-gradient-to-b from-indigo-50 to-gray-50 py-20 px-4 sm:px-6 lg:px-8">
					<div class="max-w-4xl mx-auto text-center">
						<span
							class="inline-block bg-indigo-100 text-indigo-700 text-xs font-semibold px-3 py-1 rounded-full mb-4">
							Spring Boot Web Application </span>
						<h1 class="text-4xl sm:text-5xl font-extrabold text-gray-900 leading-tight mb-6">
							더 쉽고 스마트한 <br class="hidden sm:inline" /> 웹 서비스 경험을 시작하세요
						</h1>
						<p class="text-lg text-gray-600 mb-8 max-w-2xl mx-auto">스프링부트 기반의
							가볍고 강력한 웹 애플리케이션 프레임워크입니다. 회원가입과 로그인을 통해 다양한 맞춤형 서비스를 지금 바로 체험해 보세요.</p>

						<div class="flex flex-col sm:flex-row justify-center gap-4">
							<a href="#"
								class="w-full sm:w-auto px-8 py-3.5 bg-indigo-600 hover:bg-indigo-700 text-white font-medium rounded-lg text-center transition shadow-lg shadow-indigo-200">
								지금 무료로 시작하기 </a> <a href="#features"
								class="w-full sm:w-auto px-8 py-3.5 bg-white hover:bg-gray-100 text-gray-700 font-medium rounded-lg text-center border border-gray-300 transition">
								주요 기능 살펴보기 </a>
						</div>
					</div>
				</section>

				<!-- Feature Section -->
				<section id="features" class="py-16 px-4 sm:px-6 lg:px-8 max-w-6xl mx-auto flex-grow">
					<div class="text-center mb-12">
						<h2 class="text-2xl sm:text-3xl font-bold text-gray-900">제공하는 핵심
							서비스</h2>
						<p class="text-gray-500 mt-2 text-sm">안정적이고 빠르게 동작하는 핵심 기능을
							확인하세요.</p>
					</div>

					<div class="grid grid-cols-1 md:grid-cols-3 gap-8">
						<!-- Feature 1 -->
						<div
							class="bg-white p-6 rounded-xl border border-gray-100 shadow-sm hover:shadow-md transition">
							<div
								class="w-12 h-12 bg-indigo-100 text-indigo-600 rounded-lg flex items-center justify-center mb-4">
								<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
									<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
										d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
								</svg>
							</div>
							<h3 class="text-lg font-bold text-gray-900 mb-2">보안 인증 시스템</h3>
							<p class="text-sm text-gray-600 leading-relaxed">Spring Security
								기반의 세션 및 토큰 기반 인증으로 안전한 데이터 보호와 회원 관리를 보장합니다.</p>
						</div>

						<!-- Feature 2 -->
						<div
							class="bg-white p-6 rounded-xl border border-gray-100 shadow-sm hover:shadow-md transition">
							<div
								class="w-12 h-12 bg-indigo-100 text-indigo-600 rounded-lg flex items-center justify-center mb-4">
								<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
									<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
										d="M13 10V3L4 14h7v7l9-11h-7z" />
								</svg>
							</div>
							<h3 class="text-lg font-bold text-gray-900 mb-2">보안 인증 시스템</h3>
							<p class="text-sm text-gray-600 leading-relaxed">최적화된 백엔드 구조와
								경량화된 UI 디자인으로 빠른 페이지 로딩 환경을 제공합니다.</p>
						</div>

						<!-- Feature 3 -->
						<div
							class="bg-white p-6 rounded-xl border border-gray-100 shadow-sm hover:shadow-md transition">
							<div
								class="w-12 h-12 bg-indigo-100 text-indigo-600 rounded-lg flex items-center justify-center mb-4">
								<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
									<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
										d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" />
								</svg>
							</div>
							<h3 class="text-lg font-bold text-gray-900 mb-2">반응형 디자인</h3>
							<p class="text-sm text-gray-600 leading-relaxed">모바일, 태블릿, PC 등
								모든 기기 화면에 맞게 최적화된 화면 구성을 지원합니다.</p>
						</div>
					</div>
				</section>

				<!-- Call to Action Banner -->
				<section class="bg-indigo-600 py-12 px-4 text-center text-white">
					<div class="max-w-3xl mx-auto">
						<h2 class="text-2xl font-bold mb-3">지금 바로 가입하고 모든 기능을 사용해 보세요!</h2>
						<p class="text-indigo-100 text-sm mb-6">간단한 정보 입력만으로 빠르게 계정을 생성할
							수 있습니다.</p>
						<a href="#"
							class="inline-block bg-white text-indigo-600 font-bold px-6 py-3 rounded-lg hover:bg-indigo-50 transition shadow">
							무료 회원가입 하기 </a>
					</div>
				</section>

				<!-- Footer -->
				<footer class="bg-white border-t border-gray-200 py-8 px-4 sm:px-6 lg:px-8">
					<div
						class="max-w-6xl mx-auto flex flex-col sm:flex-row items-center justify-between text-sm text-gray-500">
						<p>&copy; 2026 MyService Corp. All rights reserved.</p>
						<div class="flex space-x-6 mt-4 sm:mt-0">
							<a href="#" class="hover:text-gray-700">이용약관</a>
							<a href="#" class="hover:text-gray-700">개인정보처리방침</a> 
							<a href="#" class="hover:text-gray-700">문의하기</a>
						</div>
					</div>
				</footer>

			</html>
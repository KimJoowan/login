<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>서버 오류</title>
</head>

<body>

    <main>
        <h1>500</h1>

        <h2>서버 오류가 발생했습니다.</h2>

        <p>
            요청을 처리하는 중 문제가 발생했습니다.<br>
            잠시 후 다시 시도해주세요.
        </p>

        <a href="${pageContext.request.contextPath}/">
            메인으로 돌아가기
        </a>
    </main>

</body>
</html>
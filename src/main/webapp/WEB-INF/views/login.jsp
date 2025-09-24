<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Sign In - DineDigital</title>
    <%@ include file="/WEB-INF/views/includes/head.jspf" %>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    </head>
<body class="page-wrapper">
<%@ include file="/WEB-INF/views/includes/nav.jspf" %>
<main class="content-wrap content-section">
<div class="container auth-container safe-top" style="max-width: 480px;">
  <div class="card p-4 login-card">
    <c:set var="type" value="${param.type}"/>
    <h2>
        <c:choose>
            <c:when test="${type == 'kitchen'}">Kitchen Login</c:when>
            <c:otherwise>Admin Login</c:otherwise>
        </c:choose>
    </h2>
    <c:if test="${not empty error}"><div class="alert alert-danger mt-2">${error}</div></c:if>
    <form class="mt-3" method="post" action="<c:url value='/auth/login'/>">
        <label class="form-label">Username</label>
        <input class="form-control" type="text" name="username" required />
        <label class="form-label mt-3">Password</label>
        <input class="form-control" type="password" name="password" required />
        <button class="btn btn-primary w-100 mt-3" type="submit">Login</button>
    </form>
    <div class="switch text-center mt-3">
        <a class="link-secondary" style="color: var(--primary-accent) !important;" href="<c:url value='/login?type=admin'/>">Admin</a> ·
        <a class="link-secondary" style="color: var(--primary-accent) !important;" href="<c:url value='/login?type=kitchen'/>">Kitchen</a>
    </div>
</div>
</div>
</main>
<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
</body>
</html>

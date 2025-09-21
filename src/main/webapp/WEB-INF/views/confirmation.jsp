<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Reservation Confirmed - DineDigital</title>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
</head>
<body class="page-wrapper">
  <%@ include file="/WEB-INF/views/includes/nav.jspf" %>
  <main class="content-wrap content-section">
    <div class="container text-center" style="max-width: 720px;">
      <h2 class="accent-heading">Reservation Confirmed</h2>
      <p class="lead mt-3">Thank you, <strong>${name}</strong>. Please save your confirmation code:</p>
      <div class="code-card mt-4">
        <div class="text-muted">Your Confirmation Code</div>
        <div class="code-value" id="conf-code">${code}</div>
      </div>
      <p class="mt-3">We may request this code on arrival to verify your booking.</p>
      <div class="mt-4">
          <a href="<c:url value='/'/>" class="btn btn-primary">Return to Homepage</a>
      </div>
    </div>
  </main>
  <%@ include file="/WEB-INF/views/includes/footer.jspf" %>
</body>
</html>

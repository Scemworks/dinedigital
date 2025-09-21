<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Server Error - DineDigital</title>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
</head>
<body class="page-wrapper">
<%@ include file="/WEB-INF/views/includes/nav.jspf" %>
<main class="content-wrap content-section">
  <div class="container" style="max-width:720px;">
    <div class="code-card text-center mx-auto">
      <h2>Something went wrong</h2>
      <p class="mt-2">Please try again or return to the home page.</p>
      <div class="mt-3">
        <a class="btn btn-primary" href="<c:url value='/'/>">Home</a>
        <a class="btn btn-outline-secondary" href="<c:url value='/order'/>">Order</a>
      </div>
    </div>
  </div>
 </main>
<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
</body>
</html>

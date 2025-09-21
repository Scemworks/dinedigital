<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>DineDigital - An Exquisite Dining Experience</title>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
</head>
<body class="page-wrapper">
  <c:set var="page" value="home"/>
  <%@ include file="/WEB-INF/views/includes/nav.jspf" %>
  <header class="hero">
    <div class="hero-content">
      <h1>An Unforgettable Culinary Journey</h1>
      <p>Experience the art of fine dining, where classic tradition meets the convenience of modern innovation.</p>
      <a href="<c:url value='/menu'/>" class="btn btn-primary">View The Menu</a>
      <a href="<c:url value='/reservation'/>" class="btn btn-outline-light">Book a Table</a>
    </div>
  </header>
  <%@ include file="/WEB-INF/views/includes/footer.jspf" %>
</body>
</html>

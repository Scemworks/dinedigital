<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Our Menu - DineDigital</title>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
</head>
<body class="page-wrapper">
  <c:set var="page" value="menu"/>
  <%@ include file="/WEB-INF/views/includes/nav.jspf" %>
  <main class="content-wrap content-section">
    <div class="container">
      <h2 class="text-center mb-5">From Our Kitchen</h2>
      <div class="row g-4">
        <c:forEach var="item" items="${menu}">
          <div class="col-lg-4 col-md-6">
            <div class="card h-100">
              <img src="${item.image}" class="card-img-top" alt="${item.name}">
              <div class="card-body d-flex flex-column">
                <h5 class="card-title">${item.name}</h5>
                <p class="card-text">${item.description}</p>
                <p class="fw-bold mt-auto">$${item.price}</p>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>
  </main>
  <%@ include file="/WEB-INF/views/includes/footer.jspf" %>
</body>
</html>

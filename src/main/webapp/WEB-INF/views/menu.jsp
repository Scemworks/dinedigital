<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
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
              <div style="position:relative;">
                <c:choose>
                  <c:when test="${not empty item.image}">
                    <img src="${item.image}" class="card-img-top" alt="${item.name}">
                  </c:when>
                  <c:otherwise>
                    <div class="card-img-top d-flex align-items-center justify-content-center" style="height:220px;background:#1a1a1a;color:#777;">No image</div>
                  </c:otherwise>
                </c:choose>
                <span class="badge badge-gold" style="position:absolute; top:10px; right:10px; font-size:0.95rem;">₹ <fmt:formatNumber value="${item.price}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
              </div>
              <div class="card-body d-flex flex-column">
                <h5 class="card-title">${item.name}</h5>
                <p class="card-text" style="display:-webkit-box; -webkit-line-clamp:3; -webkit-box-orient:vertical; overflow:hidden;">${item.description}</p>
                <div class="mt-auto d-flex align-items-center justify-content-between">
                  <span class="text-muted small">Chef's choice</span>
                  <a href="<c:url value='/order'/>" class="btn btn-sm btn-primary">Order</a>
                </div>
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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Kitchen Display - DineDigital</title>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
</head>
<body class="page-wrapper">
  <%@ include file="/WEB-INF/views/includes/nav.jspf" %>

  <main class="content-wrap content-section">
    <div class="container">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
          <c:choose>
            <c:when test="${pageContext.request.isUserInRole('ADMIN')}">
              <li class="breadcrumb-item"><a href="<c:url value='/admin'/>">Admin</a></li>
            </c:when>
            <c:otherwise>
              <li class="breadcrumb-item"><a href="<c:url value='/'/>">Home</a></li>
            </c:otherwise>
          </c:choose>
          <li class="breadcrumb-item active" aria-current="page">Kitchen</li>
        </ol>
      </nav>
      <div class="mb-3">
        <c:choose>
          <c:when test="${pageContext.request.isUserInRole('ADMIN')}">
            <a class="btn btn-outline-secondary" href="<c:url value='/admin'/>">Back to Admin</a>
          </c:when>
          <c:otherwise>
            <a class="btn btn-outline-secondary" href="<c:url value='/'/>">Back to Home</a>
          </c:otherwise>
        </c:choose>
      </div>
      <h2 class="mb-4">Pending Orders</h2>
      <c:choose>
        <c:when test="${empty orders}">
          <div class="alert alert-secondary">No pending orders.</div>
        </c:when>
        <c:otherwise>
          <div class="row g-4">
            <c:forEach var="order" items="${orders}">
              <div class="col-md-6">
                <div class="card h-100">
                  <div class="card-body d-flex flex-column">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                      <h5 class="card-title mb-0">Order #${order.orderId}</h5>
                      <span class="badge badge-gold">NEW</span>
                    </div>
                    <p class="mb-1"><strong>Table:</strong> <c:out value="${order.tableNumber != null ? order.tableNumber : '—'}"/></p>
                    <p class="mb-3"><strong>Reservation:</strong> <c:out value="${order.reservationId != null && order.reservationId > 0 ? order.reservationId : '—'}"/></p>
                    <p class="mb-3"><strong>Ordered at:</strong> <c:out value="${order.createdAtStr}"/></p>
          <ul class="list-group list-group-flush flex-grow-1">
                      <c:forEach var="it" items="${order.items}">
            <li class="list-group-item d-flex justify-content-between align-items-center kitchen-item">
                          <span>${it.name}</span>
                          <span class="badge bg-light text-dark">x${it.quantity}</span>
                        </li>
                      </c:forEach>
                    </ul>
                    <form action="<c:url value='/kitchen/complete'/>" method="post" class="mt-3">
                      <input type="hidden" name="orderId" value="${order.realId}"/>
                      <button type="submit" class="btn btn-success w-100">Mark as Served</button>
                    </form>
                  </div>
                </div>
              </div>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </main>

  <%@ include file="/WEB-INF/views/includes/footer.jspf" %>
</body>
</html>

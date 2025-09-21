<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Billing - DineDigital</title>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  </head>
<body class="page-wrapper">
<%-- unified nav for consistent theming --%>
<%@ include file="/WEB-INF/views/includes/nav.jspf" %>
<main class="content-wrap content-section">
<div class="container" style="max-width: 900px;">
  <h2 class="mb-3">Billing</h2>

  <c:choose>
    <c:when test="${empty orders}">
      <div class="alert alert-secondary">No pending orders.</div>
    </c:when>
    <c:otherwise>
      <div class="row g-3">
        <c:forEach var="order" items="${orders}">
          <div class="col-md-6">
            <div class="card h-100">
              <div class="card-body d-flex flex-column">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <h5 class="card-title mb-0">Order #${order.order_number}</h5>
                  <span class="badge badge-gold">PENDING</span>
                </div>
                <p class="mb-1"><strong>Table:</strong> <c:out value="${order.table_number != null ? order.table_number : '—'}"/></p>
                <p class="mb-1"><strong>Reservation:</strong> <c:out value="${order.reservation_id != null && order.reservation_id > 0 ? order.reservation_id : '—'}"/></p>
                <p class="mb-1"><strong>Status:</strong> ${order.status}</p>
                <p class="mb-3"><strong>Date:</strong> <fmt:formatDate value="${order.created_at}" pattern="yyyy-MM-dd HH:mm"/></p>
                <div class="mt-auto">
                  <a class="btn btn-outline-primary btn-sm" href="<c:url value='/admin/billing/pdf'><c:param name='orderId' value='${order.real_id}'/></c:url>">Download PDF</a>
                  <form method="post" action="<c:url value='/admin/billing/paid'/>" class="d-inline ms-2">
                    <input type="hidden" name="orderId" value="${order.real_id}"/>
                    <button type="submit" class="btn btn-success btn-sm" onclick="return confirm('Mark order as paid? This will remove it.');">Mark as Paid</button>
                  </form>
                </div>
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

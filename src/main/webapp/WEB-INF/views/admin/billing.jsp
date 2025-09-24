<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Billing - DineDigital</title>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
</head>
<body class="page-wrapper">
<%@ include file="/WEB-INF/views/includes/nav.jspf" %>
<div class="container" style="padding:2rem; max-width: 900px;">
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="<c:url value='/admin'/>">Admin</a></li>
      <li class="breadcrumb-item active" aria-current="page">Billing</li>
    </ol>
  </nav>
  <h2>Billing</h2>
  <form class="row g-3" method="get" action="<c:url value='/admin/billing'/>">
    <div class="col-auto">
      <label class="col-form-label">Order Number</label>
    </div>
    <div class="col-auto">
      <input type="number" min="1" class="form-control" name="orderId" placeholder="#" value="${param.orderId}">
    </div>
    <div class="col-auto">
      <button class="btn btn-primary" type="submit">Lookup</button>
    </div>
  </form>

  <c:if test="${not empty notFound}">
    <div class="alert alert-warning mt-3">Order not found.</div>
  </c:if>

  <c:if test="${param.paid == '1'}">
    <div class="alert alert-success mt-3">Payment recorded. Order removed.</div>
  </c:if>

  <c:if test="${not empty order}">
    <div class="card mt-3 p-3">
      <div class="d-flex justify-content-between">
        <div>
          <div><strong>Order #</strong> ${order.order_id}</div>
          <div><strong>Table</strong> ${order.table_number}</div>
        </div>
        <div class="text-end">
          <div><strong>Status</strong> ${order.status}</div>
          <div><strong>Date</strong> ${order.created_at}</div>
        </div>
      </div>
      <div class="mt-2">
        <a class="btn btn-outline-primary btn-sm" href="<c:url value='/admin/billing/pdf'><c:param name='orderId' value='${order.order_id}'/></c:url>">Download PDF</a>
        <form method="post" action="<c:url value='/admin/billing/paid'/>" class="d-inline ms-2">
          <input type="hidden" name="orderId" value="${order.order_id}"/>
          <button type="submit" class="btn btn-success btn-sm" onclick="return confirm('Mark order as paid? This will remove it.');">Mark as Paid</button>
        </form>
      </div>
      <hr/>
      <div class="table-responsive">
      <table class="table">
        <thead><tr><th>Item</th><th class="text-end">Qty</th><th class="text-end">Price</th><th class="text-end">Amount</th></tr></thead>
        <tbody>
        <c:forEach items="${items}" var="i">
          <tr>
            <td>${i.name}</td>
            <td class="text-end">${i.quantity}</td>
            <td class="text-end">${i.price}</td>
            <td class="text-end">
              <c:set var="amt" value="${i.price * i.quantity}"/>
              ${amt}
            </td>
          </tr>
        </c:forEach>
        </tbody>
        <tfoot>
          <c:if test="${not empty order.reservation_id}">
            <tr><th colspan="3" class="text-end">Reservation Fee</th><th class="text-end">₹ 50.00</th></tr>
          </c:if>
          <tr><th colspan="3" class="text-end">Total</th><th class="text-end">${total}</th></tr>
        </tfoot>
      </table>
      </div>
    </div>
  </c:if>

</div>
<div class="container mb-4">
  <a class="btn btn-outline-secondary" href="<c:url value='/admin'/>">Back</a>
  </div>
<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
</body>
</html>

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
      <c:if test="${not empty orderId}">
        <div class="card mt-4 p-3 text-start">
          <h5>Pre-order Details</h5>
          <div><strong>Guests:</strong> ${guests}</div>
          <div><strong>Reservation:</strong> ${reservationDate} ${reservationTime}</div>
          <hr/>
          <table class="table table-sm">
            <thead><tr><th>Item</th><th class="text-end">Qty</th><th class="text-end">Price</th><th class="text-end">Amount</th></tr></thead>
            <tbody>
            <c:forEach items="${preorderItems}" var="it">
              <tr>
                <td>${it.name}</td>
                <td class="text-end">${it.quantity}</td>
                <td class="text-end">${it.price}</td>
                <td class="text-end">${it.price * it.quantity}</td>
              </tr>
            </c:forEach>
            </tbody>
            <tfoot>
              <tr><th colspan="3" class="text-end">Subtotal</th><th class="text-end">${preorderSubtotal}</th></tr>
              <tr><th colspan="3" class="text-end">Reservation Fee</th><th class="text-end">${reservationFee}</th></tr>
              <tr><th colspan="3" class="text-end">Total</th><th class="text-end">${preorderTotal}</th></tr>
            </tfoot>
          </table>
          <div class="mt-2">
            <a class="btn btn-outline-primary" href="<c:url value='/orders/pdf'><c:param name='orderId' value='${orderId}'/></c:url>" target="_blank">Print Order</a>
          </div>
        </div>
      </c:if>
      <div class="mt-4">
          <a href="<c:url value='/'/>" class="btn btn-primary">Return to Homepage</a>
      </div>
    </div>
  </main>
  <%@ include file="/WEB-INF/views/includes/footer.jspf" %>
</body>
</html>

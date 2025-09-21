<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Order Confirmation - DineDigital</title>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  </head>
<body class="page-wrapper">
<%@ include file="/WEB-INF/views/includes/nav.jspf" %>
<main class="content-wrap content-section">
<div class="container" style="max-width: 720px;">
  <div class="code-card text-center mx-auto">
    <h2>Thanks! Your order is placed.</h2>
    <p class="mt-2">Please keep this order number handy for billing.</p>
    <div class="code-value fw-bold">#<c:out value='${orderNumber}'/></div>
    <div class="mt-3 text-start">
      <c:if test="${not empty tableNumber}"><p><strong>Table:</strong> ${tableNumber}</p></c:if>
      <c:if test="${reservationId > 0}"><p><strong>Reservation ID:</strong> ${reservationId}</p></c:if>
      <p><strong>Ordered on:</strong> <fmt:formatDate value="${createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
    </div>
    <div class="mt-4 d-flex gap-2 justify-content-center">
      <a class="btn btn-primary" href="<c:url value='/order'/>">Place Another Order</a>
      <a class="btn btn-outline-primary" id="pdfLink" href="${pageContext.request.contextPath}/orders/pdf?orderId=${realOrderId}">Download PDF</a>
      <a class="btn btn-outline-secondary" href="<c:url value='/'/>">Home</a>
    </div>
  </div>
</div>
 </main>
<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
<!-- Toast container -->
<div class="position-fixed bottom-0 end-0 p-3" style="z-index: 1100">
  <div id="orderToast" class="toast align-items-center text-bg-success border-0" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="d-flex">
      <div class="toast-body">
        Order #<c:out value='${orderNumber}'/> placed successfully. Downloading receipt...
      </div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
  </div>
</div>

<script>
  (function(){
    const pdfLink = document.getElementById('pdfLink');
    // Show success toast
    try {
      const toastEl = document.getElementById('orderToast');
      if (toastEl && window.bootstrap && bootstrap.Toast) {
        new bootstrap.Toast(toastEl, { delay: 4000 }).show();
      }
    } catch (e) {}
    // Auto-download PDF
    if (pdfLink && pdfLink.href) {
      const a = document.createElement('a');
      a.href = pdfLink.href;
      a.download = '';
      document.body.appendChild(a);
      a.click();
      setTimeout(() => { try { document.body.removeChild(a); } catch(e){} }, 1000);
    }
  })();
</script>
</body>
</html>

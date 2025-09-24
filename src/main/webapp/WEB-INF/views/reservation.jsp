<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Reservations - DineDigital</title>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
</head>
<body class="page-wrapper">
  <c:set var="page" value="reservation"/>
  <%@ include file="/WEB-INF/views/includes/nav.jspf" %>
  <main class="content-wrap content-section">
    <div class="container">
      <h2 class="text-center mb-5">Reserve Your Table</h2>
      <form class="mx-auto" style="max-width: 700px;" action="<c:url value='/reservation/confirm'/>" method="post">
        <h4 class="mb-4">Your Details</h4>
        <div class="mb-4">
          <label for="name" class="form-label">Full Name</label>
          <input type="text" class="form-control" id="name" name="name" placeholder="Enter your full name" required>
        </div>
        <div class="mb-4">
          <label for="email" class="form-label">Email Address</label>
          <input type="email" class="form-control" id="email" name="email" placeholder="Enter your email" required>
        </div>
        <div class="row">
          <div class="col-md-6 mb-4">
            <label for="date" class="form-label">Date</label>
            <input type="date" class="form-control" id="date" name="date" required>
          </div>
          <div class="col-md-6 mb-4">
            <label for="time" class="form-label">Time</label>
            <input type="time" class="form-control" id="time" name="time" required>
          </div>
        </div>
        <div class="mb-4">
            <div class="d-flex justify-content-between align-items-center">
                <label for="guests-input" class="form-label mb-0">Number of Guests</label>
        <div class="quantity-selector d-flex align-items-center">
          <button type="button" class="btn quantity-btn" id="guest-minus-btn">-</button>
          <span class="quantity-display" id="guest-count">1</span>
          <button type="button" class="btn quantity-btn" id="guest-plus-btn">+</button>
        </div>
            </div>
            <input type="hidden" id="guests-input" name="guests" value="1" required>
        </div>
        <hr class="my-5" style="border-color: var(--border-color);">
        <div>
            <div class="preorder-toggle" data-bs-toggle="collapse" data-bs-target="#preorderCollapse" aria-expanded="false" aria-controls="preorderCollapse">
                <h4>Pre-order Your Meal (Optional)</h4>
                <div class="toggle-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-chevron-down" viewBox="0 0 16 16">
                      <path fill-rule="evenodd" d="M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"/>
                    </svg>
                </div>
            </div>
            <div class="collapse" id="preorderCollapse">
                <div class="pt-3">
                    <div id="preorder-menu-items" class="mb-4">
                      <div class="row g-3">
                        <c:forEach items="${items}" var="it">
                          <div class="col-12">
                            <div class="d-flex align-items-center justify-content-between border rounded p-2 menu-item-row">
                              <div class="d-flex align-items-center" style="gap:12px;">
                                <c:if test="${not empty it.image}">
                                  <img src="${it.image}" alt="${it.name}" style="height:48px;width:64px;object-fit:cover;border-radius:4px;"/>
                                </c:if>
                                <div>
                                  <div class="fw-semibold">${it.name}</div>
                                  <div class="text-muted small">${it.description}</div>
                                </div>
                              </div>
                              <div class="d-flex align-items-center menu-item-meta" style="gap:8px;">
                                <div class="fw-bold">₹ ${it.price}</div>
                                <input type="hidden" name="preorderNames" value="${it.name}"/>
                                <input type="hidden" name="preorderPrices" value="${it.price}"/>
                                <div class="quantity-selector d-flex align-items-center">
                                  <button type="button" class="btn quantity-btn preorder-minus">-</button>
                                  <span class="quantity-display preorder-count">0</span>
                                  <button type="button" class="btn quantity-btn preorder-plus">+</button>
                                  <input type="hidden" name="preorderQtys" class="preorder-qty" value="0"/>
                                </div>
                              </div>
                            </div>
                          </div>
                        </c:forEach>
                      </div>
                    </div>
                    <div class="preorder-total d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Pre-order Total:</h5>
                        <h5 class="mb-0" id="preorder-total-amount">₹ 0.00</h5>
                    </div>
                    <div class="form-text mt-3 mb-4">
                        For our full selection, please
                        <a href="<c:url value='/menu'/>" target="_blank" style="color: var(--primary-accent);">View The Full Menu</a>.
                    </div>
                    <label for="special-requests" class="form-label mt-3">Special Requests</label>
                    <textarea class="form-control" id="special-requests" rows="3" placeholder="Please list any dietary restrictions or special requests here..."></textarea>
                </div>
            </div>
        </div>
        <button type="submit" class="btn btn-primary w-100 mt-5">Confirm Reservation</button>
      </form>
    </div>
  </main>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
  <%@ include file="/WEB-INF/views/includes/footer.jspf" %>
  <script>
    (function(){
      // Disable past dates for reservation
      const dateInput = document.getElementById('date');
      try {
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth()+1).padStart(2,'0');
        const dd = String(today.getDate()).padStart(2,'0');
        const minStr = `${yyyy}-${mm}-${dd}`;
        dateInput.min = minStr;
      } catch(e) {}
      // Guest +/- control
      const guestMinus = document.getElementById('guest-minus-btn');
      const guestPlus = document.getElementById('guest-plus-btn');
      const guestDisplay = document.getElementById('guest-count');
      const guestInput = document.getElementById('guests-input');
      function setGuests(n){
        if(n < 1) n = 1;
        guestDisplay.textContent = n;
        guestInput.value = n;
      }
      guestMinus.addEventListener('click', () => setGuests(parseInt(guestInput.value,10) - 1));
      guestPlus.addEventListener('click', () => setGuests(parseInt(guestInput.value,10) + 1));
      const qtyInputs = document.querySelectorAll('.preorder-qty');
      const totalEl = document.getElementById('preorder-total-amount');
      const form = document.querySelector('form[action*="/reservation/confirm"]');

      function recalc(){
        let total = 0;
        qtyInputs.forEach(q => {
          const row = q.closest('.d-flex');
          const priceInput = row.querySelector('input[name="preorderPrices"]');
          const qty = parseInt(q.value || '0', 10);
          const price = parseFloat(priceInput.value || '0');
          total += qty * price;
        });
        try {
          totalEl.textContent = '₹ ' + total.toFixed(2);
        } catch(e) {
          totalEl.textContent = '₹ ' + total;
        }
      }
      qtyInputs.forEach(i => i.addEventListener('input', recalc));
      // Wire +/- controls for preorder items
      document.querySelectorAll('#preorder-menu-items .quantity-selector').forEach(qs => {
        const minus = qs.querySelector('.preorder-minus');
        const plus = qs.querySelector('.preorder-plus');
        const display = qs.querySelector('.preorder-count');
        const hiddenQty = qs.querySelector('.preorder-qty');
        function setQty(n){
          if(n < 0) n = 0;
          display.textContent = String(n);
          hiddenQty.value = String(n);
          recalc();
        }
        minus.addEventListener('click', () => setQty(parseInt(hiddenQty.value||'0',10) - 1));
        plus.addEventListener('click', () => setQty(parseInt(hiddenQty.value||'0',10) + 1));
      });
      recalc();

      // On submit, validate date and disable zero-qty rows so server ignores them
      form.addEventListener('submit', (e) => {
        // Prevent past date submissions
        try {
          const val = document.getElementById('date').value;
          if (val) {
            const picked = new Date(val + 'T00:00:00');
            const now = new Date();
            const todayMidnight = new Date(now.getFullYear(), now.getMonth(), now.getDate());
            if (picked < todayMidnight) {
              e.preventDefault();
              alert('Please choose today or a future date for your reservation.');
              return;
            }
          }
        } catch(err) {}
        qtyInputs.forEach(q => {
          const qty = parseInt(q.value || '0', 10);
          if(qty === 0){
            const row = q.closest('.d-flex');
            row.querySelectorAll('input[name="preorderNames"], input[name="preorderPrices"], input[name="preorderQtys"]').forEach(x => x.disabled = true);
          }
        });
      });
    })();
  </script>
</body>
</html>

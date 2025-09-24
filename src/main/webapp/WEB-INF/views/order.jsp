<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Order At Table - DineDigital</title>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
</head>
<body class="page-wrapper">
  <c:set var="page" value="order"/>
  <%@ include file="/WEB-INF/views/includes/nav.jspf" %>
  <main class="content-wrap content-section">
    <div class="container">
      <c:if test="${not empty param.error}">
        <div class="alert alert-warning alert-dismissible fade show mt-2" role="alert">
          <c:choose>
            <c:when test="${param.error eq 'noItems'}">
              Please select at least one item (quantity > 0) before placing the order.
            </c:when>
            <c:when test="${param.error eq 'selectItems'}">
              Please select items and set a table number, then try again.
            </c:when>
            <c:otherwise>
              Unable to place order. Please review your selection and try again.
            </c:otherwise>
          </c:choose>
          <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
      </c:if>
      <h2 class="mb-3 text-center">Order At Your Table</h2>
      <div class="row g-4">
        <div class="col-12 col-lg-5">
          <div class="card p-3">
            <h5>Scan Table QR</h5>
            <div id="qr-reader" style="width:100%"></div>
            <div class="mt-2 d-flex align-items-center gap-2 flex-wrap">
              <button class="btn btn-sm btn-outline-primary" id="startScan" type="button">Start Camera</button>
              <button class="btn btn-sm btn-outline-secondary" id="stopScan" type="button" disabled>Stop</button>
              <label class="btn btn-sm btn-outline-secondary mb-0" for="scanFile">Scan From Image</label>
              <input type="file" id="scanFile" accept="image/*" class="d-none" capture="environment"/>
              <span class="text-muted small" id="scanStatus">Allow camera permission when asked.</span>
            </div>
            <div class="text-muted small mt-2">Tip: If camera access is blocked, allow permissions or use manual entry.</div>
            <div class="mt-3">
              <label class="form-label">Manual Table Number</label>
              <div class="input-group">
                <input type="number" min="1" id="manualTable" class="form-control" placeholder="Enter table #">
                <button class="btn btn-primary" id="applyTable">Apply</button>
              </div>
            </div>
            <div class="mt-3">Current table: <strong id="currentTable">-</strong></div>
          </div>
        </div>
        <div class="col-12 col-lg-7">
          <div class="card p-3">
            <h5 class="mb-3">Menu</h5>
            <form id="orderForm" method="post" action="<c:url value='/orders/place'/>">
              <input type="hidden" name="tableNumber" id="tableNumberInput" />
              <div id="menuList" class="row g-3">
                <c:forEach items="${items}" var="it" varStatus="st">
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
                        <input type="hidden" name="names" value="${it.name}"/>
                        <input type="hidden" name="prices" value="${it.price}"/>
                        <div class="quantity-selector d-flex align-items-center">
                          <button type="button" class="btn quantity-btn item-minus">-</button>
                          <span class="quantity-display item-count">0</span>
                          <button type="button" class="btn quantity-btn item-plus">+</button>
                          <input type="hidden" name="qtys" class="item-qty" value="0"/>
                        </div>
                      </div>
                    </div>
                  </div>
                </c:forEach>
              </div>
              <div class="d-flex justify-content-between align-items-center mt-3">
                <div class="text-muted small">Set quantities for items you want.</div>
                <button type="submit" class="btn btn-primary" id="placeOrderBtn" disabled>Place Order</button>
              </div>
            </form>
            <div id="orderSuccess" class="alert alert-success mt-3 d-none">Order placed! Your order number is <strong id="orderNumber"></strong>.</div>
          </div>
        </div>
      </div>
    </div>
  </main>
  <%@ include file="/WEB-INF/views/includes/footer.jspf" %>
  <script src="https://unpkg.com/html5-qrcode@2.3.9/html5-qrcode.min.js"></script>
  <script>
    (function(){
      const tableLabel = document.getElementById('currentTable');
      const tableInput = document.getElementById('tableNumberInput');
      const applyBtn = document.getElementById('applyTable');
      const manual = document.getElementById('manualTable');
      const orderForm = document.getElementById('orderForm');
      const placeBtn = document.getElementById('placeOrderBtn');

      function setCookie(name, value, days){
        const d = new Date();
        d.setTime(d.getTime() + (days*24*60*60*1000));
        document.cookie = name + "=" + encodeURIComponent(value) + ";expires=" + d.toUTCString() + ";path=/";
      }
      function getCookie(name){
        const m = document.cookie.match(new RegExp('(?:^|; )' + name.replace(/([.$?*|{}()\[\]\\\/\+^])/g,'\\$1') + '=([^;]*)'));
        return m ? decodeURIComponent(m[1]) : null;
      }

      function updateTable(t) {
        if(!t || isNaN(t)) return;
        const n = parseInt(t, 10);
        if(n > 0) {
          tableLabel.textContent = n;
          tableInput.value = n;
          // reflect in manual input for visibility and quick edits
          try { document.getElementById('manualTable').value = String(n); } catch(e) {}
          setCookie('DD_TABLE', String(n), 7);
          maybeEnablePlace();
        }
      }

      function maybeEnablePlace(){
        // enable when table set and at least one qty>0
        const anyQty = Array.from(orderForm.querySelectorAll('input[name="qtys"]'))
          .some(i => parseInt(i.value||'0',10) > 0);
        placeBtn.disabled = !(tableInput.value && anyQty);
      }

      // Wire +/- buttons for each item
      orderForm.querySelectorAll('.quantity-selector').forEach(qs => {
        const minus = qs.querySelector('.item-minus');
        const plus = qs.querySelector('.item-plus');
        const display = qs.querySelector('.item-count');
        const hiddenQty = qs.querySelector('.item-qty');
        function setQty(n){
          if(n < 0) n = 0;
          display.textContent = String(n);
          hiddenQty.value = String(n);
          maybeEnablePlace();
        }
        minus.addEventListener('click', () => setQty(parseInt(hiddenQty.value||'0',10) - 1));
        plus.addEventListener('click', () => setQty(parseInt(hiddenQty.value||'0',10) + 1));
      });

      applyBtn.addEventListener('click', () => updateTable(manual.value));

      // QR parsing: expect absolute/relative URL with ?table=XX or just raw number
      function parseTableFromText(txt){
        try {
          // Support absolute or relative URLs by providing base
          const url = new URL(txt, window.location.origin);
          const t = url.searchParams.get('table');
          return t || undefined;
        } catch(e) {
          // not a URL, maybe just number
          return txt.match(/^\d+$/) ? txt : undefined;
        }
      }

      const qrDivId = 'qr-reader';
      const qr = new Html5Qrcode(qrDivId);
      const config = { fps: 10, qrbox: { width: 250, height: 250 } };
      const startBtn = document.getElementById('startScan');
      const stopBtn = document.getElementById('stopScan');
      const scanStatus = document.getElementById('scanStatus');
      let running = false;

      async function startCamera() {
        try {
          scanStatus.textContent = 'Requesting camera permission...';
          const cams = await Html5Qrcode.getCameras();
          // Prefer back/environment camera on mobile; otherwise last camera
          let camId;
          if (cams && cams.length) {
            const back = cams.find(c => /back|rear|environment/i.test(c.label||''));
            camId = (back ? back.id : cams[cams.length-1].id);
          }
          if (!camId) { scanStatus.textContent = 'No camera found.'; return; }
          await qr.start(camId, config, (decodedText) => {
            const t = parseTableFromText(decodedText);
            if (t) updateTable(t);
          }, (err) => { /* ignore scan errors */ });
          running = true;
          startBtn.disabled = true; stopBtn.disabled = false;
          scanStatus.textContent = 'Scanning...';
        } catch (e) {
          scanStatus.textContent = 'Camera permission denied or unavailable.';
        }
      }

      async function stopCamera() {
        if (!running) return;
        try { await qr.stop(); } catch(e) {}
        running = false; startBtn.disabled = false; stopBtn.disabled = true;
        scanStatus.textContent = 'Scanner stopped.';
      }

      startBtn.addEventListener('click', startCamera);
      stopBtn.addEventListener('click', stopCamera);

      // Stop camera when page hidden (saves battery, releases camera)
      document.addEventListener('visibilitychange', () => { if (document.hidden) { stopCamera(); } });

      // Scan still image fallback
      document.getElementById('scanFile').addEventListener('change', async (ev) => {
        const f = ev.target.files && ev.target.files[0];
        if (!f) return;
        scanStatus.textContent = 'Scanning image...';
        try {
          const result = await qr.scanFile(f, true);
          const t = parseTableFromText(result);
          if (t) updateTable(t);
          scanStatus.textContent = 'Image scanned.';
        } catch(e) {
          scanStatus.textContent = 'Could not read QR from image.';
        } finally {
          ev.target.value = '';
        }
      });

  // Prefill from cookie or URL param
      (function initTable(){
        const params = new URLSearchParams(location.search);
        const tParam = params.get('table');
        if (tParam) { updateTable(tParam); return; }
        const cookieVal = getCookie('DD_TABLE');
        if (cookieVal) { updateTable(cookieVal); return; }
        // Fallback: some venues encode table directly as the path fragment /order/12
        const m = location.pathname.match(/\/order\/(\d+)/);
        if (m && m[1]) { updateTable(m[1]); }
      })();

      // form submit: convert zero-qty rows to not submit by clearing matching name/price
      orderForm.addEventListener('submit', (e) => {
        const rows = orderForm.querySelectorAll('input[name="qtys"]');
        rows.forEach((qtyInput) => {
          const qty = parseInt(qtyInput.value||'0',10);
          if (qty === 0) {
            // clear corresponding preceding hidden inputs in the same row
            const row = qtyInput.closest('.d-flex');
            if(row){
              row.querySelectorAll('input[name="names"], input[name="prices"]').forEach(x => x.disabled = true);
              qtyInput.disabled = true;
            }
          }
        });
      });

    })();
  </script>
</body>
</html>

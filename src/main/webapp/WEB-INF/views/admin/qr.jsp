<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>QR Generator - DineDigital</title>
  <%@ include file="/WEB-INF/views/includes/head.jspf" %>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
</head>
<body class="page-wrapper">
<%@ include file="/WEB-INF/views/includes/nav.jspf" %>
<main class="content-wrap content-section">
  <div class="container">
    <nav aria-label="breadcrumb">
      <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="<c:url value='/admin'/>">Admin</a></li>
        <li class="breadcrumb-item active" aria-current="page">QR Codes</li>
      </ol>
    </nav>
    <div class="card card-accent-top p-3" style="max-width: 1000px; margin: 0 auto;">
      <h2 class="accent-heading">Generate Table QR Codes</h2>
      <p class="text-muted">Each QR encodes a link to the order page with a prefilled table number.</p>
      <div class="row g-3 align-items-end">
        <div class="col-12 col-md-5">
          <label class="form-label">Base URL</label>
          <input id="baseUrl" class="form-control" type="text" value="<c:url value='/order'/>"/>
        </div>
        <div class="col-6 col-md-2">
          <label class="form-label">From</label>
          <input id="from" class="form-control" type="number" min="1" value="1"/>
        </div>
        <div class="col-6 col-md-2">
          <label class="form-label">To</label>
          <input id="to" class="form-control" type="number" min="1" value="10"/>
        </div>
        <div class="col-12 col-md-3">
          <button id="gen" class="btn btn-primary w-100">Generate</button>
        </div>
      </div>
    <div id="qrs" class="qr-grid mt-3"></div>
      <div class="mt-3 d-flex justify-content-between align-items-center">
        <div id="statusMsg" class="small text-muted"></div>
        <div class="d-flex gap-2">
      <button id="downloadZip" class="btn btn-outline-primary">Download ZIP (PNGs)</button>
        </div>
      </div>
    </div>
  </div>
</main>
<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
<!-- No client QR libs needed; using server-side PNGs via /admin/qr/png and /admin/qr/zip -->
<script>
  const out = document.getElementById('qrs');
  const genBtn = document.getElementById('gen');
  const zipBtn = document.getElementById('downloadZip');
  const status = document.getElementById('statusMsg');
  function setStatus(msg){ status.textContent = msg || ''; }
  function disableWhile(btn, fn){
    return async function(){
      try { btn.disabled = true; setStatus('Working...'); await fn(); setStatus(''); }
      catch(e){ console.error(e); setStatus('An error occurred. Please try again.'); }
      finally { btn.disabled = false; }
    };
  }
  genBtn.addEventListener('click', disableWhile(genBtn, async () => {
    out.innerHTML = '';
    const base = document.getElementById('baseUrl').value || '/order';
    const a = parseInt(document.getElementById('from').value||'1',10);
    const b = parseInt(document.getElementById('to').value||'1',10);
    if(isNaN(a) || isNaN(b) || a < 1 || b < 1){ setStatus('Enter a valid range (>=1).'); return; }
    const from = Math.min(a,b), to = Math.max(a,b);
    const count = to - from + 1;
    if (count > 500){ setStatus('Range too large. Please generate at most 500 at a time.'); return; }
    let ok = 0;
    for(let t=from;t<=to;t++){
      let abs;
      try{ abs = new URL(base, window.location.origin); }catch(e){ setStatus('Invalid Base URL'); return; }
      abs.searchParams.set('table', String(t));
      const wrap = document.createElement('div');
      wrap.className = 'qr-item';
  const img = document.createElement('img');
      img.width = 160; img.height = 160; img.alt = `Table ${t}`;
  const qs = new URLSearchParams({ text: abs.toString(), size: '160' }).toString();
  img.src = `<c:url value='/admin/qr/png'/>?${qs}`;
      wrap.appendChild(img);
      const label = document.createElement('div');
      label.textContent = 'Table ' + t;
      label.style.marginTop = '8px';
      wrap.appendChild(label);
      out.appendChild(wrap);
      ok++;
      if (ok % 25 === 0) setStatus(`Rendered ${ok}/${count}...`);
    }
    setStatus(`Rendered ${count} QR codes.`);
  }));

  zipBtn.addEventListener('click', disableWhile(zipBtn, async () => {
    const base = document.getElementById('baseUrl').value || '/order';
    const a = parseInt(document.getElementById('from').value||'1',10);
    const b = parseInt(document.getElementById('to').value||'1',10);
    if(isNaN(a) || isNaN(b) || a < 1 || b < 1){ setStatus('Enter a valid range (>=1).'); return; }
    const from = Math.min(a,b), to = Math.max(a,b);
  const absBase = new URL(base, window.location.origin).toString();
  const qs = new URLSearchParams({ baseUrl: absBase, from: String(from), to: String(to), size: '160' }).toString();
  const link = document.createElement('a');
  link.href = `<c:url value='/admin/qr/zip'/>?${qs}`;
    link.download = 'table-qr-codes.zip';
    document.body.appendChild(link);
    link.click();
    link.remove();
    setStatus('Preparing ZIP download...');
  }));
</script>
</body>
</html>

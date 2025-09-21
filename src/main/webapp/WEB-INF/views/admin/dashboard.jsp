<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Admin Dashboard - DineDigital</title>
    <%@ include file="/WEB-INF/views/includes/head.jspf" %>
</head>
<body class="page-wrapper">
<%@ include file="/WEB-INF/views/includes/nav.jspf" %>
<main class="content-wrap content-section">
<div class="container">
        <div class="mb-4">
            <h1 class="mb-1 accent-heading">Admin Dashboard</h1>
            <p class="text-muted">Manage the restaurant content and operations from one place.</p>
        </div>

        <div class="row g-4">
            <div class="col-12 col-md-6 col-lg-4">
                <a class="text-decoration-none" href="<c:url value='/admin/menu'/>">
                    <div class="card card-accent-top admin-tile h-100 p-3">
                        <div class="d-flex align-items-start gap-3">
                            <i class="bi bi-card-checklist fs-3" style="color: var(--primary-accent);"></i>
                            <div>
                                <h5 class="mb-1">Manage Menu</h5>
                                <p class="mb-0 text-muted">Add, edit, or remove dishes and prices.</p>
                            </div>
                        </div>
                    </div>
                </a>
            </div>

            <div class="col-12 col-md-6 col-lg-4">
                <a class="text-decoration-none" href="<c:url value='/admin/reservations'/>">
                    <div class="card card-accent-top admin-tile h-100 p-3">
                        <div class="d-flex align-items-start gap-3">
                            <i class="bi bi-calendar2-check fs-3" style="color: var(--primary-accent);"></i>
                            <div>
                                <h5 class="mb-1">Reservations</h5>
                                <p class="mb-0 text-muted">View bookings and check-in by code.</p>
                            </div>
                        </div>
                    </div>
                </a>
            </div>

            <div class="col-12 col-md-6 col-lg-4">
                <a class="text-decoration-none" href="<c:url value='/kitchen'/>">
                    <div class="card card-accent-top admin-tile h-100 p-3">
                        <div class="d-flex align-items-start gap-3">
                            <i class="bi bi-egg-fried fs-3" style="color: var(--primary-accent);"></i>
                            <div>
                                <h5 class="mb-1">Kitchen Board</h5>
                                <p class="mb-0 text-muted">Track and complete in-restaurant orders.</p>
                            </div>
                        </div>
                    </div>
                </a>
            </div>

            <div class="col-12 col-md-6 col-lg-4">
                <a class="text-decoration-none" href="<c:url value='/admin/billing'/>">
                    <div class="card card-accent-top admin-tile h-100 p-3">
                        <div class="d-flex align-items-start gap-3">
                            <i class="bi bi-receipt fs-3" style="color: var(--primary-accent);"></i>
                            <div>
                                <h5 class="mb-1">Billing</h5>
                                <p class="mb-0 text-muted">Lookup orders and download invoices.</p>
                            </div>
                        </div>
                    </div>
                </a>
            </div>

            

            <div class="col-12 col-md-6 col-lg-4">
                <a class="text-decoration-none" href="<c:url value='/admin/users'/>">
                    <div class="card card-accent-top admin-tile h-100 p-3">
                        <div class="d-flex align-items-start gap-3">
                            <i class="bi bi-people fs-3" style="color: var(--primary-accent);"></i>
                            <div>
                                <h5 class="mb-1">Users</h5>
                                <p class="mb-0 text-muted">Manage admin and kitchen accounts.</p>
                            </div>
                        </div>
                    </div>
                </a>
            </div>
        </div>

        <div class="mt-4 d-flex">
            <form class="ms-auto" method="post" action="<c:url value='/auth/logout'/>">
                <button class="btn btn-outline-secondary" type="submit">Logout</button>
            </form>
        </div>
</div>
</main>
<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
</body>
</html>

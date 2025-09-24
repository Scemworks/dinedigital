<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Reservations - DineDigital</title>
    <%@ include file="/WEB-INF/views/includes/head.jspf" %>
</head>
<body class="page-wrapper">
<%@ include file="/WEB-INF/views/includes/nav.jspf" %>
<main class="content-wrap content-section">
<div class="container">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="<c:url value='/admin'/>">Admin</a></li>
                <li class="breadcrumb-item active" aria-current="page">Reservations</li>
            </ol>
        </nav>
        <h1 class="accent-heading">Reservations</h1>

    <form class="row g-2 align-items-end card card-accent-top p-3" method="post" action="<c:url value='/admin/reservations/checkin'/>">
                <div class="col-auto">
                    <label class="form-label">Check-in by Confirmation Code</label>
                    <input class="form-control" type="text" name="code" placeholder="ABC12345" required/>
                </div>
                <div class="col-auto">
                    <label class="form-label">Assign Table</label>
                    <input class="form-control" type="number" name="tableNumber" min="1" placeholder="e.g. 12" />
                </div>
                <div class="col-auto">
                    <button class="btn btn-primary" type="submit">Check In</button>
                </div>
        </form>

    <table class="table table-dark table-striped table-bordered" style="margin-top:1rem;">
        <thead class="table-dark">
        <tr>
            <th>ID</th><th>Name</th><th>Email</th><th>Date</th><th>Time</th><th>Guests</th><th>Code</th><th>Checked In</th><th>Table</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${reservations}" var="r">
            <tr>
                <td>${r.id}</td>
                <td>${r.name}</td>
                <td>${r.email}</td>
                <td>${r.date}</td>
                <td>${r.time}</td>
                <td>${r.guests}</td>
                <td>${r.confirmationCode}</td>
                <td><c:choose><c:when test="${r.checkedIn}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose></td>
                <td><c:out value="${r.tableNumber != null ? r.tableNumber : '—'}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <p class="mt-3"><a class="btn btn-outline-secondary" href="<c:url value='/admin'/>">Back</a></p>
</div>
</main>
<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
</body>
</html>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Users - DineDigital</title>
    <%@ include file="/WEB-INF/views/includes/head.jspf" %>
</head>
<body class="page-wrapper">
<%@ include file="/WEB-INF/views/includes/nav.jspf" %>
<main class="content-wrap content-section">
<div class="container">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="<c:url value='/admin'/>">Admin</a></li>
                <li class="breadcrumb-item active" aria-current="page">Users</li>
            </ol>
        </nav>
        <h1 class="accent-heading">Users</h1>
    <table class="table table-dark table-striped table-bordered align-middle">
        <thead class="table-dark">
        <tr><th>ID</th><th>Username</th><th>Role</th><th>Actions</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${users}" var="u">
            <tr>
                <td>${u.id}</td>
                <td>${u.username}</td>
                <td>${u.role}</td>
                <td>
                    <form class="d-inline" method="post" action="<c:url value='/admin/users/delete'/>">
                        <input type="hidden" name="id" value="${u.id}"/>
                        <button class="btn btn-sm btn-outline-danger" type="submit">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <h2 class="mt-4 accent-heading">Add User</h2>
    <form class="card card-accent-top p-3" method="post" action="<c:url value='/admin/users/add'/>">
        <label class="form-label">Username</label>
        <input class="form-control" type="text" name="username" required/>
        <label class="form-label">Password</label>
        <input class="form-control" type="password" name="password" required/>
        <label class="form-label">Role</label>
        <select class="form-select" name="role" required>
            <option value="ADMIN">ADMIN</option>
            <option value="KITCHEN">KITCHEN</option>
        </select>
        <button class="btn btn-success mt-3" type="submit">Create</button>
    </form>
    <p class="mt-3"><a class="btn btn-outline-secondary" href="<c:url value='/admin'/>">Back</a></p>
</div>
</main>
<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
</body>
</html>

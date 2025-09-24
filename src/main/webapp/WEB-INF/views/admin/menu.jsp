<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Manage Menu - DineDigital</title>
    <%@ include file="/WEB-INF/views/includes/head.jspf" %>
</head>
<body class="page-wrapper">
<%@ include file="/WEB-INF/views/includes/nav.jspf" %>
<main class="content-wrap content-section">
<div class="container">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="<c:url value='/admin'/>">Admin</a></li>
                <li class="breadcrumb-item active" aria-current="page">Menu</li>
            </ol>
        </nav>
        <h1 class="accent-heading">Menu</h1>
    <div class="table-responsive">
    <table class="table table-dark table-striped table-bordered align-middle">
        <thead class="table-dark">
        <tr><th>ID</th><th>Name</th><th>Description</th><th>Price</th><th>Image</th><th style="width:160px">Actions</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${items}" var="it">
            <tr>
                <form method="post" action="<c:url value='/admin/menu/update'/>">
                    <td>${it.id}<input type="hidden" name="id" value="${it.id}"/></td>
                    <td><input class="form-control" type="text" name="name" value="${it.name}"/></td>
                    <td><input class="form-control" type="text" name="description" value="${it.description}"/></td>
                    <td><input class="form-control" type="number" step="0.01" name="price" value="${it.price}"/></td>
                    <td>
                        <input class="form-control" type="url" name="image" value="${it.image}"/>
                        <c:if test="${not empty it.image}"><br/><img src="${it.image}" alt="img" style="height:40px"/></c:if>
                    </td>
                    <td style="white-space:nowrap;">
                        <button class="btn btn-sm btn-primary" type="submit">Save</button>
                        </form>
                        <form method="post" action="<c:url value='/admin/menu/delete'/>">
                            <input type="hidden" name="id" value="${it.id}"/>
                            <button class="btn btn-sm btn-outline-danger" type="submit">Delete</button>
                        </form>
                    </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>

    <h2 class="mt-4 accent-heading">Add Item</h2>
    <form class="card card-accent-top p-3" method="post" action="<c:url value='/admin/menu/add'/>">
        <div>
            <label class="form-label text-white">Name</label>
            <input class="form-control" type="text" name="name" required/>
        </div>
        <div>
            <label class="form-label text-white">Description</label>
            <input class="form-control" type="text" name="description"/>
        </div>
        <div>
            <label class="form-label text-white">Price</label>
            <input class="form-control" type="number" step="0.01" name="price" required/>
        </div>
        <div>
            <label class="form-label text-white">Image URL</label>
            <input class="form-control" type="url" name="image"/>
        </div>
        <button class="btn btn-success mt-3" type="submit">Add</button>
    </form>
    <p class="mt-3"><a class="btn btn-outline-secondary" href="<c:url value='/admin'/>">Back</a></p>
</div>
</main>
<%@ include file="/WEB-INF/views/includes/footer.jspf" %>
</body>
</html>

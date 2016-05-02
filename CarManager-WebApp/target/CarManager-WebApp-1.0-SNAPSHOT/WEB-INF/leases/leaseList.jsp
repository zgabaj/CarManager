
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Pozicky</title>
    </head>
    <body>
        <% String message = (String) request.getAttribute("nedostupne");%>

        <h1>Pozicane auta</h1>
        <table >
            <thead>
                <tr>
                    <th>SPZ</th>
                    <th>Znacka</th>
                    <th>Datum od</th>
                    <th>Datum do</th>
                    <th>Meno</th>
                </tr>
            </thead>
            <c:forEach items="${leases}" var="lease">
                <tr>
                    <c:forEach items="${cars}" var="car">
                        <c:if test="${car.id == lease.carId}">
                            <td><c:out value="${car.licencePlate}"/></td>
                            <td><c:out value="${car.brand}"/></td>
                        </c:if>
                    </c:forEach>
                    <td><c:out value="${lease.startDate}"/></td>
                    <td><c:out value="${lease.endDate}"/></td>
                    <td><c:out value="${lease.customerFullName}"/></td>              
                    <td><form method="post" action="${pageContext.request.contextPath}/Leases/delete?id=${lease.id}"
                              style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>
                </tr>
            </c:forEach>
        </table>
        <h1>Nova pozicka</h1>
        <c:if test="${not empty unavailable}">
            <div style="border: solid 1px black;  background-color:#FF9900; padding: 5px">
                <c:out value="${unavailable}"/>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div style="border: solid 1px black;  background-color:#FF9900; padding: 5px">
                <c:out value="${error}"/>
            </div>
        </c:if>
        <c:if test="${not empty date}">
            <div style="border: solid 1px black;  background-color:#FF9900; padding: 5px">
                <c:out value="${date}"/>
            </div>
        </c:if>
        <form name="leases" action="${pageContext.request.contextPath}/Leases/add" method="post">
            <table>
                <tr>
                    <th>Datum od:</th>
                    <td><input type="date" name="startDate" value="<c:out value='${param.startDate}'/>"/></td>
                </tr>
                <tr>
                    <th>Datum do:</th>
                    <td><input type="date" name="endDate" value="<c:out value='${param.endDate}'/>"/></td>
                </tr>
                <tr>
                    <th>Meno</th>
                    <td><input type="text" name="customerFullName" value="<c:out value='${param.customerFullName}'/>"/></td>
                </tr>
                <tr>
                    <th>Auto:</th>
                    <td>
                        <select name="carId">
                            <c:forEach items="${cars}" var="car">
                                <option value="<c:out value="${car.id}"/>">
                                    <c:out value="${car.licencePlate}"/>
                                    <c:out value="${car.brand}"/>
                                </option>
                            </c:forEach>
                        </select>

                    </td>
                </tr>
            </table>
            <input type="Submit" value="Zadat" />

 
    </body></html>

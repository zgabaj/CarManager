<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Auta</title>
    </head>
    <body>
        <h1>Auta</h1>
        <table >
            <thead>
                <tr>
                    <th>SPZ</th>
                    <th>Znacka</th>
                    <th></th>
                    <th>Nova SPZ</th>
                    <th></th>
                    <th>Nova Znacka</th>
                    <th></th>

                </tr>
            </thead>
            <c:forEach items="${cars}" var="car">
                <tr>
                    <td><c:out value="${car.licencePlate}"/></td>
                    <td><c:out value="${car.brand}"/></td>
                    <td><form method="post" action="${pageContext.request.contextPath}/Cars/delete?id=${car.id}"
                              style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>
                <form method="post" 
                      action="${pageContext.request.contextPath}/Cars/update/licencePlate?id=${car.id}"
                      style="margin-bottom: 0;">
                    <td>
                        <input type="text" name="newLicencePlate" value="<c:out value='${param.newLicencePlate}'/>"/>
                    </td>
                    <td>
                        <input type="submit" value="Upravit">
                    </td>
                </form>
                <form method="post" 
                      action="${pageContext.request.contextPath}/Cars/update/brand?id=${car.id}"
                      style="margin-bottom: 0;">
                    <td>
                        <select name="newBrand">
                            <option value="BMW" >BMW</option>
                            <option value="MERCEDES_BENZ">Mercedes Benz</option>
                            <option value="SKODA">Skoda</option>
                            <option value="VOLGA">Volga</option>
                            <option value="MOSKVIC">Moskovic</option>
                            <option value="KIA">Kia</option>
                            <option value="TATRA">Tatra</option>
                            <option value="VARBURG">Varburg</option>
                        </select>
                    </td>
                    <td>
                        <input type="submit" value="Upravit">
                    </td>
                </form>

            </tr>
        </c:forEach>
    </table>
    <h1>Zadajte nove Auto:</h1>
    <c:if test="${not empty wrongPlate}">
        <div style="border: solid 1px black;  background-color:#FF9900; padding: 5px">
            <c:out value="${wrongPlate}"/>
            <form method="get" style="margin-bottom: 0;">
                <input type="submit" value="OK">
            </form> 
        </div>



    </c:if>
    <c:if test="${not empty noPlate}">
        <div style="border: solid 1px black;  background-color:#FF9900; padding: 5px">
            <c:out value="${noPlate}"/>
            <form method="get" style="margin-bottom: 0;">
                <input type="submit" value="OK">
            </form>
        </div>
    </c:if>
    <form name="cars" action="${pageContext.request.contextPath}/Cars/add" method="post">
        <table>
            <tr>
                <th>SPZ:</th>
                <td><input type="text" name="licencePlate" value="<c:out value='${param.licencePlate}'/>"/></td>
            </tr>
            <tr>
                <th>Znacka:</th>
                <td>
                    <select name="brand">
                        <option value="BMW" >BMW</option>
                        <option value="MERCEDES_BENZ">Mercedes Benz</option>
                        <option value="SKODA">Skoda</option>
                        <option value="VOLGA">Volga</option>
                        <option value="MOSKVIC">Moskovic</option>
                        <option value="KIA">Kia</option>
                        <option value="TATRA">Tatra</option>
                        <option value="VARBURG">Varburg</option>
                    </select>
                </td>
            </tr>
        </table>
        <input type="Submit" value="Zadat"/>
</body>
</html>

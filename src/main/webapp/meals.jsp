<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <style>
        table {
            border: thin solid black;
            border-collapse: collapse;
        }

        td, th {
            border: thin solid black;
            padding: 5px;
        }

        .green {
            color: green;
        }

        .red {
            color: red;
        }
    </style>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<table>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
    </tr>
    <c:forEach items="${meals}" var="meal">
        <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
        <tr class="${meal.excess ? 'red' : 'green'}">
            <td><javatime:format value="${meal.dateTime}" pattern="yyyy-MM-dd HH:mm"/></td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
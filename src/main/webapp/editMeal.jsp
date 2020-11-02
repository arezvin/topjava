<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Edit Meal</title>
    <style>
        input {
            padding: 5px;
            margin: 5px;
        }
    </style>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit Meal</h2>
<form method="post" action="meals">
    <input type="hidden" name="mealId" value="<c:out value="${meal.id}"/>"/>
    Date: <input type="datetime-local" name="dateTime" value="<c:out value="${meal.dateTime}"/>"/><br/>
    Description: <input type="text" name="description" value="<c:out value="${meal.description}"/>"/><br/>
    Calories: <input type="number" name="calories" value="<c:out value="${meal.calories}"/>"/><br/>
    <input type="submit" value="Submit"/>
</form>
</body>
</html>
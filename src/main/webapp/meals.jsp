<%--
  Created by IntelliJ IDEA.
  User: vladimirvolkov
  Date: 2020-02-11
  Time: 23:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
    <head>
        <title>Meals</title>
        <link href="style.css" rel="stylesheet">
    </head>
    <body>
    <h2 align="center">Home work 1</h2>
    <div class="block">
        <button class="insert">
            <img src="https://img.icons8.com/officel/30/000000/paste.png">
        </button>
    <table class="white">
        <tr>
            <th>Дата ввода</th>
            <th>Описание</th>
            <th>Калории</th>
            <th colspan="2">Опции</th>
        </tr>
    <c:forEach items="${meals}" var="meal">
        <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo" scope="page"/>
        <fmt:parseDate value="${meal.dateTime}" pattern="y-M-dd'T'H:m" var="parseDate"/>
        <tr <c:if test="${meal.excess}">class="red" </c:if> >
            <td><fmt:formatDate value="${parseDate}" pattern="yyyy.MM.dd HH:mm"/></td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td style="text-align: center"><a href="meals?action=update&id=${meal.id}"><img src="https://img.icons8.com/officel/20/000000/edit.png"></a></td>
            <td style="text-align: center"><a href="meals?action=delete&id=${meal.id}"><img src="https://img.icons8.com/officel/20/000000/delete.png"></a></td>
        </tr>
    </c:forEach>
    </table>
    </div>
    <div class="b-container">
    </div>
    <div class="b-popup <c:if test="${mealUp== null}">b-popup-on</c:if>">
        <c:if test="${mealUp!=null}"><jsp:useBean id="mealUp" type="ru.javawebinar.topjava.model.MealTo" scope="request"/></c:if>
        <div class="b-popup-content">
            <form method="post"
                  action="meals?action=<c:choose><c:when test="${mealUp!=null}">update&<c:out value="id=${mealUp.id}"/></c:when><c:otherwise>save</c:otherwise></c:choose>">
                <table>
                    <tr>
                        <th>Дата ввода</th>
                        <th>Описание</th>
                        <th>Калории</th>
                    </tr>
                    <tr>
                        <td><input class="in" type="datetime-local" name="datetime" value=<c:if test="${mealUp!=null}">${mealUp.dateTime}</c:if>></td>
                        <td><input class="in" type="text" name="description" value="<c:if test="${mealUp!=null}"><c:out value="${mealUp.description}"/></c:if>"/></td>
                        <td><input class="in-num" type="number" name="calories" value=<c:if test="${mealUp!=null}">${mealUp.calories}</c:if>></td>
                    </tr>
                </table>
                <input type="submit" value="Сохранить" id="Save" name="Save" class="button">
                <input type="submit" value="Отменить" id="Cancel" name="Cancel" class="button">
            </form>
        </div>
    </div>
    </body>
    <script src="meal-action.js"></script>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setBundle basename="messages.app"/>
<html>
<head>
    <title><fmt:message key="meal.title"/></title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<section>
    <h3><a href="index.html"><fmt:message key="app.home"/></a></h3>
    <hr>
    <h2><c:choose>
        <c:when test="${action == 'create'}">
            <fmt:message key="meal.create_meal"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="meal.update_meal"/>
        </c:otherwise>
    </c:choose></h2>
    <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
    <form method="post" action="meals">
        <input type="hidden" name="id" value="${meal.id}">
        <dl>
            <dt><fmt:message key="meal.date"/></dt>
            <dd><input type="datetime-local" value="${meal.dateTime}" name="dateTime" required></dd>
        </dl>
        <dl>
            <dt><fmt:message key="meal.description"/></dt>
            <dd><input type="text" value="${meal.description}" size=40 name="description" required></dd>
        </dl>
        <dl>
            <dt><fmt:message key="meal.calories"/></dt>
            <dd><input type="number" value="${meal.calories}" name="calories" required></dd>
        </dl>
        <button type="submit" formaction="<c:url value="/meals"/>"><fmt:message key="meal.save"/></button>
        <button onclick="window.history.back()" type="button"><fmt:message key="meal.cancel"/></button>
    </form>
</section>
</body>
</html>

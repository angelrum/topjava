<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<fmt:setBundle basename="messages.app"/>
<html>
<head>
    <title><fmt:message key="meal.title"/></title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<section>
    <h3><a href="index.jsp"><fmt:message key="app.home"/></a></h3>
    <hr/>
    <h2><fmt:message key="meal.title"/></h2>
    <form method="get" action="meals">
        <input type="hidden" name="action" value="filter">
        <dl>
            <dt><fmt:message key="meal.from_date"/></dt>
            <dd><input type="date" name="startDate" value="${param.startDate}"></dd>
        </dl>
        <dl>
            <dt><fmt:message key="meal.to_date"/></dt>
            <dd><input type="date" name="endDate" value="${param.endDate}"></dd>
        </dl>
        <dl>
            <dt><fmt:message key="meal.from_time"/></dt>
            <dd><input type="time" name="startTime" value="${param.startTime}"></dd>
        </dl>
        <dl>
            <dt><fmt:message key="meal.to_time"/></dt>
            <dd><input type="time" name="endTime" value="${param.endTime}"></dd>
        </dl>
        <button type="submit"><fmt:message key="meal.filter"/></button>
    </form>
    <hr/>
    <a href="meals/create"><fmt:message key="meal.add"/></a>
    <br><br>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th><fmt:message key="meal.date"/></th>
            <th><fmt:message key="meal.description"/></th>
            <th><fmt:message key="meal.calories"/></th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <c:forEach items="${meals}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
            <tr data-mealExcess="${meal.excess}">
                <td>
                        <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                        <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                        <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                        ${fn:formatDateTime(meal.dateTime)}
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td><a href="meals/update?id=${meal.id}"><fmt:message key="meal.update"/></a></td>
                <td><a href="meals/delete?id=${meal.id}"><fmt:message key="meal.delete"/></a></td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>
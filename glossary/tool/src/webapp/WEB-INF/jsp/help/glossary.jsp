<%@ include file="/WEB-INF/jsp/include.jsp" %>
<html>
<bead>
</head>
<body>
<h3>Glossary</h3>

<c:forEach var="entry" items="${glossary}">
<a name="<c:out value="${entry.term}"/>"/>
<p><b><c:out value="${entry.term}"/></b> - <c:out value="${entry.description}"/></p>
</c:forEach>

</body>
</html>
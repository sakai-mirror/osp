<%@ include file="/WEB-INF/jsp/include.jsp"%>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.glossary.bundle.Messages"/>

<html>
<head>
</head>
<body>
<h3><c:out value="${entry.term}" /></h3>
<c:out value="${entry.longDescription}" escapeXml="false" /> <br />
<br />
<form>
<p class="act">
<input type="button" name="Close" value='<fmt:message key="button.close"/>'
    onclick="window.close()">
</p>
</form>
</body>
</html>

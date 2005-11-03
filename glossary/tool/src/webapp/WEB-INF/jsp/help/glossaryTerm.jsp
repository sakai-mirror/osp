<%@ include file="/WEB-INF/jsp/include.jsp"%>
<html>
<head>
</head>
<body>
<h3><c:out value="${entry.term}" /></h3>
<c:out value="${entry.longDescription}" escapeXml="false" /> <br />
<br />
<form>
<p class="act">
<input type="button" name="Close" value="close"
    onclick="window.close()">
</p>
</form>
</body>
</html>

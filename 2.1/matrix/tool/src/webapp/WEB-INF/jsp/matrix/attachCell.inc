<!-- attach cell -->
<%
Object cell = request.getSession().getAttribute("org_theospi_matrix_attachCell");
request.setAttribute("org_theospi_matrix_attachCell", cell);
%>

<c:if test="${not empty org_theospi_matrix_attachCell}" >
<osp-h:glossary link="true" hover="true">
<table width="650" border="0" height="41" cellpadding="0" cellspacing="0">
  <tr> 
    <td height="20" valign="middle" width="47" align="center">&nbsp;</td>
    <td height="20" valign="middle" width="603" align="left"><font face="Verdana, Arial, Helvetica, sans-serif" size="2">Select 
      a file or files you wish to attach:</font></td>
  </tr>
  <tr> 
    <td height="20" valign="middle" align="right" width="47"><font face="Verdana, Arial, Helvetica, sans-serif" size="2">&nbsp;</font></td>
    <td height="20" valign="middle" align="left" width="603"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"> 
      <b>CIG: </b><c:out value="${org_theospi_matrix_attachCell.matrix.matrixTool.scaffolding.title}"/></font></td>
  </tr>
  <tr> 
    <td height="22" valign="middle" align="right" width="47"><font face="Verdana, Arial, Helvetica, sans-serif" size="2">&nbsp;</font></td>    
    <td height="22" valign="middle" align="left" width="603"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"><b>Criteria:</b> 
      <c:out value="${org_theospi_matrix_attachCell.scaffoldingCell.rootCriterion.description}"/></font></td>
  </tr>
  <tr> 
    <td height="21" valign="middle" align="right" width="47">&nbsp;</td>
    <td height="21" valign="middle" align="left" width="603"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"><b>Level: 
      </b><c:out value="${org_theospi_matrix_attachCell.scaffoldingCell.level.description}"/></font></td>
  </tr>
  <tr> 
    <td height="21" valign="middle" align="right" width="47"><font face="Verdana, Arial, Helvetica, sans-serif" size="2">&nbsp;</font></td>
    <td height="21" valign="middle" align="left" width="603"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"><br>
      </font></td>
  </tr>
</table>
</osp-h:glossary>
</c:if>



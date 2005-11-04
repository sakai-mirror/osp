<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ include file="attachCell.inc" %>

<fieldset>
  <h3>Attach Item</h3>
  
  <form  method="POST">
    <osp:form/>
  
    <table width="100%">
      <tr>
        <td><span class="reqStarInline">*</span><span class="instruction">
          File/Folder to attach</span>
        </td>
        <td>
          <input type="text" name="nodeName" disabled="true" id="nodeName" value="" />
          <spring:bind path="form.node_id">
            <input type="hidden" name="node_id" id="node_id" value="<c:out value="${status.value}"/>"/>
            <span class="error_message"><c:out value="${status.errorMessage}"/></span>
          </spring:bind> 
  
		  <script language="javascript">
			var pickerId = createFilePickerLink('<osp:url value="filePicker.osp"/>', '', 'attachFilePicker', 
			'cell_id=<c:out value="${org_theospi_matrix_attachCell.scaffoldingCell.id}"/>',
			'node_id', 'nodeName', true);
		  </script>          
         
        </td>
      </tr>
      <tr>
        <td colspan="2">
          <script language="javascript">
              createFilePickerFrame(pickerId, '<osp:url value="filePicker.osp"/>', '', 'attachFilePicker', 
              'cell_id=<c:out value="${org_theospi_matrix_attachCell.scaffoldingCell.id}"/>',
              'node_id', 'nodeName', true);
          </script>    
        </td>
     </tr>
   </table>
  
   <p class="act">
     <input type="submit" name="Attach" value="Attach" class="active" />
     <input type="button" name="Cancel" value="Cancel" 
  			onclick="window.document.location='<osp:url value="viewCell.osp">
  			<osp:param name="cell_id" value="${org_theospi_matrix_attachCell.id}"/>
  			</osp:url>'"/>
   </p>
  
  </form>
</fieldset>
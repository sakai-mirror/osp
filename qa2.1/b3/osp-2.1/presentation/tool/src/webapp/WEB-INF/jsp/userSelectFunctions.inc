<script type="text/javascript" language="JavaScript">

function updateOrLoadParticipantList(selectBoxName,filterName,select1Name,select2Name) {

   <c:if test="${empty filterSelect}">
      if (getSelectedValue(filterName) != "roles") {
         var select1Box = ospGetElementById(select1Name);
         updateItems(selectBoxName);
         select1Box.form.onsubmit();         
         select1Box.form.submit();
         return;
      }
   </c:if>

   updateParticipantList(filterName,select1Name,select2Name);
}

function getRolesForMember(memberId){

<c:forEach var="member" items="${members}" varStatus="status">
   if (memberId == "<c:out value="${member.id.value}"/>"){
      var roles<c:out value="${status.index}"/> = new Array();
      <c:forEach var="role" items="${member.worksiteRoles}" varStatus="i">
      roles<c:out value="${status.index}"/>[<c:out value="${i.index}"/>] = "<c:out value="${role.id.value}"/>";
      </c:forEach>
      return roles<c:out value="${status.index}"/>;
   }
</c:forEach>
   var roles = new Array();
   return roles;
}

function getRoles(){
   var roles = new Array();
<c:forEach var="role" items="${roles}" varStatus="status2">
   <c:set var="index" value="${status2.index}"/>
   var option<c:out value="${index}"/> = new Option();
   option<c:out value="${index}"/>.value = "<c:out value="${role.id.value}"/>";
   option<c:out value="${index}"/>.text = "All participants with '<c:out value="${role.displayName}"/>' role";
   roles[<c:out value="${index}"/>] = option<c:out value="${index}"/>;
</c:forEach>
   return roles;
}

function getMembers(){
   var members = new Array();
<c:forEach var="member" items="${members}" varStatus="status">
   <c:set var="index" value="${status.index}"/>
   var option<c:out value="${index}"/> = new Option();
   option<c:out value="${index}"/>.value = "<c:out value="${member.id.value}"/>";
   option<c:out value="${index}"/>.text = "<c:out value="${member.displayName}"/> (<c:out value="${member.id.value}"/>)";
   members[<c:out value="${index}"/>] = option<c:out value="${index}"/>;
</c:forEach>
   return members;
}
</script>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.presentation.bundle.Messages"/>

<osp-c:authZMap prefix="osp.presentation." var="can" />
<script type="text/javascript" src="/library/js/jquery.js">
</script>
<script type="text/javascript">
    $(document).ready(function(){
	  $('.ospTable tr:even td').addClass('even');
	  
	  });
</script>
<!--[if gt IE 5.0]><![if lt IE 7]>
<style type="text/css">
/* that IE 5+ conditional comment makes this only visible in IE 5+*/ 
ul.makeMenu{border:none}
ul.makeMenu ul {  /* copy of above declaration without the > selector */                                                                      
  display: none; position: absolute; top: 18px; left: 0px; background-image:none; background-color:#ffffff;width: 8em; z-index:3;border:1px solid  #666  	
}
ul.makeMenu ul li {display:block}
ul.makeMenu{width:5em !important}

.attach{width:1em !important;}
</style>
<![endif]><![endif]-->
<!--[if gt IE 6.0]>
<style type="text/css">
ul.makeMenu li {
  list-style-type: none; 
  margin: 0px; 
  padding:5px; 
  position: relative;
  color: #000000;
  display:inline;
}

.menuOpen{margin:2px !important;padding:2px !important;background:#fff;width:6em;border-width:1px 1px 1px 0;border-style:solid;border-color: #ccc;}
.menuOpen:hover{border-width:1px 1px 1px 0;border-style:solid;border-color: #ccc;;background:#fff}
ul.makeMenu{border:none !important;background:transparent;border:1px solid #ccc !important;margin:2px;padding:2px;padding-left:5px;width:3em;}
ul.makeMenu:hover{border:none !important; background:transparent}
ul.makeMenu ul{top: 15px !important;left:0}
.attach{width:1em !important;}
ul.makeMenu{border:none !important}
</style>
<![endif]-->

<%--
<script  type ="text/javascript">
$(document).ready(function() {
	$('.makeMenu').click(function(e) {
			targetId=$(e.target).parent('li').attr('id');
			$('.makeMenuChild').fadeOut();
			$('#menu-' + targetId).fadeIn();
		});
	});

</script>
--%>
	
<script  type ="text/javascript">
$(document).ready(function() {
	jQuery('body').click(function(e) { 
			
		if ( e.target.className !='menuOpen' &&e.target.className !='dropdn'  ){
			$('.makeMenuChild').fadeOut();
		}
			else
			{
				if( e.target.className =='dropdn' ){
			targetId=$(e.target).parent('li').attr('id');
			$('.makeMenuChild').hide();
			$('#menu-' + targetId).fadeIn(500);

}
				else{
			targetId=e.target.id;
			$('.makeMenuChild').hide();
			$('#menu-' + targetId).fadeIn(500);
			}}
	});
	});

</script>


<ul class="navIntraTool">
    <c:if test="${can.create}">
        <li class="firstToolBarItem"><span><a href="<osp:url value="createPresentation.osp"/>"
            title="<fmt:message key="action_new_portfolio"/>"> <fmt:message key="action_new_portfolio"/> </a></span></li>
    </c:if>
    
    <c:if test="${isMaintainer}">
        <li><span><a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message"><fmt:message key="message_permissionsEdit">
	          <fmt:param><c:out value="${tool.title}"/></fmt:param>
		  <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message></osp:param>
                <osp:param name="name" value="presentation"/>
                <osp:param name="qualifier" value="${tool.id}"/>
                <osp:param name="returnView" value="listPresentationRedirect"/>
                <osp:param name="session.${lastViewKey}" value="/listPresentation.osp"/>
                </osp:url>"
            title="<fmt:message key="action_permissions_title"/>"> <fmt:message key="action_permissions"/> </a></span></li>
    </c:if>
</ul>

<ul class="tabNav specialLink">
    <c:choose>
      <c:when test="${filterList != 'all'}">
          <li><a href="<osp:url value="listPresentation.osp"/>&filterListKey=all"><fmt:message key="action_filter_all"/></a></li>
      </c:when>
      <c:otherwise>
        <li class="selected"><span><fmt:message key="action_filter_all"/></span></li>
      </c:otherwise>
    </c:choose>
    
    <c:choose>
      <c:when test="${filterList != 'mine'}">
          <li><a href="<osp:url value="listPresentation.osp"/>&filterListKey=mine"><fmt:message key="action_filter_mine"/></a></li>
      </c:when>
      <c:otherwise>
      	<li class="selected"><span><fmt:message key="action_filter_mine"/></span></li>
			</c:otherwise>
    </c:choose>
    
    <c:choose>
      <c:when test="${filterList != 'shared'}">
         <li><a href="<osp:url value="listPresentation.osp"/>&filterListKey=shared"><fmt:message key="action_filter_shared"/></a></li>
      </c:when>
      <c:otherwise>
        <li class="selected"><span><fmt:message key="action_filter_shared"/></span></li>
      </c:otherwise>
    </c:choose>
	</ul>
	<div class="tabNavPanel">
 <!-- temp separation; end of tabs -->


<ul class="smallNavIntraTool specialLink">
		<li>
	    <fmt:message key="title_show"/>
		</li>	
    <c:choose>
      <c:when test="${showHidden != 'visible'}">
         <li class="firstItem"><span><a href="<osp:url value="listPresentation.osp"/>&showHiddenKey=visible"><fmt:message key="action_show_not_hidden"/></a></span></li>
      </c:when>
      <c:otherwise>
         <li class="firstItem"><span><fmt:message key="action_show_not_hidden"/></span></li>
      </c:otherwise>
    </c:choose>
    
    <c:choose>
      <c:when test="${showHidden != 'hidden'}">
          <li><span><a href="<osp:url value="listPresentation.osp"/>&showHiddenKey=hidden"><fmt:message key="action_show_hidden"/></a></span></li>
      </c:when>
      <c:otherwise>
        <li><span><fmt:message key="action_show_hidden"/></span></li>
      </c:otherwise>
    </c:choose>
    
    <c:choose>
      <c:when test="${showHidden != 'all'}">
          <li><span><a href="<osp:url value="listPresentation.osp"/>&showHiddenKey=all"><fmt:message key="action_show_all"/></a></span></li>
      </c:when>
      <c:otherwise>
        <li><span><fmt:message key="action_show_all"/></span></li>
      </c:otherwise>
    </c:choose>
</ul>

<div class="navPanel" id="NavPanelPager">
	<osp:url var="listUrl" value="listPresentation.osp"/>
	<osp:listScroll listUrl="${listUrl}" className="listNav" />
</div>	

<%-- show no portfolio message if presentation list is empty --%>
<c:choose>
	<c:when  test="${empty presentations && showHidden == 'all'}">
      <p align="center">
		<fmt:message key="table_empty_list_all"/>
      <c:if test="${filterList != 'shared' && can.create}">
        <br/><a href="<osp:url value="createPresentation.osp"/>"
            title="<fmt:message key="action_new_portfolio_now"/>"> <fmt:message key="action_new_portfolio_now"/> </a>
      </c:if>
      </p>
	</c:when>
   
	<c:when  test="${empty presentations && showHidden == 'hidden'}">
      <p align="center">
		<fmt:message key="table_empty_list_hidden"/>
      <c:if test="${filterList != 'shared' && can.create}">
        <br/><a href="<osp:url value="createPresentation.osp"/>"
            title="<fmt:message key="action_new_portfolio_now"/>"> <fmt:message key="action_new_portfolio_now"/> </a>
      </c:if>
      </p>
	</c:when>
   
	<c:when  test="${empty presentations && showHidden == 'visible'}">
      <p align="center">
		<fmt:message key="table_empty_list_visible"/>
      <c:if test="${filterList != 'shared' && can.create}">
        <br/><a href="<osp:url value="createPresentation.osp"/>"
            title="<fmt:message key="action_new_portfolio_now"/>"> <fmt:message key="action_new_portfolio_now"/> </a>
      </c:if>
      </p>
	</c:when>
   
   <%-- Otherwise display list of portfolios --%>
	<c:otherwise>
		
	<table class="listHier ospTable" cellspacing="0" cellpadding="0"  border="0" summary="<fmt:message key=" table_presentationManager_summary" />" >
	   <thead>
		  <tr>
			 <th scope="col"><fmt:message key="table_header_name"/></th>
			 <th scope="col" class="attach"></th>
			 <th scope="col"><fmt:message key="table_header_owner"/></th>
			 <th scope="col"><fmt:message key="table_header_dateModified"/></th>
			 <th scope="col" class="attach"><fmt:message key="table_header_status"/></th>
			 <th scope="col" class="attach"><fmt:message key="table_header_shared"/></th>
			 <th scope="col" class="attach"><fmt:message key="table_header_comments"/></th>
			 <c:if test="${myworkspace}">
			   <th scope="col"><fmt:message key="table_header_worksite"/></th>
			 </c:if>
		  </tr>
	   </thead>
		<tbody>
      
	  <c:forEach var="presentationBean" items="${presentations}" varStatus="loopCounter">
	
		<c:set var="presentation" value="${presentationBean.presentation}" />
		<c:set var="isAuthorizedTo" value="${presentation.authz}" />
		<osp-c:authZMap prefix="osp.presentation." var="presCan" qualifier="${presentation.id}"/>
	
		<tr
		<c:if test="${presentation.expired}">
		class="inactive"
		</c:if>
		>
		  <td style="white-space:nowrap">
		  		  <h4>
		  <a <c:if test="${presentation.template.includeHeaderAndFooter == false}">target="_blank" title="<fmt:message key="table_presentationManager_new_window"/>"</c:if>
					href="<osp:url value="viewPresentation.osp"/>&id=<c:out value="${presentation.id.value}" />">
			 <c:out value="${presentation.name}" />
		  </a>
		  </h4>	
		  </td>
        
        <!-- START selection of actions/options -->
		  <td>
           <form name="form${presentation.id.value}" style="margin:0">
				<a href="#" onfocus="document.getElementById('menu-<c:out  value="${loopCounter.index}" />').style.display='none';"></a>
				<a href="#" onfocus="document.getElementById('menu-<c:out  value="${loopCounter.index}" />').style.display='block';" class="skip"><fmt:message key="table_action_action_open"/></a>
		
		   <%-- desNote: alternate rendering - using the resources menu as model - come back to it if time--%>	
					<ul style="z-index:<c:out  value="${1000 - loopCounter.index}" />;margin:0;display:block" class="makeMenu">
						<li  class="menuOpen" id="<c:out  value="${loopCounter.index}" />">
							&nbsp;<fmt:message key="table_action_action"/>
							<img src = "/library/image/sakai/icon-dropdn.gif?panel=Main" border="0"  alt="Add"  class="dropdn"/> 
							<ul  id="menu-<c:out  value="${loopCounter.index}" />" class="makeMenuChild">
							<c:if test="${presentation.owner.id.value == osp_agent.id.value}">
								<li>
									<a href="<osp:url value="sharePresentation.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="action_share"/>
									</a>
								</li>
							</c:if>
							<c:if test="${isAuthorizedTo.edit}">
									<a 
									href="<osp:url value="editPresentation.osp"/>&id=<c:out value="${presentation.id.value}" />"> <fmt:message key="table_action_edit"/>
									</a>
							</c:if>
  <c:if test="${presentation.owner.id.value == osp_agent.id.value}">
             <a
                href="<osp:url value="PresentationStats.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="table_action_viewStats"/></a>
             </c:if>
   
             <c:if test="${presentation.owner.id.value == osp_agent.id.value}">
             <a 
                href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=presentationManager&presentationId=<c:out value="${presentation.id.value}"/>/<c:out value="${presentation.name}" />.zip"><fmt:message key="table_action_download"/></a>
             </c:if>
             
             <c:if test="${isAuthorizedTo.delete}">
             <a
                href="<osp:url value="deletePresentation.osp"/>&id=<c:out value="${presentation.id.value}"/>"><fmt:message key="table_action_delete"/></a>
             </c:if>
             
             <c:if test="${!presCan.hide}">
             <a
                href="<osp:url value="hidePresentation.osp"/>&hideAction=hide&id=<c:out value="${presentation.id.value}" />"><fmt:message key="table_action_hide"/></a>
             </c:if>
             
             <c:if test="${presCan.hide}">
             <a
                href="<osp:url value="hidePresentation.osp"/>&hideAction=show&id=<c:out value="${presentation.id.value}" />"><fmt:message key="table_action_show"/></a>
             </c:if>
							</ul>
						</li>
					<li style="height:1px;width:1px;display:inline;">
						<a href="#"  class="skip" onfocus="document.getElementById('menu-<c:out  value="${loopCounter.index}" />').style.display='none';document.getElementById('last-<c:out  value="${loopCounter.index}" />').focus()" ><fmt:message key="table_action_action_close"/></a>
					</li>
						
					</ul>
						<a href="#" id="last-<c:out  value="${loopCounter.index}" />" class="skip"></a>
           </form>
        </td>
        <!-- END selection of actions/options -->
        
		  <td><c:out value="${presentation.owner.displayName}" /></td>
        
		  <td><c:set var="dateFormat"><fmt:message key="dateFormat_Middle"/></c:set><fmt:formatDate value="${presentation.modified}" pattern="${dateFormat}"/></td> 
        
		  <td align="center">
			 <c:if test="${!presentation.expired}">
				<img alt="<fmt:message key="alt_image_yes"/>"  src="/library/image/sakai/checkon.gif" border="0"/>
			 </c:if>
		  </td>
        
		  <td align="center">
			 <c:choose>
				 <c:when test="${presentationBean.public}">
					<fmt:message key="comments_public"/>
				 </c:when>
				 <c:when test="${presentationBean.shared}">
					<img alt="<fmt:message key="alt_image_yes"/>"  src="/library/image/sakai/checkon.gif" border="0"/>
				 </c:when>
				 <c:otherwise/>
			 </c:choose>
		  </td>
		  
		  <td align="center">
				<c:choose>
				<c:when test="${presentationBean.commentNum > 0}">
  				<a href="<osp:url value="listComments.osp">
					<osp:param name="id" value="${presentation.id.value}" />
				</osp:url>" title="<fmt:message key="table_header_comments"/>"> 
				<c:out value="${presentationBean.commentNumAsString}" />
				</a>
				</c:when>
				<c:otherwise>
					 <c:out value="${presentationBean.commentNumAsString}" />
				</c:otherwise>
				</c:choose>
			</td>
		  
		  <c:if test="${myworkspace}">
			 <td><c:out value="${presentation.worksiteName}" /></td>
		  </c:if>
		</tr>
	  </c:forEach>
	   </tbody>
	  </table>
	  <div style="height:20em"></div>
	 </c:otherwise>
</c:choose> 
</div>

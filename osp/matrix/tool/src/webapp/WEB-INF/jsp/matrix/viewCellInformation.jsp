<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<c:set var="cell" value="${cellBean.cell}" />

<form method="POST">

	<br>

	<span class="xheader"><osp:message key="label_title" /></span>&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${wizardPageDef.title}"/>

	
	<c:if test="${wizardPageDef.description != null && wizardPageDef.description != ''}">
		<h4 class="xheader">
			<osp:message key="label_description" />
		</h4>
		<div class="indnt1">
			<c:out value="${wizardPageDef.description}" escapeXml="false" />
		</div>
	</c:if>
	
	<c:if test="${not empty wizardPageDef.guidance}">

		<c:set value="false" var="oneDisplayed" />
		<c:set value="0" var="i" />
	
		<!-- ** instruction ** -->
	
		<c:forEach var="guidanceItem"
			items="${wizardPageDef.guidance.items}">
			<c:if
				test="${guidanceItem.text != '' || not empty guidanceItem.attachments}">
				<c:if test="${guidanceItem.type == 'instruction'}">
					<h4 class="xheader">
				<!--		<img src="/osp-jsf-resource/xheader/images/xheader_mid_hide.gif"
						id="expandImg<c:out value='${i}'/>" alt=""
						onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='';document.getElementById('expandImg<c:out value='${i}'/>').style.display='none';resizeFrame('shrink')"
						<c:if test="${!oneDisplayed}"> style="display:none;" </c:if> /> <img
						src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif"
						id="collapseImg<c:out value='${i}'/>" alt=""
						onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='none';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='none';document.getElementById('expandImg<c:out value='${i}'/>').style.display='';"
						<c:if test="${oneDisplayed}"> style="display:none;" </c:if> />	-->
						<osp:message key="instructions" /></h4>
					<div class="textPanel indnt1" id="textPanel<c:out value='${i}'/>"
						<c:if test="${oneDisplayed}"> style="display:none;" </c:if>>
					<c:out value="${guidanceItem.text}" escapeXml="false" /> <c:if
						test="${not empty guidanceItem.attachments}">
						<ul class="attachList indnt1">
							<c:forEach var="guidanceItemAtt"
								items="${guidanceItem.attachments}">
								<li><img border="0" title="<c:out value="${hover}" />"
									alt="<c:out value="${guidanceItemAtt.displayName}"/>"
									src="/library/image/<osp-c:contentTypeMap fileType="${guidanceItemAtt.mimeType}" mapType="image"/>" />
								<a
									href="<c:out value="${guidanceItemAtt.fullReference.base.url}" />"
									target="_blank"> <c:out
									value="${guidanceItemAtt.displayName}" /> </a></li>
							</c:forEach>
						</ul>
					</c:if></div>
				</c:if>
			</c:if>
		</c:forEach>
	
		<c:set value="1" var="i" />
	
		<!-- ** rationale ** -->
	
		<c:forEach var="guidanceItem"
			items="${wizardPageDef.guidance.items}">
			<c:if
				test="${guidanceItem.text != '' || not empty guidanceItem.attachments}">
				<c:if test="${guidanceItem.type == 'rationale'}">
					<h4 class="xheader">
		<!--						<img src="/osp-jsf-resource/xheader/images/xheader_mid_hide.gif"
						id="expandImg<c:out value='${i}'/>" alt=""
						onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='';document.getElementById('expandImg<c:out value='${i}'/>').style.display='none';resizeFrame('shrink')"
						<c:if test="${!oneDisplayed}"> style="display:none;" </c:if> /> <img
						src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif"
						id="collapseImg<c:out value='${i}'/>" alt=""
						onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='none';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='none';document.getElementById('expandImg<c:out value='${i}'/>').style.display='';"
						<c:if test="${oneDisplayed}"> style="display:none;" </c:if> />  -->
						<osp:message key="rationale" /></h4>
					<div class="textPanel indnt1" id="textPanel<c:out value='${i}'/>"
						<c:if test="${oneDisplayed}"> style="display:none;" </c:if>>
					<c:out value="${guidanceItem.text}" escapeXml="false" /> <c:if
						test="${not empty guidanceItem.attachments}">
						<ul class="attachList indnt1">
							<c:forEach var="guidanceItemAtt"
								items="${guidanceItem.attachments}">
								<li><img border="0" title="<c:out value="${hover}" />"
									alt="<c:out value="${guidanceItemAtt.displayName}"/>"
									src="/library/image/<osp-c:contentTypeMap fileType="${guidanceItemAtt.mimeType}" mapType="image"/>" />
								<a
									href="<c:out value="${guidanceItemAtt.fullReference.base.url}" />"
									target="_blank"> <c:out
									value="${guidanceItemAtt.displayName}" /> </a></li>
							</c:forEach>
						</ul>
					</c:if></div>
				</c:if>
			</c:if>
		</c:forEach>
		<c:set value="2" var="i" />
	
		<!-- ** examples ** -->
	
		<c:forEach var="guidanceItem"
			items="${wizardPageDef.guidance.items}">
			<c:if
				test="${guidanceItem.text != '' || not empty guidanceItem.attachments}">
				<c:if test="${guidanceItem.type == 'example'}">
					<h4 class="xheader">
	<!--					<img src="/osp-jsf-resource/xheader/images/xheader_mid_hide.gif"
						id="expandImg<c:out value='${i}'/>" alt=""
						onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='';document.getElementById('expandImg<c:out value='${i}'/>').style.display='none';resizeFrame('shrink')"
						<c:if test="${!oneDisplayed}"> style="display:none;" </c:if> /> <img
						src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif"
						id="collapseImg<c:out value='${i}'/>" alt=""
						onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='none';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='none';document.getElementById('expandImg<c:out value='${i}'/>').style.display='';"
						<c:if test="${oneDisplayed}"> style="display:none;" </c:if> /> -->
						<osp:message key="examples" /></h4>
					<div class="textPanel indnt1" id="textPanel<c:out value='${i}'/>"
						<c:if test="${oneDisplayed}"> style="display:none;" </c:if>>
					<c:out value="${guidanceItem.text}" escapeXml="false" /> <c:if
						test="${not empty guidanceItem.attachments}">
						<ul class="attachList indnt1">
							<c:forEach var="guidanceItemAtt"
								items="${guidanceItem.attachments}">
								<li><img border="0" title="<c:out value="${hover}" />"
									alt="<c:out value="${guidanceItemAtt.displayName}"/>"
									src="/library/image/<osp-c:contentTypeMap fileType="${guidanceItemAtt.mimeType}" mapType="image"/>" />
								<a
									href="<c:out value="${guidanceItemAtt.fullReference.base.url}" />"
									target="_blank"> <c:out
									value="${guidanceItemAtt.displayName}" /> </a></li>
							</c:forEach>
						</ul>
					</c:if></div>
				</c:if>
			</c:if>
		</c:forEach>
		
		<!-- ** Rubric ** -->
	
		<c:forEach var="guidanceItem"
			items="${wizardPageDef.guidance.items}">
			<c:if
				test="${guidanceItem.text != '' || not empty guidanceItem.attachments}">
				<c:if test="${guidanceItem.type == 'rubric'}">
					<h4 class="xheader">
	<!--					<img src="/osp-jsf-resource/xheader/images/xheader_mid_hide.gif"
						id="expandImg<c:out value='${i}'/>" alt=""
						onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='';document.getElementById('expandImg<c:out value='${i}'/>').style.display='none';resizeFrame('shrink')"
						<c:if test="${!oneDisplayed}"> style="display:none;" </c:if> /> <img
						src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif"
						id="collapseImg<c:out value='${i}'/>" alt=""
						onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='none';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='none';document.getElementById('expandImg<c:out value='${i}'/>').style.display='';"
						<c:if test="${oneDisplayed}"> style="display:none;" </c:if> /> -->
						<osp:message key="rubrics" /></h4>
					<div class="textPanel indnt1" id="textPanel<c:out value='${i}'/>"
						<c:if test="${oneDisplayed}"> style="display:none;" </c:if>>
					<c:out value="${guidanceItem.text}" escapeXml="false" /> <c:if
						test="${not empty guidanceItem.attachments}">
						<ul class="attachList indnt1">
							<c:forEach var="guidanceItemAtt"
								items="${guidanceItem.attachments}">
								<li><img border="0" title="<c:out value="${hover}" />"
									alt="<c:out value="${guidanceItemAtt.displayName}"/>"
									src="/library/image/<osp-c:contentTypeMap fileType="${guidanceItemAtt.mimeType}" mapType="image"/>" />
								<a
									href="<c:out value="${guidanceItemAtt.fullReference.base.url}" />"
									target="_blank"> <c:out
									value="${guidanceItemAtt.displayName}" /> </a></li>
							</c:forEach>
						</ul>
					</c:if></div>
				</c:if>
			</c:if>
		</c:forEach>
		
		<!-- ** Expectations ** -->
	
		<c:forEach var="guidanceItem"
			items="${wizardPageDef.guidance.items}">
			<c:if
				test="${guidanceItem.text != '' || not empty guidanceItem.attachments}">
				<c:if test="${guidanceItem.type == 'expectations'}">
					<h4 class="xheader">
	<!--					<img src="/osp-jsf-resource/xheader/images/xheader_mid_hide.gif"
						id="expandImg<c:out value='${i}'/>" alt=""
						onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='';document.getElementById('expandImg<c:out value='${i}'/>').style.display='none';resizeFrame('shrink')"
						<c:if test="${!oneDisplayed}"> style="display:none;" </c:if> /> <img
						src="/osp-jsf-resource/xheader/images/xheader_mid_show.gif"
						id="collapseImg<c:out value='${i}'/>" alt=""
						onclick="document.getElementById('textPanel<c:out value='${i}'/>').style.display='none';document.getElementById('collapseImg<c:out value='${i}'/>').style.display='none';document.getElementById('expandImg<c:out value='${i}'/>').style.display='';"
						<c:if test="${oneDisplayed}"> style="display:none;" </c:if> /> -->
						<osp:message key="expectations" /></h4>
					<div class="textPanel indnt1" id="textPanel<c:out value='${i}'/>"
						<c:if test="${oneDisplayed}"> style="display:none;" </c:if>>
					<c:out value="${guidanceItem.text}" escapeXml="false" /> <c:if
						test="${not empty guidanceItem.attachments}">
						<ul class="attachList indnt1">
							<c:forEach var="guidanceItemAtt"
								items="${guidanceItem.attachments}">
								<li><img border="0" title="<c:out value="${hover}" />"
									alt="<c:out value="${guidanceItemAtt.displayName}"/>"
									src="/library/image/<osp-c:contentTypeMap fileType="${guidanceItemAtt.mimeType}" mapType="image"/>" />
								<a
									href="<c:out value="${guidanceItemAtt.fullReference.base.url}" />"
									target="_blank"> <c:out
									value="${guidanceItemAtt.displayName}" /> </a></li>
							</c:forEach>
						</ul>
					</c:if></div>
				</c:if>
			</c:if>
		</c:forEach>
		
		
		
	
	</c:if> 
	<!-- ************* Guidance Area End ************* -->
</form>
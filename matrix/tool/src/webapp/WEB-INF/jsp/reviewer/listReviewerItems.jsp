<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "org.theospi.portfolio.matrix.bundle.Messages"/>

<osp-c:authZMap prefix="osp.matrix." var="can" />

<div class="navIntraTool">
    <c:if test="${isMaintainer}">
        <a href="<osp:url value="osp.permissions.helper/editPermissions">
        <osp:param name="message"><fmt:message key="action_message_setPermission">
        <fmt:param><c:out value="${tool.title}"/></fmt:param>
               <fmt:param><c:out value="${worksite.title}"/></fmt:param></fmt:message>
        </osp:param>

             <osp:param name="name" value="review"/>
             <osp:param name="qualifier" value="${tool.id}"/>
             <osp:param name="returnView" value="listReviewerItemsRedirect"/>
             </osp:url>"
            title="<fmt:message key="action_permissions_title"/>" >
            <fmt:message key="action_permissions"/>
        </a>
    </c:if>
</div>

<c:if test="${can.evaluate}">
    <form method="POST" id="reviewList" name="reviewList">
        <osp:form />
        <osp:url  var="listUrl" value="listReviewerItems.osp" />
        <osp:listScroll  listUrl="${listUrl}" className="navIntraTool" />
    </form>
</c:if>
    <h3><fmt:message key="title_evaluationManager"/></h3>
    
<c:if test="${!can.evaluate}">   
    <div class="validation"><fmt:message key="eval_message_notAllowed"/><div>
</c:if>

<c:if test="${can.evaluate}">
    <form method="POST" id="reviewList" name="reviewList">
        <osp:form />
    
    
    <c:if test="${not empty errorMessage}">
        <div class="validation"><c:out value="${errorMessage}" /></div>
        <br />
    </c:if>

    <p class="instructions">
        <fmt:message key="eval_sortTitleMessage"/>
    </p>
    <br />
    <c:set var="sortDir" value="asc" />
    <c:set var="sortDirectionText" value="descending" />
    <table class="listHier" cellspacing="0">
        <thead>
            <tr>
                <th title='<fmt:message key="eval_sortbytitle"/>' scope="col">
                   <c:if test="${sortByColumn == 'title'}">
                     <c:if test="${direction == 'asc'}">
                        <c:set var="sortDir" value="desc" />
                        <c:set var="sortDirectionText" value="ascending" />
                     </c:if>
                  </c:if>
                  <a href="<osp:url value="listReviewerItems.osp"/>&sortByColumn=title&direction=<c:out value="${sortDir}" />">
                     <fmt:message key="eval_title"/>
                     <c:if test="${sortByColumn == 'title'}">
                     <img src="/library/image/sakai/sort<c:out value="${sortDirectionText}" />.gif?panel=Main" border="0"
                             <c:if test="${sortDirectionText == 'ascending'}">
                                alt ='<fmt:message key="eval_sortbytitleasc"/>'/>
                             </c:if>
                             <c:if test="${sortDirectionText == 'descending'}">
                                alt ='<fmt:message key="eval_sortbytitledesc"/>'/>
                             </c:if>
                     </c:if>
                  </a>
               </th>
               <th title='<fmt:message key="eval_sortbyowner"/>'>
                  <c:if test="${can.viewOwner}">
                     <c:if test="${sortByColumn == 'owner'}">
                        <c:if test="${direction == 'asc'}">
                           <c:set var="sortDir" value="desc" />
                           <c:set var="sortDirectionText" value="ascending" />
                        </c:if>
                     </c:if>
                     <a href="<osp:url value="listReviewerItems.osp"/>&sortByColumn=owner&direction=<c:out value="${sortDir}" />">
                        <fmt:message key="eval_owner"/>
                        <c:if test="${sortByColumn == 'owner'}">
                        <img src="/library/image/sakai/sort<c:out value="${sortDirectionText}" />.gif?panel=Main" border="0"
                            <c:if test="${sortDirectionText == 'ascending'}">
                                alt ='<fmt:message key="eval_sortbyownerasc"/>'/>
                             </c:if>
                             <c:if test="${sortDirectionText == 'descending'}">
                                alt ='<fmt:message key="eval_sortbyownerdesc"/>'/>
                             </c:if>
                        </c:if>
                     </a>
                  </c:if> 
                  <c:if test="${!can.viewOwner}">&nbsp</c:if>
                    
                </th>
                <th title='<fmt:message key="eval_sortbydateReceived"/>'>
                  <c:if test="${sortByColumn == 'date'}">
                     <c:if test="${direction == 'asc'}">
                        <c:set var="sortDir" value="desc" />
                        <c:set var="sortDirectionText" value="ascending" />
                     </c:if>
                  </c:if>
                  <a href="<osp:url value="listReviewerItems.osp"/>&sortByColumn=date&direction=<c:out value="${sortDir}" />">
                     Date Received 
                     <c:if test="${sortByColumn == 'date'}">
                     <img src="/library/image/sakai/sort<c:out value="${sortDirectionText}" />.gif?panel=Main" border="0"
                             <c:if test="${sortDirectionText == 'ascending'}">
                                alt ='<fmt:message key="eval_sortbydateReceivedasc"/>'/>
                             </c:if>
                             <c:if test="${sortDirectionText == 'descending'}">
                                alt ='<fmt:message key="eval_sortbydateReceiveddesc"/>'/>
                             </c:if>
                     </c:if>
                  </a>
                </th>
                <th title='<fmt:message key="eval_sortbytype"/>'>
                  <c:if test="${sortByColumn == 'type'}">
                     <c:if test="${direction == 'asc'}">
                        <c:set var="sortDir" value="desc" />
                        <c:set var="sortDirectionText" value="ascending" />
                     </c:if>
                  </c:if>
                  <a href="<osp:url value="listReviewerItems.osp"/>&sortByColumn=type&direction=<c:out value="${sortDir}" />">
                     Type 
                     <c:if test="${sortByColumn == 'type'}">
                     <img src="/library/image/sakai/sort<c:out value="${sortDirectionText}" />.gif?panel=Main" border="0"
                             <c:if test="${sortDirectionText == 'ascending'}">
                                alt ='<fmt:message key="eval_sortbytypeasc"/>'/>
                             </c:if>
                             <c:if test="${sortDirectionText == 'descending'}">
                                alt ='<fmt:message key="eval_sortbytypedesc"/>'/>
                             </c:if>
                     </c:if>
                  </a>
                </th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="item" items="${reviewerItems}"
                varStatus="loopCount">

                <tr>
                    <td>
                    <div align="left">
                        <a href="<osp:url value="${item.url}">
                           <c:forEach var="paramBean" items="${item.urlParams}">
                              <osp:param name="${paramBean.key}" value="${paramBean.value}" />
                           </c:forEach>
                           </osp:url>">
                        <c:out value="${item.title}" />
                        </a>
                    </div>
                    </td>
                    <td>
                    <div align="left"><c:if test="${can.viewOwner}">
                        <c:out value="${item.owner.sortName}" />
                    </c:if> <c:if test="${!can.viewOwner}">&nbsp</c:if>
                    </div>
                    </td>
                    <td>
                     <div align="left"><c:if test="${item.submittedDate==null}"> &nbsp; </c:if>
                        <c:set var="dateFormat"><fmt:message key="dateFormat_Middle"/></c:set><fmt:formatDate value="${item.submittedDate}" pattern="${dateFormat}"/> </div>
                    </td>
                    <td>
                     <div align="left">
                      <c:out value="${item.evalType}" /></div>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <br />


    </form>
</c:if>
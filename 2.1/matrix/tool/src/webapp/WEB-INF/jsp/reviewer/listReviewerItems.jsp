<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<osp-c:authZMap prefix="osp.matrix." var="can" />

<div class="navIntraTool">
    <c:if test="${isMaintainer}">
        <a
            href="<osp:url value="osp.permissions.helper/editPermissions">
             <osp:param name="message" value="Set permissions for ${tool.title} in worksite '${worksite.title}'"/>
             <osp:param name="name" value="review"/>
             <osp:param name="qualifier" value="${tool.id}"/>
             <osp:param name="returnView" value="listReviewerItemsRedirect"/>
             </osp:url>"
            title="Permissions..."> 
                Permissions... 
        </a>
    </c:if>
</div>

<c:if test="${can.review}">
    <form method="POST" id="reviewList" name="reviewList">
        <osp:form />
        <osp:url  var="listUrl" value="listReviewerItems.osp" />
        <osp:listScroll  listUrl="${listUrl}" className="navIntraTool" />
        <input type="hidden" name="reviewItem" value="" />
        <input type="hidden" name="newStatus" value="" />
    </form>
</c:if>
    <h3>Review Manager</h3>
    
<c:if test="${!can.review}">   
    <div class="validation">Not allowed to review<div>
</c:if>

<c:if test="${can.review}">
    <form method="POST" id="reviewList" name="reviewList">
        <osp:form />
        <input type="hidden" name="reviewItem" value="" />
        <input type="hidden" name="newStatus" value="" />
    
    
    <c:if test="${not empty errorMessage}">
        <div class="validation"><c:out value="${errorMessage}" /></div>
        <br />
    </c:if>

    <p class="instructions">
        Click the column title name to sort by column.
    </p>
    <br />
    <table class="listHier" cellspacing="0">
        <thead>
            <tr>
                <th scope="col" onclick="sort('dynamic',0,0)"
                    style="cursor:pointer"
                    title="Sort by Root Criterion">Root Criterion</th>
                <th scope="col" onclick="sort('dynamic',1,0)"
                    style="cursor:pointer" title="Sort by Level">Level</th>
                <th scope="col" onclick="sort('dynamic',2,0)"
                    style="cursor:pointer" title="Sort Owner"><c:if
                    test="${can.viewOwner}">Owner</c:if> <c:if
                    test="${!can.viewOwner}">&nbsp</c:if></th>
                <th scope="col" onclick="sort('dynamic',3,0)"
                    style="cursor:pointer" title="Sort by Date received">Date
                Received</th>
                <th scope="col" onclick="sort('dynamic',4,0)"
                    style="cursor:pointer" title="Sort by Status">Status</th>
                <th scope="col" onclick="sort('dynamic',5,0)"
                    style="cursor:pointer" title="Sort by Status Date">Status
                Date</th>
                <th scope="col" onclick="sort('dynamic',6,0)"
                    style="cursor:pointer" title="Sort by Reviewer">Reviewer</th>
            </tr>
        </thead>


        <tbody id="dynamic">
            <c:forEach var="item" items="${reviewerItems}"
                varStatus="loopCount">

                <c:set var="cell" value="${item.cell}" />

                <tr>
                    <td>
                    <div align="left"><c:out
                        value="${cell.scaffoldingCell.rootCriterion.description}" />
                        &nbsp; <!-- reviewer item actions -->
                        <%@ include file="actions.inc"%></div>
                    </td>
                    <td>
                    <div align="left"><c:out
                        value="${cell.scaffoldingCell.level.description}" />
                    </div>
                    </td>

                    <td>
                    <div align="left"><c:if test="${can.viewOwner}">
                        <c:out value="${cell.matrix.owner.displayName}" />
                    </c:if> <c:if test="${!can.viewOwner}">&nbsp</c:if>
                    </div>
                    </td>

                    <td>
                    <div align="left"><fmt:formatDate pattern="M-d-yy"
                        value="${item.created}" /></div>
                    </td>
                    <td>
                    <div align="left"><spring:message
                        code="${item.status}" text="${item.status}" /></div>
                    </td>
                    <td>
                    <div align="left"><c:if
                        test="${item.modified==null}"> &nbsp; </c:if> <fmt:formatDate
                        pattern="M-d-yy" value="${item.modified}" /></div>
                    </td>
                    <td>
                    <div align="left"><c:if
                        test="${item.reviewer==null}"> &nbsp; </c:if> <c:out
                        value="${item.reviewer.displayName}" /></div>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <br />


    </form>
</c:if>


<script type="text/javascript">

var ctr=1;
var sequence=1000;

var sort_state=new Array();

function sort(tableBodyId,column,offset) {
   var desc=false;
   var index=[tableBodyId,column,offset]
   
   if (sort_state[index]>0) {
         desc=sort_state[index]==1;
   }
   
   if (desc) {
      sort_state[index]=2;
   } else {
      sort_state[index]=1;
   }

   var tb=document.getElementById(tableBodyId);
   
   var rows=tb.getElementsByTagName("tr");
   
   var i;
   var row;
   var len=rows.length;
   var index = new Array(len);

   for (i=0; i<len; i++) {
      row=rows[i];
      var tdata=row.getElementsByTagName("td");
      
      var next=tdata[column];
      
      /* the index contains the row_ids + keys */
      row.setAttribute("id","row_"+i);
            
      if (navigator.appName == "Microsoft Internet Explorer")
      {
		//alert(tdata[column].childNodes[offset].childNodes[0].nodeValue);
       	index[i]=[row.getAttribute("id"),tdata[column].childNodes[offset].childNodes[0].nodeValue];
      }
	  else
	  {
	    //alert(tdata[column].firstChild.nextSibling.firstChild.nodeValue);
		index[i]=[row.getAttribute("id"),tdata[column].firstChild.nextSibling.firstChild.nodeValue];
	  }
      
   }
   index.sort(sort2nd);
 
  if (desc) {
      index.reverse();
   }
   
   for (i=0; i<len; i++) {
    /* alert("row--> "+index[i][0]);*/
      row=document.getElementById(index[i][0]);
      tb.appendChild(row);
   }
   
}

function sort2nd(a,b) 
{

  if ((a[1] != null) && (b[1] != null) && (isDate( TrimString(a[1]), "M-d-yy") || isDate( TrimString(b[1]), "M-d-yy") ))
  {
  	if (isDate( TrimString(a[1]), "M-d-yy"))
  	{
  		var startDashA = a[1].indexOf("-");
  		var endDashA   = a[1].lastIndexOf("-");
  		var yearA      = a[1].substring(endDashA + 1);
  		var monthA     = a[1].substring(0,startDashA);
  		var dayA       = a[1].substring(startDashA + 1,endDashA);
  	}
  	else
  	{
  		var yearA      = 0;
  		var monthA     = 0;
  		var dayA       = 0;
  	}
  
  	if (isDate( TrimString(b[1]), "M-d-yy"))
  	{
  		var startDashB = b[1].indexOf("-");
  		var endDashB   = b[1].lastIndexOf("-");
  		var yearB      = b[1].substring(endDashB + 1);
  		var monthB     = b[1].substring(0,startDashB);
  		var dayB       = b[1].substring(startDashB + 1,endDashB);
  	}
  	else
  	{
  		var yearB      = 0;
  		var monthB     = 0;
  		var dayB       = 0;
  	}
  	
  	

	if ( parseInt(yearA) < parseInt(yearB) ) return -1;
	else if ( parseInt(yearA) > parseInt(yearB) ) return 1;
	else 
	{
		if ( parseInt(monthA) < parseInt(monthB) ) return -1;
		else if ( parseInt(monthA) > parseInt(monthB) ) return 1;
		else
		{
			if ( parseInt(dayA) < parseInt(dayB) ) return -1;
			if ( parseInt(dayA) > parseInt(dayB) ) return 1;
		}
		
	}
	
	return 0;
 	
  }
  else
  {
	if (String(a[1])<String(b[1])) return -1;
  	if (String(a[1])>String(b[1])) return 1;
 
  	return 0;
  }
}

function isDate(val,format) 
{

	var date=getDateFromFormat(val,format);

	if (date==0) 
	{ 
		return false; 
	}
	return true;
}

function TrimString(sInString) {
  sInString = sInString.replace( /^\s+/g, "" );// strip leading
  return sInString.replace( /\s+$/g, "" );// strip trailing
}


function getDateFromFormat(val,format) {
	val=val+"";
	format=format+"";
	var i_val=0;
	var i_format=0;
	var c="";
	var token="";
	var token2="";
	var x,y;
	var now=new Date();
	var year=now.getYear();
	var month=now.getMonth()+1;
	var date=1;
	var hh=now.getHours();
	var mm=now.getMinutes();
	var ss=now.getSeconds();
	var ampm="";
	
	while (i_format < format.length) {
		// Get next token from format string
		c=format.charAt(i_format);
		token="";
		while ((format.charAt(i_format)==c) && (i_format < format.length)) {
			token += format.charAt(i_format++);
			}
		// Extract contents of value based on format token
		if (token=="yyyy" || token=="yy" || token=="y") {
			if (token=="yyyy") { x=4;y=4; }
			if (token=="yy")   { x=2;y=2; }
			if (token=="y")    { x=2;y=4; }
			year=_getInt(val,i_val,x,y);
			if (year==null) { return 0; }
			i_val += year.length;
			if (year.length==2) {
				if (year > 70) { year=1900+(year-0); }
				else { year=2000+(year-0); }
				}
			}
		else if (token=="MMM"||token=="NNN"){
			month=0;
			for (var i=0; i<MONTH_NAMES.length; i++) {
				var month_name=MONTH_NAMES[i];
				if (val.substring(i_val,i_val+month_name.length).toLowerCase()==month_name.toLowerCase()) {
					if (token=="MMM"||(token=="NNN"&&i>11)) {
						month=i+1;
						if (month>12) { month -= 12; }
						i_val += month_name.length;
						break;
						}
					}
				}
			if ((month < 1)||(month>12)){return 0;}
			}
		else if (token=="EE"||token=="E"){
			for (var i=0; i<DAY_NAMES.length; i++) {
				var day_name=DAY_NAMES[i];
				if (val.substring(i_val,i_val+day_name.length).toLowerCase()==day_name.toLowerCase()) {
					i_val += day_name.length;
					break;
					}
				}
			}
		else if (token=="MM"||token=="M") {
			month=_getInt(val,i_val,token.length,2);
			if(month==null||(month<1)||(month>12)){return 0;}
			i_val+=month.length;}
		else if (token=="dd"||token=="d") {
			date=_getInt(val,i_val,token.length,2);
			if(date==null||(date<1)||(date>31)){return 0;}
			i_val+=date.length;}
		else if (token=="hh"||token=="h") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<1)||(hh>12)){return 0;}
			i_val+=hh.length;}
		else if (token=="HH"||token=="H") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<0)||(hh>23)){return 0;}
			i_val+=hh.length;}
		else if (token=="KK"||token=="K") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<0)||(hh>11)){return 0;}
			i_val+=hh.length;}
		else if (token=="kk"||token=="k") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<1)||(hh>24)){return 0;}
			i_val+=hh.length;hh--;}
		else if (token=="mm"||token=="m") {
			mm=_getInt(val,i_val,token.length,2);
			if(mm==null||(mm<0)||(mm>59)){return 0;}
			i_val+=mm.length;}
		else if (token=="ss"||token=="s") {
			ss=_getInt(val,i_val,token.length,2);
			if(ss==null||(ss<0)||(ss>59)){return 0;}
			i_val+=ss.length;}
		else if (token=="a") {
			if (val.substring(i_val,i_val+2).toLowerCase()=="am") {ampm="AM";}
			else if (val.substring(i_val,i_val+2).toLowerCase()=="pm") {ampm="PM";}
			else {return 0;}
			i_val+=2;}
		else {
			if (val.substring(i_val,i_val+token.length)!=token) {return 0;}
			else {i_val+=token.length;}
			}
		}
	// If there are any trailing characters left in the value, it doesn't match
	if (i_val != val.length) { return 0; }
	// Is date valid for month?
	if (month==2) {
		// Check for leap year
		if ( ( (year%4==0)&&(year%100 != 0) ) || (year%400==0) ) { // leap year
			if (date > 29){ return 0; }
			}
		else { if (date > 28) { return 0; } }
		}
	if ((month==4)||(month==6)||(month==9)||(month==11)) {
		if (date > 30) { return 0; }
		}
	// Correct hours value
	if (hh<12 && ampm=="PM") { hh=hh-0+12; }
	else if (hh>11 && ampm=="AM") { hh-=12; }
	var newdate=new Date(year,month-1,date,hh,mm,ss);
	return newdate.getTime();
}

function _getInt(str,i,minlength,maxlength) {
	for (var x=maxlength; x>=minlength; x--) {
		var token=str.substring(i,i+x);
		if (token.length < minlength) { return null; }
		if (_isInteger(token)) { return token; }
		}
	return null;
}

function _isInteger(val) {
	var digits="1234567890";
	for (var i=0; i < val.length; i++) {
		if (digits.indexOf(val.charAt(i))==-1) { return false; }
		}
	return true;
}


</script>

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

    <c:set var="show_progress" value="true" />
    <c:set var="wizardTitle" value="Intellectual Growth" />
    
    <%@ include file="/WEB-INF/jsp/matrix/reflection/wizardHeader.inc" %>
    
    <script type="text/javascript">
        regularInit = false;
        var myeditor = null;
    </script>
    <form name="wizardform" method="post" onsubmit="return true;" action="<osp:url value="reflect.osp" />">
    
        <input type="hidden" name="direction" value=""/>
       <h4>Intellectual Growth</h4>
          <p class="longtext">
            <osp-h:glossary link="true" hover="true">
            <p class="instruction">
              How has your understanding of <c:out value="${reflect.cell.scaffoldingCell.rootCriterion.description}"/> changed 
              as a result of your having created the above evidence? Please try to keep 
              your response between 150 and 350 words.</p>
              </osp-h:glossary>
                <spring:bind path="reflect.growthStatement">
                    <table><tr>
                    <td><textarea id="<c:out value="${status.expression}"/>" 
                            name="<c:out value="${status.expression}"/>" cols="75" 
                            rows="15"><c:out value="${reflect.growthStatement}"/></textarea></td>
                    </tr></table>
                </spring:bind>
          </p>

          <table width="550" border="0">
            <tr>
              <td width="50">&nbsp;</td>
              <td width="490" align="left"> 
                <p class="act">
                    <input type="button" value="Word Count" onClick="countWords();" name="button"/>
                    <input name="wordCount" value=""/>
                </p>
              </td>
            </tr>
          </table>
          <p>
              <c:if test="${empty reflect.cell.scaffoldingCell.expectations}">
            		<c:set var="suppress_previous" value="true" />
              </c:if>
              <c:set var="suppress_submit" value="true" />
              <%@ include file="/WEB-INF/jsp/matrix/reflection/wizardFooter.inc" %>
          </p>
    </form>

<%@ include file="reflectionHtmlArea.inc" %>

<script type="text/javascript" defer="1">


function initEditor(fieldName) {
//	var localConfig = config

/*
config.registerButton(
   "wordcount",
   "Count words",
   _editor_url + config.imgURL + "wordcount.gif",
   false,
   function(editor){ countWords(editor.getHTML()); }
);

config.toolbar.push([ "separator", "wordcount" ]);
*/
  myeditor = new HTMLArea(fieldName, config);
  // comment the following two lines to see how customization works
  myeditor.generate();
  return false;
}

function trimAll( strValue ) {
/************************************************
DESCRIPTION: Removes leading and trailing spaces.

PARAMETERS: Source string from which spaces will
  be removed;

RETURNS: Source string with whitespaces removed.
*************************************************/
 var objRegExp = /^(\s*)$/;

    //check for all spaces
    if(objRegExp.test(strValue)) {
       strValue = strValue.replace(objRegExp, '');
       if( strValue.length == 0)
          return strValue;
    }

   //check for leading & trailing spaces
   objRegExp = /^(\s*)([\W\w]*)(\b\s*$)/;
   if(objRegExp.test(strValue)) {
       //remove leading and trailing whitespace characters
       strValue = strValue.replace(objRegExp, '$2');
    }
  return strValue;
}

function countWords()
{
	var text = myeditor.getHTML();
   var trimmedStr = trimAll(text);
   var non_alphanumerics_rExp = rExp = /[^A-Za-z0-9]+/gi;
   var cleanedStr = trimmedStr.replace(/<br \/>/g, '&nbsp;');//Replace br with a space 
   cleanedStr = cleanedStr.replace(/<(.+?)>/g, ' ');//Don't count HTML tags 
   cleanedStr = cleanedStr.replace(/&nbsp;/g, ' ');//Count nbsp; as one keystroke
   cleanedStr = cleanedStr.replace(/'/g, '');//replace apostrophe with nothing
   cleanedStr = cleanedStr.replace(non_alphanumerics_rExp, " ");
   cleanedStr = trimAll(cleanedStr);

   var splitString = cleanedStr.split(" ");
   var word_count = splitString.length;

   document.wizardform.wordCount.value= word_count;
}

initEditor("growthStatement");


</script>


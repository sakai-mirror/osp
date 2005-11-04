
<script type="text/javascript">
  _editor_url = "/osp-matrix-tool/js/htmlarea/";
  _editor_lang = "en";
</script>
<script type="text/javascript" src="/osp-matrix-tool/js/htmlarea/htmlarea.js"></script>
<!--<script type="text/javascript" src="/sakai-chef-tool/htmlarea/sakai-htmlarea.js"></script>-->

<script type="text/javascript" defer="1">
var config = new HTMLArea.Config(); // create a new configuration object

config.pageStyle ='body { background-color: white; color: black; font-family: verdana,sans-serif;bfont-size: 10px; background-color: #ffffff; border: 1px solid #666666;border-style: 1px solid black;} ';

// register new buttons
config.registerButton(
   "insertartifactfield",
   "Insert item field",
   _editor_url + config.imgURL + "insert.gif",
   false,
   function(editor) {
      editor.insertHTML(editor.artifactfieldValue);
   }
);
config.registerButton(
   "insertfile",
   "Insert file",
   _editor_url + config.imgURL + "insert.gif",
   false,
   function(editor) {
      editor.insertHTML(editor.filedropdownValue);
   }
);

// register new drop downs

var the_options = {
"select a field to insert" : ""
<c:forEach var="itemDef" items="${template.items}" >
   <c:forEach var="element" items="${elements[itemDef.name]}" >
,"<c:out value="${element}"/>" : "<c:out value="${element}"/>"
   </c:forEach>
</c:forEach>
};
var artifactfield = {
	id                 : "artifactfield",
	tooltip            : "tooltip",
	options            : the_options,
	action             :
      function(editor, value) {editor.artifactfieldValue =  value; },
   refresh            : function(editor){}
};

config.registerDropdown(artifactfield);


var file_options = {
"select a file url to insert" : ""
<c:forEach var="file" items="${reflect.cellArtifacts}" >
,"<c:out value="${file.displayName}"/>" : "<a href='<c:out value="${file.externalUri}"/>'><c:out value="${file.displayName}"/></a>"
</c:forEach>
};

var filedropdown = {
	id                 : "filedropdown",
	tooltip            : "tooltip",
	options            : file_options,
	action             :
      function(editor, value) {editor.filedropdownValue =  value; },
   refresh            : function(editor){}
};

config.registerDropdown(filedropdown);

config.toolbar = [
[ "fontname", "space",
  "fontsize", "space",
  "bold", "italic", "underline", "strikethrough", "separator",
  "subscript", "superscript", "separator",
  "justifyleft", "justifycenter", "justifyright", "justifyfull", "separator",
   ],
[ "copy", "cut", "paste", "space", "undo", "redo", "separator", "orderedlist", "unorderedlist", "outdent", "indent",
  "inserthorizontalrule", "createlink", "insertimage","inserttable", "htmlmode", "separator",
  "showhelp", "about"
   ]
,[ "filedropdown","insertfile" ]

];

config.statusBar = false;
config.sizeIncludesToolbar = false;
config.killWordOnPaste = true;

// instantiate the widget
if (regularInit) {
	HTMLArea.replaceAll(config);
}

</script>
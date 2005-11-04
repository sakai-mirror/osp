

<script type="text/javascript">
  _editor_url = "<c:url value="/js/htmlarea/"/>";
  _editor_lang = "en";
</script>
<script type="text/javascript" src="<c:url value="/js/htmlarea/htmlarea.js"/>"></script>


<script type="text/javascript" defer="1">
var config = new HTMLArea.Config(); // create a new configuration object
         
         
                            // having all the default values

/*

config.width = '550px';
config.height = '200px';

config.toolbar = [
[ "fontname", "space",
  "fontsize", "space",
  "formatblock", "space",
  "bold", "italic", "underline", "separator",
  "strikethrough", "subscript", "superscript", "separator",
  "copy", "cut", "paste", "space", "undo", "redo" ],
		
[ "justifyleft", "justifycenter", "justifyright", "justifyfull", "separator",
  "insertorderedlist", "insertunorderedlist", "outdent", "indent", "separator",
  "forecolor", "hilitecolor", "textindicator", "separator",
  "inserthorizontalrule", "createlink", "insertimage", "inserttable", "htmlmode" ]
];



// the following sets a style for the page body (black text on yellow page)
// and makes all paragraphs be bold by default

*/
config.pageStyle ='body { background-color: white; color: black; font-family: verdana,sans-serif;bfont-size: 10px; background-color: #ffffff; border: 1px solid #666666;border-style: 1px solid black;} ';



	config.toolbar = [
	[ "fontname", "space",
	  "fontsize", "space",
	  "bold", "italic", "underline", "strikethrough", "separator",
	  "subscript", "superscript", "separator",
	  "justifyleft", "justifycenter", "justifyright", "justifyfull", "separator"
	  ],

	[ 
	  "orderedlist", "unorderedlist", "outdent", "indent",
	  "inserthorizontalrule", "createlink", "insertimage", "htmlmode", "separator", 
	  "showhelp", "about" ]
	];
	
	/*	
	config.registerButton({
	  id        : "my-hilite",
	  tooltip   : "Highlight text",
	  image     : "ed_help.gif",
	  textMode  : false,
	  action    : function(editor, id) {
	                editor.surroundHTML('<ins>', '</ins>');
	              }
	});
	*/
	
	


	config.statusBar = false;
	config.sizeIncludesToolbar = false;
	config.killWordOnPaste = true;
	
	// instantiate the widget
   HTMLArea.replaceAll(config);
   
</script>



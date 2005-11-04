/**********************************************************************************
*
* $Header: /opt/CVS/osp2.x/matrix/tool/src/webapp/js/htmlarea/sakai-htmlarea.js,v 1.1 2005/07/29 17:52:27 chmaurer Exp $
*
***********************************************************************************
@license@
**********************************************************************************/

// This Sakai-specific JavaScript does setup for using the HTMLArea JavaScript library for formatted text editing
// within Sakai

if (document.htmlareascriptsetup == undefined)
{
	document.htmlareascriptsetup = true;

	// This section will only be included ONCE per HTML document that contains formatted text editing widget(s)
	_editor_url = "/sakai-chef-tool/htmlarea/";
	_editor_lang = "en";
	
	// funky way to include the HTMLArea JavaScript library, from within JavaScript
	document.write('<script type="text/javascript" src="/sakai-chef-tool/htmlarea/htmlarea.js"></script>');
}

// textarea_id - The HTML id of the plain-old text area that should be turned into a fancy formatted text editing widget
function chef_setupformattedtextarea(textarea_id)
{
	// HTMLArea doesn't work in the Camino browser,
	// and it doesn't know that it doesn't work - so disable it
	var is_camino = /Camino/i.test(navigator.userAgent);
	if (is_camino)
	{
		return;
	}

	
	// configure the widget
	var config = new HTMLArea.Config();
	
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
	HTMLArea.replace(textarea_id, config);
}

/**********************************************************************************
*
* $Header: /opt/CVS/osp2.x/matrix/tool/src/webapp/js/htmlarea/sakai-htmlarea.js,v 1.1 2005/07/29 17:52:27 chmaurer Exp $
*
**********************************************************************************/

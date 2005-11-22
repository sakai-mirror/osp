function sakaiRegisterResourceList(config, image, resources) {
  var filedropdown = {
     id                 : "filedropdown",
     tooltip            : "tooltip",
     options            : resources,
     action             :
        function(editor) {updateValue(editor, this)},
     refresh            : function(editor){;}
  };

  config.registerDropdown(filedropdown);

  config.registerButton(
     "insertfile",
     "Insert file",
     image,
     false,
     function(editor) {
        editor.insertHTML(editor.filedropdownValue);
     }
  );

}

function updateValue(editor, obj) {
   var value = editor._toolbarObjects[obj.id].element.value;
   editor.filedropdownValue = value;
}

function resetEditor(clientId, config) {

   for (i=0;i<document.htmlareas.length;i++){
      if (document.htmlareas[i][0] == clientId) {
         var editor = document.htmlareas[i][1];
         editor.setMode();
         editor.setMode();
      }
   }

}
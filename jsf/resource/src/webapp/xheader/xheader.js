
function showHideDiv(divNo, context)
  {
  //alert(divNo);
  var tmpdiv = divNo;
  var tmpimg = "img" + divNo;
  var divisionNo = getTheElement(tmpdiv);
  var imgNo = getTheElement(tmpimg);
  if(divisionNo)
    {
    //alert(divisionNo.style.display);
    if(divisionNo.style.display =="none")
      {
      //alert("in if");
      divisionNo.style.display="block";
      imgNo.src = context + "/xheader/images/xheader_mid_show.gif";
      }
    else
      {
      //alert("in else");
      divisionNo.style.display ="none";
      imgNo.src = context + "/xheader/images/xheader_mid_hide.gif";
      }
    }
  }
  
  // getElementById with special handling of old browsers
function getTheElement(thisid){

  var thiselm = null;

  if (document.getElementById)
  {
    // browser implements part of W3C DOM HTML ( Gecko, Internet Explorer 5+, Opera 5+
    thiselm = document.getElementById(thisid);
  }
  else if (document.all){
    // Internet Explorer 4 or Opera with IE user agent
    thiselm = document.all[thisid];
  }
  else if (document.layers){
    // Navigator 4
    thiselm = document.layers[thisid];
  }

  if(thiselm)  {

    if(thiselm == null)
    {
      return;
    }
    else
    {
      return thiselm;
    }
  }
}
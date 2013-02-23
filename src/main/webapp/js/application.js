$(document).ready(function(){
  $('.chzn-select').chosen();
  
  $(".view_opts").on('change','select[name!=pagenum_select],input[type=checkbox],input[type=text]',submitParentForm);
  $(".view_opts").on('click','.pagination-container a',function(e){
      if (!($(this).hasClass("disabled"))){
        $.proxy(submitParentForm,this)(e,{pagenum: $(this).data("pagenum")});
      }
      return false;
    });
  $(".view_opts").on('change','select[name=pagenum_select]',function(e){
	  $.proxy(submitParentForm,this)(e,{pagenum: $(this).val()});
  });
  
  function submitParentForm(e,args){
	  args = args || {};
	  var form = $(this).closest('form');
	  window.location.href = form.attr('action') + '?' + 
	  	form.find('select[name!=pagenum_select],input').serialize() + 
	  	"&" + $.param(args);
  }
});

window.MouseConf = window.MouseConf || {};
MouseConf.mice = MouseConf.mice || {};
MouseConf.requests = MouseConf.requests || {};
MouseConf.submissions = MouseConf.submissions || {};
MouseConf.holders = MouseConf.holders || {};

window.MouseConf.addSubmissions = function(submissions) {
  $.each(submissions, function(i,sub){
    MouseConf.submissions[sub.submissionID] = sub;
  });
};

window.MouseConf.addMice = function(mice) {  
  $.each(mice, function(i,mouse){
    MouseConf.mice[mouse.mouseID] = mouse;
  });
}

window.MouseConf.addChangeRequests = function(requests) {  
  $.each(requests, function(i,request){
    MouseConf.requests[request.requestID] = request;
  });
}

window.MouseConf.addHolders = function(holders) {  
  $.each(holders, function(i,holder){
    MouseConf.holders[holder.holderID] = holder;
  });
}

function setCookie(c_name,value,exdays)
{
  var exdate=new Date();
  exdate.setDate(exdate.getDate() + exdays);
  var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
  document.cookie=c_name + "=" + c_value;
}

function getCookie(c_name)
{
var i,x,y,ARRcookies=document.cookie.split(";");
  for (i=0;i<ARRcookies.length;i++)
  {
    x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
    y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
    x=x.replace(/^\s+|\s+$/g,"");
    if (x==c_name)
    {
      return unescape(y);
    }
  }
}
$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};
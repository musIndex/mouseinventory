$(document).ready(function(){
  $('.chzn-select').chosen();
  
  $(".view_opts").on('change','select',function(){
    var form = $(this).closest('form');
    window.location.href = form.attr('action') + '?' + form.serialize();
  });
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
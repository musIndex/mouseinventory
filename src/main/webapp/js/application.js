$(document).ready(function(){
  $('.chzn-select').chosen();
});

window.MouseConf = window.MouseConf || {};
MouseConf.mice = MouseConf.mice || {};
MouseConf.requests = MouseConf.requests || {};
MouseConf.submissions = MouseConf.submissions || {};

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
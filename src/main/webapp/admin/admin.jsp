<%@page import="edu.ucsf.mousedatabase.dataimport.ImportHandler"%>
<%@page import="edu.ucsf.mousedatabase.dataimport.ImportDefinition"%>
<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="java.util.*" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("admin.jsp", true) %>


<%
  DBConnect.initialTable();
  ArrayList<ArrayList<SubmittedMouse>> submissionLists = new ArrayList<ArrayList<SubmittedMouse>>();
 
  ArrayList<String> submissionListLabels = new ArrayList<String>();
  


  ArrayList<ArrayList<ChangeRequest>> changeRequestLists = new ArrayList<ArrayList<ChangeRequest>>();
  changeRequestLists.add(DBConnect.getChangeRequests("new",null, "Request form"));
  changeRequestLists.add(DBConnect.getChangeRequests("pending",null, "Request form")) ;
  String[] changeRequestListLabels = new String[]{"new","pending"};

  StringBuffer buf = new StringBuffer();
  
  Map<String, String> sourceStatuses = new HashMap<String,String>();
  sourceStatuses.put("new", "new requests");
  sourceStatuses.put("pending","pending requests");
  
  for (String sourceStatus : sourceStatuses.keySet()){
  ArrayList<ArrayList<String>> openRequestSources = DBConnect.getOpenRequestSources(sourceStatus);
    if (openRequestSources.size() > 0) {
        buf.append("<br>");
  	  buf.append("<dl>"); 
  
      for(ArrayList<String> source : openRequestSources) {
    	String sourceName = source.get(0);
        int count = Integer.parseInt(source.get(1));
        if (sourceName.equals("Change request form")) {
          continue;
        }
        for(ImportDefinition def : ImportHandler.getImportDefinitions()) {
        	sourceName = sourceName.replace(def.Name, "");
        }
        
        buf.append("<dt>There " + (count > 1 ? "are" : "is") + " <b><a href='" + adminRoot + "ManageChangeRequests.jsp?status=" + sourceStatus + "&requestSource=" 
              + sourceName + "'>" + count + " " + sourceStatuses.get(sourceStatus)
              + " </a></b> from data upload <b>" + sourceName + "</b></dt>");
      }
      buf.append("</dl>");
    }
  }
  
  sourceStatuses = new HashMap<String,String>();
  sourceStatuses.put("new", "new submissions");
  sourceStatuses.put("need more info","submissions on hold");
  
  for (String sourceStatus : sourceStatuses.keySet()){
    ArrayList<ArrayList<String>> openSubmissionSources = DBConnect.getOpenSubmissionSources(sourceStatus);
    if (openSubmissionSources.size() > 0) {
        buf.append("<br>");
  	  buf.append("<dl>"); 
  
      for(ArrayList<String> source : openSubmissionSources) {
    	String sourceName = source.get(0);
    	int count = Integer.parseInt(source.get(1));
        if (sourceName.equals(SubmittedMouse.SubmissionFormSource)) {
          continue;
        }
        for(ImportDefinition def : ImportHandler.getImportDefinitions()) {
        	sourceName = sourceName.replace(def.Name, "");
        }
        
        if (sourceStatus.equals("new")) {
          buf.append("<dt>There " + (count > 1 ? "are" : "is") + " <b><a href='" + adminRoot + "ListSubmissions.jsp?status=" + sourceStatus + "&submissionSource=" 
                + sourceName + "'>" + count + " " + sourceStatuses.get(sourceStatus)
                + " </a></b> from data upload <b>" + sourceName + "</b></dt>");
        }
        else {
         
        	submissionLists.add(DBConnect.getMouseSubmissions("need more info",null,null,source.get(0)));
        	submissionListLabels.add("submissions on hold from data upload " + sourceName);
        }
      }
      buf.append("</dl>");
    }
  }
  
  
  submissionLists.add(DBConnect.getMouseSubmissions("new",null,null,SubmittedMouse.SubmissionFormSource));
  submissionLists.add(DBConnect.getMouseSubmissions("need more info",null,null,SubmittedMouse.SubmissionFormSource));
  submissionListLabels.add("new manual submissions");
  submissionListLabels.add("manual submissions on hold");

  for (int i = 0; i < changeRequestLists.size();i++)
  {
    ArrayList<ChangeRequest> changeRequests = changeRequestLists.get(i);
    String label = changeRequestListLabels[i];
    if(changeRequests.size() > 0)
    {
      buf.append("<br>");
      buf.append("<dl>");
      buf.append("<dt><font color='green'><b>There are " + changeRequests.size() + " " + label + " manual change requests:</b></font></dt>");
      for(ChangeRequest changeRequest : changeRequests)
      {
        String changeRequestTitle = changeRequest.getFirstname() + " " + changeRequest.getLastname() + " requested: " +
          changeRequest.actionRequested().label + " ";
        if (changeRequest.actionRequested() == ChangeRequest.Action.ADD_HOLDER ||
            changeRequest.actionRequested() == ChangeRequest.Action.REMOVE_HOLDER ||
            changeRequest.actionRequested() == ChangeRequest.Action.CHANGE_CRYO_LIVE_STATUS) {
         changeRequestTitle += changeRequest.getHolderFirstname() + " " + changeRequest.getHolderLastname();
        }
        
        
        ArrayList<MouseRecord> mouse = DBConnect.getMouseRecord(changeRequest.getMouseID());
        if(mouse.size() > 0)
        {
          buf.append("<dd>" + changeRequestTitle
              +  ": <span class='mouseName'>" + HTMLGeneration.emptyIfNull(mouse.get(0).getMouseName()) + "</span> "
              + "<a href=\"CompleteChangeRequest.jsp?id="
              + changeRequest.getRequestID() + "\">Edit record #"
              + changeRequest.getMouseID() + "</a>");
        }
      }
      buf.append("</dl>");
    }
  }
  for(int i =0; i< submissionLists.size(); i++)
  {
    ArrayList<SubmittedMouse> newSubmissions = submissionLists.get(i);
    String label = submissionListLabels.get(i);
    if(newSubmissions.size() > 0)
    {

      buf.append("<br><dl>");
      buf.append("<dt><font color='green'><b>There are " + newSubmissions.size() + " " + label + ":</b></font></dt>");
      for(SubmittedMouse mouse : newSubmissions)
      {
        String mouseName = "";
        if (mouse.getOfficialSymbol() == null || mouse.getOfficialSymbol().isEmpty())
        {
          mouseName = mouse.getMouseName();
          if(mouseName == null || mouseName.isEmpty())
          {
            mouseName = mouse.getOfficialMouseName();
          }
        }
        String action = "held by";
        if (!mouse.getSubmissionSource().equals(SubmittedMouse.SubmissionFormSource))
        {
          if (mouse.getSubmissionSource().contains("PDU") || mouse.getSubmissionSource().contains("Purchase"))
          {
            action = "purchased by";
          }
          else if (mouse.getSubmissionSource().contains("IDU")){
           action = "imported by";
          }

        }
        String holders = "";
        ArrayList<MouseHolder> mouseHolders = mouse.getHolders();
        if (mouse.getHolderName() != null && !mouse.getHolderName().equals("unassigned"))
        {
          holders += mouse.getHolderName();
        }
        if (mouseHolders != null)
        {
          for (MouseHolder holder : mouseHolders)
          {
            if (holders.length() > 0) holders += ", ";
            holders += holder.getFullname();
          }
        }
        buf.append("<dd><span class='mouseName'>" + mouseName
            + HTMLUtilities.getCommentForDisplay(HTMLGeneration.emptyIfNull(mouse.getOfficialSymbol()))
            + "</span> " + action + "  " + holders
            + " - <a href=\"CreateNewRecord.jsp?id=" + mouse.getSubmissionID() + "\">Convert to record</a></dd>");
      }
      buf.append("</dl>");
    }
  }


%>
<div class="site_container">
<h2>Welcome to Rodent Inventory Administration.</h2>
Administer the Rodent Inventory by choosing from the menu items above.<br>

<%=buf.toString() %>
</div>

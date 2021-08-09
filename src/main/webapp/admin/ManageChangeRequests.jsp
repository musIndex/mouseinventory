<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.ResultSet" %>

<%=getPageHeader(null, false, true) %>
<%=getNavBar("ManageChangeRequests.jsp", true) %>
<%@ include file='SendMailForm.jspf' %>

    <%
        String newStatus = request.getParameter("newStatus");
        String idToUpdate = request.getParameter("idToUpdate");
        String updateMessage = "";

        String status = request.getParameter("status");
        String orderBy = request.getParameter("orderby");
        String requestSource = request.getParameter("requestSource");
        int currentHolderId = -1;

        int pagenum = HTMLGeneration.stringToInt(request.getParameter("pagenum"));
        int limit = HTMLGeneration.stringToInt(request.getParameter("limit"));
        if (limit == -1) {
            limit = 25;
        }
        if (pagenum == -1) {
            pagenum = 1;
        }
        int offset = limit * (pagenum - 1);
        if (status == null) {
            if ((status = (String) session.getAttribute("manageChangeRequestStatus")) == null) {
                status = "new";
            }
        }
        session.setAttribute("manageChangeRequestStatus", status);
        if (orderBy == null) {
            if ((orderBy = (String) session.getAttribute("manageChangeRequestOrderBy")) == null) {
                orderBy = "changerequest.id";
            }
        } else {
            session.setAttribute("manageChangeRequestOrderBy", orderBy);
        }
        if (requestSource == null) {
            if ((requestSource = (String) session.getAttribute("manageChangeRquestRequestSource")) == null) {
                requestSource = "all";
            }
        } else {
            session.setAttribute("manageChangeRequestRequestSource", requestSource);
        }

        if (request.getParameter("holder_id") == null && session.getAttribute("manageChangeRequestHolderId") != null) {
            currentHolderId = (Integer) session.getAttribute("manageChangeRequestHolderId");
        } else {
            currentHolderId = stringToInt(request.getParameter("holder_id"));
        }
        session.setAttribute("manageChangeRequestHolderId", currentHolderId);

        String[] sortOptions = new String[]{"changerequest.id", "requestdate", "requestdate DESC", "mouse_id", "mouse_id DESC", "firstname", "lastname"};
        String[] sortOptionNiceNames = new String[]{"Request #", "Request date", "Reverse request date", "Record #", "Reverse Record #", "Requestor first name", "Requestor last name"};

        String[] filterOptions = new String[]{"new", "pending", "done", "all"};
        String[] filterOptionNiceNames = new String[]{"New", "Pending", "Completed", "All"};


        StringBuffer sortBuf = new StringBuffer();
        sortBuf.append("<form class='view_opts' action='ManageChangeRequests.jsp' method='get'>");
        sortBuf.append("<div class=\"mousetype_selection_links\" style=\"width:25%;padding-bottom:10px\"><ul class=\"label_text\" style=\"columns:1;font-size:16px\">");
        sortBuf.append("<li style=\"margin-top:0px\">Show:");
        sortBuf.append(genSelect("status",filterOptions,filterOptionNiceNames, status,"",true));
        sortBuf.append("</li><li style=\"margin: 6px 0;\">Sort by:");
        sortBuf.append(genSelect("orderby",sortOptions,sortOptionNiceNames, orderBy,"",true));
        sortBuf.append("</li>");
        sortBuf.append("</ul></div>");

//        sortBuf.append("<form class='view_opts' action='ManageChangeRequests.jsp'>");
//        sortBuf.append("&nbsp;Show: ");
//        sortBuf.append(genSelect("status", filterOptions, filterOptionNiceNames, status, null, false));
//        sortBuf.append("&nbsp;Source: ");
//        sortBuf.append("<input name='requestSource' style='width: 200px' type='text' value='" + (requestSource.equals("all") ? "" : requestSource) + "'>");
//        sortBuf.append("&nbsp;<input class='btn' type='submit' value='Update'>");
//        sortBuf.append("&nbsp;<a id='clearSource' class='btn'>Clear</a>");
//        //sortBuf.append("&nbsp;Filter by holder: ");
//        //sortBuf.append(getHolderSelect("holder_id", currentHolderId, false));
//        sortBuf.append("&nbsp;Sort by: ");
//        sortBuf.append(genSelect("orderby", sortOptions, sortOptionNiceNames, orderBy, null, false));

        int requestCount = DBConnect.countChangeRequests(status, orderBy, requestSource);

        ArrayList<ChangeRequest> requests = DBConnect.getChangeRequests(status, orderBy, requestSource, limit, offset);

        String topPageSelectionLinks = HTMLGeneration.getNewPageSelectionLinks(limit, pagenum, requestCount, true);
        String bottomPageSelectionLinks = HTMLGeneration.getNewPageSelectionLinks(limit, pagenum, requestCount, false);

        Holder currentHolder = DBConnect.getHolder(currentHolderId);
        if (currentHolder != null) {
            ArrayList<ChangeRequest> temp = new ArrayList<ChangeRequest>();
            for (ChangeRequest req : requests) {
                if (req.getHolderEmail() != null && req.getHolderEmail().equalsIgnoreCase(currentHolder.getEmail())) {
                    temp.add(req);
                }
            }
            requests = temp;
        }

        String newTable = getChangeRequestsTable(requests, status);

        String statusString = status + " change requests";
        if (status.equalsIgnoreCase("done")) {
            statusString = " completed change requests";
        }
        if (!requestSource.equalsIgnoreCase("all") && !requestSource.isEmpty()) {
            statusString += " from source '" + requestSource + "'";
        }
        if (currentHolderId > 0) {
            IHolder holder = DBConnect.getHolder(currentHolderId);
            statusString += " for holder " + holder.getFullname();
        }


        session.setAttribute("manageRequestsLastQuery", "status=" + status + "&orderby=" + orderBy + "&requestSource=" + requestSource);
        session.setAttribute("manageRequestsLastTitle", statusString);
        statusString = "Listing " + statusString;

    %>

<div class="site_container">
    <p class="main_header"><%= statusString %></p>
    <p class="label_text"><%= requestCount %> found.<span id='matching_search'></span></p>
<%--    <%= updateMessage %>--%>
    <form id='search_form' style="float: right">
        <input type="search" placeholder="Search..." style="font-size:120%;vertical-align:top;margin-top: 0px" class="input-xlarge" name="search_terms" id="search_terms">
        <input onclick="" type="image" alt="Submit" src="/img/Eyeglass-black.svg" style="height: 28px;margin: 0px">
    </form>
    <%= sortBuf.toString() %>

    <%= newTable.toString() %>

    <%= bottomPageSelectionLinks %>
</div>
<%=HTMLGeneration.getWebsiteFooter()%>

<script>


    !function ($) {

        $('#clearSource').click(function () {
            $("input[name=requestSource]").val('');
            return false;
        });

        var search_form = $("form#search_form");
        var clear_btn = $("form#search_form a.clear_btn");
        var search_input = $("form#search_form input[type=search]");
        var matching_label = $("#matching_search");
        search_form.submit(function (e) {
            e.preventDefault();
            var term = search_input.val();
            var expr = new RegExp("(^|\\s|-|\\()" + term.trim() + "(\\s|$|\\.|\\?|,|-|\\))", 'i');
            var matchCount = 0;
            $("div.mouseTable tr").removeClass('hide');
            if (!term) {
                clear_btn.hide();
                matching_label.text("");
                return false;
            }
            clear_btn.show();
            $("div.mouseTable tr.changerequestlist, div.mouseTable tr.changerequestlistAlt").each(function (i, row) {
                var $row = $(row);
                if (!$row.text().match(expr)) {
                    $row.addClass('hide');
                } else {
                    matchCount++;
                }
            });
            matching_label.text(" (" + matchCount + " matching quick search '" + term + "')");

            return false;
        });
        clear_btn.click(function () {
            search_input.val('');
            search_form.submit();
        });
    }(jQuery);

    function pageSwitch(num){
        document.getElementById("pagenum").value = num;
        this.form.submit();
    }

    function resetPage(){
        document.getElementById("pagenum").value = 1;
        this.form.submit();
    }

</script>

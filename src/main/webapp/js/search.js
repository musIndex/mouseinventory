$(document).ready(function(){
  var search_form = $("#searchForm");
  var instr_link = $("#show_search_instructions");
  var instr = $(".search-instructions");
  var search_button = $("#search_button");
  var results_div = $("#searchresults");
  var search_box = $('input[name=searchterms]');
  var search_container = $(".search-box");

  instr_link.toggle(show_help,hide_help);
  search_button.click(search_button_clicked);
  $(window).bind("hashchange", function(){search(extract_search_params());});
  $(window).trigger("hashchange");

  function hide_help(){
    instr.hide();
    instr_link.text("how do I search?")
    search_box.focus();
  }
  function show_help(){
    instr.show();
    instr_link.text("hide search help");
    search_box.focus();
  }

  function search_results_loaded(){
    var hash = extract_search_params();

    //todo make the search results a js object, load them here after parsing some stuff out
    if (hash.searchterms) {
      highlight_searchterms(hash.searchterms);
    }
    hide_help(); 
    
    if (hash.searchterms != null && hash.searchterms != "") {
      search_container.addClass("search-box-small");  
    }
    else {
      search_container.removeClass("search-box-small");
    }
    search_container.show();
    
    //update the handlers for the pagination controls, which are returned by the search
    $("select[name=limit]").change(function(){
      $.bbq.pushState({limit:$(this).val(), pagenum:1});
      return false;
    }).chosen();
    $(".pagination-container a").click(function(){
      if (!($(this).hasClass("disabled"))){
        $.bbq.pushState({pagenum:$(this).data("pagenum")});
      }
      return false;
    });
    $("a.search-strategy-show-details").click(function(){
      $(this).siblings().toggle();
      return false;
    })
  }
  
  function highlight_searchterms(searchterms){
    $('.searchresults-mice').each(function(){
      var $results = $(this);
      var $header = $(this).prev();
      var tokens = $header.data('tokens').split(',');
      $results.find(".mouselist, .mouselistAlt").highlight(tokens,{className: 'highlight-searchterm'});
    });

    $("span.highlight-searchterm").parent().parent().each(function(){
      var $element = $(this);
      if($element.is("dt")) {
        if($element.parent().hasClass("mouselist-holderlist")){
          $element.show();
        }
      }
    });
  }
    
  function search_button_clicked(){
    $.bbq.pushState({searchterms: search_box.val(), pagenum: 1});
    return false;
  }
  
  function search(search_query){
    var $this = $(this);

    if (window.searchQuery != search_query ) {
      window.searchQuery = search_query;
      $("#searchresults-container").load('search.jsp?' + $.param(window.searchQuery) + ' #searchresults', search_results_loaded);
    }
    return false;
  } 
  
  function extract_search_params(){
    var hash = $.bbq.getState( );
    var query = $.deparam.querystring();
     
    if ((hash == null || hash.searchterms == undefined) && query != null && query.searchterms != null) {
      hash.searchterms = query.searchterms;
    }
    if (!hash.limit) hash.limit = 25;
    if (!hash.pagenum) hash.pagenum = 1;
    
    search_box.val(hash.searchterms);
    $("select[name=limit]").val(hash.limit);
    return hash;
  }

});
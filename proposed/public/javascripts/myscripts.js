	function showHidePlots(e) {
			var $showHidePlotsButton= $('#showHidePlotsButton');
	
		$('.streamconfig').toggle(); 
		$('.streamplot_overview').toggle(); 
		$('.streamplot').toggle();
		$('.streamNoDataLine').toggle();
		
		var currentTitle = $showHidePlotsButton.text();
		console.debug("currentTitle: " + currentTitle);
		
		var nextTitle = $showHidePlotsButton.attr('data-nextTitle');
				console.debug("nextTitle: " + nextTitle);
		
		$showHidePlotsButton.text(nextTitle);
		$showHidePlotsButton.attr('data-nextTitle', currentTitle);
	}
	
function showAlert(type, msg) {
	$('#mainContainer').before('<div class="container-errormsg"><div class="alert ' + type + '"><a class="close" data-dismiss="alert">×</a>' + msg.data + '</div></div>');
	//$('.container').after('<div class="container-errormsg><div class="' + type + '"><a class="close" data-dismiss="alert">×</a>' + msg + '</div></div>');
}	

function toggleFollowStreamButton(event){
		var $this_button=$(this);
		var my_stream_id=$this_button.attr('parent_id');
		var current_tooltip_title=$this_button.attr('title');
		var next_tooltip_title=$this_button.attr('inactive_title');
		var follow = false;
		if($this_button.hasClass('follow_stream')) {
			follow = true;
			console.debug("Current FollowStream button: " +follow);
		} else {
			follow = false;
			console.debug("Current FollowStream button: " +follow);
		}
		jsRoutes.controllers.CtrlUser.followStream(my_stream_id,follow).ajax({
			dataType : "text",
			success: function(fmsg) {
	    	//$('div.container-errormsg').html('<strong>Reparing Error!</strong>'+fmsg);
				console.debug("FollowStream result: " + my_stream_id + fmsg);
	    	if(fmsg=="true") {
					console.debug("FollowStream result: " + my_stream_id + fmsg);

	    		$this_button.removeClass("icon-star-empty follow_stream");
	    		$this_button.addClass("icon-star unfollow_stream");
	    	} else if(fmsg=="false") {
					console.debug("FollowStream result: " + my_stream_id + fmsg);

	    		$this_button.removeClass("icon-star unfollow_stream");
	    		$this_button.addClass("icon-star-empty follow_stream");
	    	} 
	    },
	    error: function(emsg) {
	    	//$('div.container-errormsg').html('<strong>Error unfollow!</strong>'+emsg);
				console.debug("Error: Follow stream! " + my_stream_id + emsg);
	    	jsRoutes.controllers.CtrlUser.isFollowingStream(my_stream_id).ajax({
	    		dataType : "text",
	  	    success: function(fmsg) {
	  	    	//$('div.container-errormsg').html('<strong>Reparing Error!</strong>'+fmsg);
	  				console.debug("Error: Reparing Error of followStream! " + my_stream_id + fmsg);
	  	    	if(fmsg=="true") {
	  	    		$this_button.removeClass("icon-star-empty follow_stream");
	  	    		$this_button.addClass("icon-star unfollow_stream");
	  	    	} else if(fmsg=="false") {
	  	    		$this_button.removeClass("icon-star unfollow_stream");
	  	    		$this_button.addClass("icon-star-empty follow_stream");
	  	    	} 
	  	    },
	  	    error: function(fmsg) {
	  	    	//$('div.container-errormsg').html('<strong>Error!</strong>'+fmsg);
	  	    	console.debug("Error: isFollowingStream! " + my_stream_id + fmsg);
	  	    }
	  	  });
	    }
	  });
		$('.unfollow_stream').on("click", toggleFollowStreamButton);
		$('.follow_stream').on("click", toggleFollowStreamButton);
	  return false;
	};
	$('.unfollow_stream').on("click", toggleFollowStreamButton);
	$('.follow_stream').on("click", toggleFollowStreamButton);

	function hideStreamList(event){
		$(this).toggleClass("icon-chevron-down icon-chevron-up");
		//$(this).children().find('.icon-chevron-up').toggleClass("icon-chevron-down").toggleClass("icon-chevron-up");
		$(this).parent().find('.stream_list').toggle();
		return false;
	};
	$('.hide_streams').on("click", hideStreamList);
	
	//controllers.CtrlStream.setPublicAccess(id: Long, pub: Boolean)
	function togglePublicAccessStreamButton(event){
		var $this_button=$(this);
		var my_stream_id=$this_button.attr('parent_id');
		var current_tooltip_title=$this_button.attr('title');
		var next_tooltip_title=$this_button.attr('inactive_title');
		var pub = false;
		if($this_button.hasClass('set_public_access_stream')) {
			pub = true;
		}
	  jsRoutes.controllers.CtrlStream.setPublicAccess(my_stream_id, pub).ajax({
	  	dataType : "text",
	    success: function(fmsg) {
	    	if(fmsg=="true") {
	    		$this_button.removeClass("icon-white set_public_access_stream");
	    		$this_button.addClass("remove_public_access_stream");
	    	} else if(fmsg=="false") {
	    		$this_button.removeClass("remove_public_access_stream");
	    		$this_button.addClass("icon-white set_public_access_stream");
	    	} 
//	    	$this_button.attr('title', next_tooltip_title);
//	    	$this_button.attr('inactive_title', current_tooltip_title);
	    	//event.stopImmediatePropagation();
	    	//event.stopPropagation();	    	
	    },
	    error: function(emsg) {
				console.debug("Error: PublicAccess stream! " + my_stream_id + emsg);
	    	//$('div.container-errormsg').html('<strong>Error unfollow!</strong>'+emsg);
	    	jsRoutes.controllers.CtrlStream.isPublicAccess(my_endpoint_id).ajax({
	    		dataType : "text",
	  	    success: function(fmsg) {
	  	    	//$('div.container-errormsg').html('<strong>Reparing Error!</strong>'+fmsg);
	  				console.debug("PublicAccess: Reparing Error! " + my_stream_id + fmsg);
	  	    	if(fmsg=="true") {
	  	    		$this_button.removeClass("icon-white set_public_access_stream");
	  	    		$this_button.addClass("remove_public_access_stream");
	  	    	} else if(fmsg=="false") {
	  	    		$this_button.removeClass("remove_public_access_stream");
	  	    		$this_button.addClass("icon-white set_public_access_stream");
	  	    	} 
	  	    },
	  	    error: function(fmsg) {
	  				console.debug("PublicAccess: Still: Error! " + my_stream_id + fmsg);
	  	    	//$('div.container-errormsg').html('<strong>Error!</strong>'+fmsg);
	  	    }
	  	  });
	    }
	  });
		$('.set_public_access_stream').on("click", togglePublicAccessStreamButton);
		$('.remove_public_access_stream').on("click", togglePublicAccessStreamButton);
	  return false;
	};
	$('.set_public_access_stream').on("click", togglePublicAccessStreamButton);
	$('.remove_public_access_stream').on("click", togglePublicAccessStreamButton);
	
	//controllers.CtrlStream.setPublicSearch(id: Long, pub: Boolean)
	function togglePublicSearchStreamButton(event){
		var $this_button=$(this);
		var my_stream_id=$this_button.attr('parent_id');
		var current_tooltip_title=$this_button.attr('title');
		var next_tooltip_title=$this_button.attr('inactive_title');
		var pub = false;
		if($this_button.hasClass('set_public_search_stream')) {
			pub = true;
		} else {
			pub = false;
		}
	  jsRoutes.controllers.CtrlStream.setPublicSearch(my_stream_id, pub).ajax({
	  	dataType : "text",
	    success: function(fmsg) {
	    	if(fmsg=="true") {
	    		$this_button.removeClass("icon-white set_public_search_stream");
	    		$this_button.addClass("remove_public_search_stream");
	    	} else if(fmsg=="false") {
	    		$this_button.removeClass("remove_public_search_stream");
	    		$this_button.addClass("icon-white set_public_search_stream");
	    	}
	    	//event.stopImmediatePropagation();
	    	//event.stopPropagation();	    	
	    },
	    error: function(emsg) {
				console.debug("Error: PublicSearch stream! " + my_stream_id + emsg);
	    	//$('div.container-errormsg').html('<strong>Error unfollow!</strong>'+emsg);
	    	jsRoutes.controllers.CtrlStream.isPublicSearch(my_endpoint_id).ajax({
	    		dataType : "text",
	  	    success: function(fmsg) {
	  	    	//$('div.container-errormsg').html('<strong>Reparing Error!</strong>'+fmsg);
	  				console.debug("PublicSearch: Reparing Error! " + my_stream_id + fmsg);
	  	    	if(fmsg=="true") {
	  	    		$this_button.removeClass("icon-white set_public_search_stream");
	  	    		$this_button.addClass("remove_public_search_stream");
	  	    	} else if(fmsg=="false") {
	  	    		$this_button.removeClass("remove_public_search_stream");
	  	    		$this_button.addClass("icon-white set_public_search_stream");
	  	    	} 
	  	    },
	  	    error: function(fmsg) {
	  				console.debug("PublicSearch: Still: Error! " + my_stream_id + fmsg);
	  	    	//$('div.container-errormsg').html('<strong>Error!</strong>'+fmsg);
	  	    }
	  	  });
	    }
	  });
		$('.set_public_search_stream').on("click", togglePublicSearchStreamButton);
		$('.remove_public_search_stream').on("click", togglePublicSearchStreamButton);
	  return false;
	};
	$('.set_public_search_stream').on("click", togglePublicSearchStreamButton);
	$('.remove_public_search_stream').on("click", togglePublicSearchStreamButton);
	
//controllers.CtrlStream.clear(stream.id)
	function clearStreamButton(event){
		var $this_button=$(this);
		var my_stream_id=$this_button.attr('parent_id');

	  jsRoutes.controllers.CtrlStream.clear(my_stream_id).ajax({
	  	dataType : "text",
	    success: function(msg) {
	    	//clear plot
				console.debug("ClearStream! Trying to reinit plot: " + my_stream_id);
	    	var plot = window['streamplot'+my_stream_id];
	    	StreamPlots.clear(plot);
	    },
	    error: function(emsg) {
				console.debug("Error: ClearStream! " + my_stream_id + emsg);
	    	//$('div.container-errormsg').html('<strong>Error unfollow!</strong>'+emsg);
	    }
	  });
		$('.clearStreamButton').on("click", clearStreamButton);
	  return false;
	};
	$('.clearStreamButton').on("click", clearStreamButton);
	
//controllers.CtrlStream.delete(stream.id)
	function deleteStreamButton(event){
		var $this_button=$(this);
		var my_stream_id=$this_button.attr('parent_id');

	  jsRoutes.controllers.CtrlStream.delete(my_stream_id).ajax({
	  	dataType : "text",
	    success: function(msg) {
	    	//remove all DOM elements related to the deleted stream
	    	//$("[id^='stream'][id$='"+my_stream_id+"']").remove();
	    	$("#streamblock"+my_stream_id).remove();
	    },
	    error: function(emsg) {
				console.debug("Error: DeleteStream! " + my_stream_id + emsg);
	    	//$('div.container-errormsg').html('<strong>Error unfollow!</strong>'+emsg);
	    }
	  });
		$('.deleteStreamButton').on("click", deleteStreamButton);
	  return false;
	};
	$('.deleteStreamButton').on("click", deleteStreamButton);
	
  // -- renumber fields

  // Rename fields to have a coherent payload like:
  //
  // streamParserWrapers[0].label
  // streamParserWrapers[0].inputParser
  // ...
  //
  // This is probably not the easiest way to do it. A jQuery plugin would help.
	function renumberParsers() {
		console.debug("renumberParsers");
		$('.parser').each(function(i) {
			console.debug("renumbering parser " + i + $(this).attr('class'));
			$(this).find('input').each(function(j) {
				console.debug("renumbering field " + $(this).attr('name'));
				$(this).attr('name', $(this).attr('name').replace(/streamParserWrapers\[.+?\]/g, 'streamParserWrapers[' + i + ']'));
				console.debug("to " + $(this).attr('name'));
			});
			$(this).find('select').each(function(j) {
				console.debug("renumbering field " + $(this).attr('name'));
				$(this).attr('name', $(this).attr('name').replace(/streamParserWrapers\[.+?\]/g, 'streamParserWrapers[' + i + ']'));
				console.debug("to " + $(this).attr('name'));
			});
    });
 	};

 	var streamParsersToDelete = new Array();
 	Array.prototype.destroy = function(obj){
    while (this.indexOf(obj) != -1) {
    	this.splice(this.indexOf(obj), 1);
    };
 	}
 	//remove from form and renumber the form
 	function removeParser(e) {
 		var requestDelete = $(this).attr('data-delete');
 		var dataparserId = $(this).attr('data-parserId');
 		console.debug("data-parserId " + dataparserId);
 		if(dataparserId > 0 && requestDelete=='true') {
 			streamParsersToDelete.destroy(dataparserId);
 			//$(this).toggleClass('icon-trash icon-trash-white');
 			$(this).attr('data-delete','false');
 			$(this).parents('.parser').toggleClass('overlay');
  		console.debug("Parser " + dataparserId + " request delete true --> false");
 		} else if (dataparserId > 0 && requestDelete=='false'){
 			streamParsersToDelete.push(dataparserId);
 			//$(this).toggleClass('icon-trash icon-trash-white');
 			$(this).attr('data-delete','true');
 			$(this).parents('.parser').toggleClass('overlay');
  		console.debug("Parser " + dataparserId + " request delete false --> true");
 		} else if(dataparserId <= 0 || typeof dataparserId === 'undefined'){
 		//delete form field only
			var streamParserWrapers = $(this).parents('.parsers');
			$(this).parent().remove();
			renumberParsers();
		  $('.removeParser').on("click", removeParser);
 		}
 		for (var i=0; i<streamParsersToDelete.length; i++) { 
  		console.debug(streamParsersToDelete[i]);
  	}
		//deleteParser(dataparserId);
  };
  
  //send the delete request to server
  function deleteParser(dataparserId) {
		console.debug("Parser " + dataparserId + " sending delete request.");
		var msgToShow = "";
  	jsRoutes.controllers.CtrlResource.deleteParser(dataparserId).ajax({
	    success: function(msg) {
	    	msgToShow = "Parser " + dataparserId + " deleted: " + msg;
	  		console.debug(msgToShow);
				showAlert("alert-success", msgToShow);
	    },
	    error: function(msg) {
	    	msgToShow = "Parser " + dataparserId + " delete error: " + msg;
	    	console.debug(msgToShow);
				showAlert("alert-error", msgToShow);
	    }
	  });	
  };
  $('.removeParser').on("click", removeParser);

  //send the create request to server
//	public static Result addParser(Long resourceId, String inputParser, String inputType, String streamPath) {

  function createParser(resourceId, inputParser, inputType, streamPath) {
  	var smsg;
  	jsRoutes.controllers.CtrlResource.addParser(resourceId, inputParser, inputType, streamPath).ajax({
	    success: function(msg) {
	    	smsg="Parser " + dataparserId + " add: " + msg;
	  		console.debug(smsg);
				showAlert("alert-success", smsg);
	    },
	    error: function(msg) {
	    	smsg="Parser " + dataparserId + " add error: " + msg;
	  		console.debug(smsg);
				showAlert("alert-error", smsg);
	    }
	  });	
  };
  //insert streamParser form field
  function insertParser(e) {
  	$('.streamParsersLabels').removeClass("hidden");
  	$('.streamParsersLabelsAddParserText').addClass("hidden");
		var template = $('.parsers_template');
		template.before('<div new="true">' + template.html() + '</div>');
		renumberParsers();
		//bind button functionality
	  $('.removeParser').on("click", removeParser);
  };
  $('.addParser').on("click", insertParser);
  
  function updateResource(e) {
//  	var $resource_form = $('#resource_form');
//  	if (!$resource_form[0].checkValidity()) {
//  	  // If the form is invalid, submit it. The form won't actually submit;
//  	  // this will just cause the browser to display the native HTML5 error messages.
//  		console.debug("form invalid!");
//  	  //$resource_form.submit();
//  	} else {
	  	$('.parsers_template').remove();
	  	var msg;
	  	for (var i=0; i<streamParsersToDelete.length; i++) {
	  		var msg = "Remove DOM: " + $('[data-parserId="'+streamParsersToDelete[i]+'"]').attr('id');
	  		deleteParser(streamParsersToDelete[i]);
	  		console.debug(msg);
	  		$('[data-parserId="'+streamParsersToDelete[i]+'"]').remove();
				console.debug(msg);
				showAlert("alert-success", msg);
	  	}
	  	streamParsersToDelete = new Array();
			renumberParsers();
//  	}		
  };
  $('#updateResource').on("click", updateResource);

  $('#addResource').on("click", function(e) {  	
	  	$('.parsers_template').remove();
			renumberParsers();
			var msg = "Adding new resource.";
			console.debug(msg);
			showAlert("alert-success", msg);			
  });
  
  function fileListFolderClick(e) {  	
  	var path = $(this).attr('data-folderpath');
  	console.debug("Browsing: " + path);
  	jsRoutes.controllers.CtrlFile.lsDir(path, false).ajax({
      success: function(msg) {
      	$('#fileTableBody').html(msg);
      },
      error: function(msg) {
      	console.debug("Failed to browse folder: " + path + " Response: "+ msg);
      }
    });
  };
  $('.folder-list-button').on("click", fileListFolderClick);
  
//  function add_resource_form_handler(e) {	
//  	$('.parsers_template').remove();
//		renumberParsers();
//		console.debug("Adding new resource.");
//  }
//  var add_resource_form = document.getElementById("add_resource_form");
//  add_resource_form.addEventListener("submit", add_resource_form_handler, false);
  
//-----------jsTree

$("#vfileTree").jstree();

$("#vfileTree").load($(this).jstree());

//$("#vfileTree").bind("open_node.jstree select_node.jstree", function(event, data) {
//	var path = '/' + $(this).jstree('get_path', data.rslt.obj, false).join('/');
//	alert(path);
//	console.debug("Browsing: " + path);
//	jsRoutes.controllers.CtrlFile.lsDir(path, false).ajax({
//    success: function(msg) {
//    	$('#fileTableBody').html(msg);
//    },
//    error: function(msg) {
//    	console.debug("Failed to browse folder: " + path + " Response: "+ msg);
//    }
//  });
//    //alert($(data.args[0]).text());
//		//window.location.hash=path;
//});  






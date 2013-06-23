var clearStreamButton, streamButtonsHandler, togglePublicAccessStreamButton, toggleFollowStreamButton, togglePublicSearchStreamButton, showAlert, hideStreamList;
var browseFolder, browseFile, initializeMaps, hideFolderFunction, selectPathInTree;
var showHidePlots;
var streamRegenerateKey;
var resourceRegenerateKey;
var browseResourceList, loadResourceList;
var fileRowClick, fileRowRightClick, fileMenuButtonHandlers, fileListFolderClick;
var renameConfirmBox, findAndReplaceInSideTree, utilShowPath;
var deleteStreamButton, renumberParsers, streamParsersToDelete, removeParser, deleteParser, deleteFileButton, removeSelection, updateResource, insertParser, createParser;
var resourceStreamPath, addResourceFunction, resourceButtonsHandler;

	showHidePlots = function (e) {
			var $showHidePlotsButton= $('#showHidePlotsButton');

		$('.streamconfig').toggle();
		$('.streamplot_overview').toggle();
		$('.streamplot').toggle();
		$('.streamNoDataLine').toggle();

		if($showHidePlotsButton.hasClass("ShowGraphs")) {
			StreamPlots.restartActivePlots();
		} else {
			StreamPlots.stopActivePlots(true);
		}
		$showHidePlotsButton.toggleClass("ShowGraphs");

		var currentTitle = $showHidePlotsButton.text();
		console.debug("currentTitle: " + currentTitle);

		var nextTitle = $showHidePlotsButton.attr('data-nextTitle');
				console.debug("nextTitle: " + nextTitle);

		$showHidePlotsButton.text(nextTitle);
		$showHidePlotsButton.attr('data-nextTitle', currentTitle);
		// $showHidePlotsButton.on("click", showHidePlots);
	}
	$('#showHidePlotsButton').on("click", showHidePlots);

	showAlert = function (type, msg) {
	$('#container-errormsg').html('<div class="alert ' + type + '"><a class="close" data-dismiss="alert">×</a>' + msg + '</div>').show().fadeOut(20000);
	// $('.container').after('<div class="container-errormsg><div class="' + type
	// + '"><a class="close" data-dismiss="alert">×</a>' + msg + '</div></div>');
}

toggleFollowStreamButton = function (event){
		var $this_button=$(this);
		var my_stream_id=$this_button.attr('data-parent_id');
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
	    	// $('div.container-errormsg').html('<strong>Reparing
				// Error!</strong>'+fmsg);
				console.debug("FollowStream result: " + my_stream_id + fmsg);
	    	if(fmsg=="true") {
					console.debug("FollowStream result: " + my_stream_id + fmsg);

	    		$this_button.removeClass("follow_stream");
	    		$this_button.addClass("btn-success disabled unfollow_stream");
	    	} else if(fmsg=="false") {
					console.debug("FollowStream result: " + my_stream_id + fmsg);

	    		$this_button.removeClass("btn-success disabled unfollow_stream");
	    		$this_button.addClass("follow_stream");
	    	}
	    },
	    error: function(emsg) {
	    	// $('div.container-errormsg').html('<strong>Error
				// unfollow!</strong>'+emsg);
				console.debug("Error: Follow stream! " + my_stream_id + emsg);
	    	jsRoutes.controllers.CtrlUser.isFollowingStream(my_stream_id).ajax({
	    		dataType : "text",
	  	    success: function(fmsg) {
	  	    	// $('div.container-errormsg').html('<strong>Reparing
						// Error!</strong>'+fmsg);
	  				console.debug("Error: Reparing Error of followStream! " + my_stream_id + fmsg);
	  	    	if(fmsg=="true") {
	  	    		$this_button.removeClass("follow_stream");
	  	    		$this_button.addClass("btn-success disabled unfollow_stream");
	  	    	} else if(fmsg=="false") {
	  	    		$this_button.removeClass("btn-success disabled unfollow_stream");
	  	    		$this_button.addClass("follow_stream");
	  	    	}
	  	    },
	  	    error: function(fmsg) {
	  	    	// $('div.container-errormsg').html('<strong>Error!</strong>'+fmsg);
	  	    	console.debug("Error: isFollowingStream! " + my_stream_id + fmsg);
	  	    }
	  	  });
	    }
	  });
		$('.unfollow_stream').unbind('click').on("click", toggleFollowStreamButton);
		$('.follow_stream').unbind('click').on("click", toggleFollowStreamButton);
	  // return false;
	};
	$('.unfollow_stream').unbind('click').on("click", toggleFollowStreamButton);
	$('.follow_stream').unbind('click').on("click", toggleFollowStreamButton);

	hideStreamList = function (event){
		$(this).toggleClass("icon-chevron-down icon-chevron-up");
		// $(this).children().find('.icon-chevron-up').toggleClass("icon-chevron-down").toggleClass("icon-chevron-up");
		$(this).parent().find('.stream_list').toggle();
		return false;
	};
	$('.hide_streams').unbind('click').on("click", hideStreamList);

	// controllers.CtrlStream.setPublicAccess(id: Long, pub: Boolean)
	togglePublicAccessStreamButton = function (event){
		var $this_button=$(this);
		var my_stream_id=$this_button.attr('data-parent_id');
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
	    		$this_button.removeClass("set_public_access_stream");
	    		$this_button.addClass("btn-success disabled remove_public_access_stream");
	    	} else if(fmsg=="false") {
	    		$this_button.removeClass("btn-success disabled remove_public_access_stream");
	    		$this_button.addClass("set_public_access_stream");
	    	}
// $this_button.attr('title', next_tooltip_title);
// $this_button.attr('inactive_title', current_tooltip_title);
	    	// event.stopImmediatePropagation();
	    	// event.stopPropagation();
	    },
	    error: function(emsg) {
				console.debug("Error: PublicAccess stream! " + my_stream_id + emsg);
	    	// $('div.container-errormsg').html('<strong>Error
				// unfollow!</strong>'+emsg);
	    	jsRoutes.controllers.CtrlStream.isPublicAccess(my_endpoint_id).ajax({
	    		dataType : "text",
	  	    success: function(fmsg) {
	  	    	// $('div.container-errormsg').html('<strong>Reparing
						// Error!</strong>'+fmsg);
	  				console.debug("PublicAccess: Reparing Error! " + my_stream_id + fmsg);
	  	    	if(fmsg=="true") {
	  	    		$this_button.removeClass("set_public_access_stream");
	  	    		$this_button.addClass("btn-success disabled remove_public_access_stream");
	  	    	} else if(fmsg=="false") {
	  	    		$this_button.removeClass("btn-success disabled remove_public_access_stream");
	  	    		$this_button.addClass("set_public_access_stream");
	  	    	}
	  	    },
	  	    error: function(fmsg) {
	  				console.debug("PublicAccess: Still: Error! " + my_stream_id + fmsg);
	  	    	// $('div.container-errormsg').html('<strong>Error!</strong>'+fmsg);
	  	    }
	  	  });
	    }
	  });
		$('.set_public_access_stream').on("click", togglePublicAccessStreamButton);
		$('.remove_public_access_stream').on("click", togglePublicAccessStreamButton);
	  return false;
	};
	$('.set_public_access_stream').unbind('click').on("click", togglePublicAccessStreamButton);
	$('.remove_public_access_stream').unbind('click').on("click", togglePublicAccessStreamButton);

	// controllers.CtrlStream.setPublicSearch(id: Long, pub: Boolean)
	togglePublicSearchStreamButton = function (event){
		var $this_button=$(this);
		var my_stream_id=$this_button.attr('data-parent_id');
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
	    		$this_button.removeClass("set_public_search_stream");
	    		$this_button.addClass("btn-success disabled remove_public_search_stream");
	    	} else if(fmsg=="false") {
	    		$this_button.removeClass("btn-success disabled remove_public_search_stream");
	    		$this_button.addClass("set_public_search_stream");
	    	}
	    	// event.stopImmediatePropagation();
	    	// event.stopPropagation();
	    },
	    error: function(emsg) {
				console.debug("Error: PublicSearch stream! " + my_stream_id + emsg);
	    	// $('div.container-errormsg').html('<strong>Error
				// unfollow!</strong>'+emsg);
	    	jsRoutes.controllers.CtrlStream.isPublicSearch(my_endpoint_id).ajax({
	    		dataType : "text",
	  	    success: function(fmsg) {
	  	    	// $('div.container-errormsg').html('<strong>Reparing
						// Error!</strong>'+fmsg);
	  				console.debug("PublicSearch: Reparing Error! " + my_stream_id + fmsg);
	  	    	if(fmsg=="true") {
	  	    		$this_button.removeClass("set_public_search_stream");
	  	    		$this_button.addClass("btn-success disabled remove_public_search_stream");
	  	    	} else if(fmsg=="false") {
	  	    		$this_button.removeClass("btn-success disabled remove_public_search_stream");
	  	    		$this_button.addClass("set_public_search_stream");
	  	    	}
	  	    },
	  	    error: function(fmsg) {
	  				console.debug("PublicSearch: Still: Error! " + my_stream_id + fmsg);
	  	    	// $('div.container-errormsg').html('<strong>Error!</strong>'+fmsg);
	  	    }
	  	  });
	    }
	  });
		$('.set_public_search_stream').unbind('click').on("click", togglePublicSearchStreamButton);
		$('.remove_public_search_stream').unbind('click').on("click", togglePublicSearchStreamButton);
	  return false;
	};
	$('.set_public_search_stream').unbind('click').on("click", togglePublicSearchStreamButton);
	$('.remove_public_search_stream').unbind('click').on("click", togglePublicSearchStreamButton);

// controllers.CtrlStream.clear(stream.id)
	clearStreamButton = function (event){
		var $this_button=$(this);
		var my_stream_id=$this_button.attr('data-parent_id');

	  jsRoutes.controllers.CtrlStream.clear(my_stream_id).ajax({
	  	dataType : "text",
	    success: function(msg) {
	    	// clear plot
				console.debug("ClearStream! Trying to reinit plot: " + my_stream_id);
	    	var plot = window['streamplot'+my_stream_id];
	    	StreamPlots.clear(plot);
	    },
	    error: function(emsg) {
	    	var errorMsg = "Error clearing stream: " + my_stream_id + emsg;
	    	console.debug( errorMsg );
	    	showAlert('alert-error',errorMsg);
	    	// $('div.container-errormsg').html('<strong>Error
				// unfollow!</strong>'+emsg);
	    }
	  });
		$('.clearStreamButton').unbind('click').on("click", clearStreamButton);
	  // return false;
	};
	$('.clearStreamButton').unbind('click').on("click", clearStreamButton);

// controllers.CtrlStream.delete(stream.id)
  deleteStreamButton = function (event){
		var $this_button=$(this);
		var my_stream_id=$this_button.attr('data-parent_id');

	  jsRoutes.controllers.CtrlStream.delete(my_stream_id).ajax({
	  	dataType : "text",
	    success: function(msg) {
	    	// remove all DOM elements related to the deleted stream
	    	// $("[id^='stream'][id$='"+my_stream_id+"']").remove();
	    	$("#streamblock"+my_stream_id).remove();
				location.reload(true);
	    },
	    error: function(emsg) {
	    	var errorMsg = "Error deleting stream: " + my_stream_id + emsg;
	    	console.debug( errorMsg );
	    	showAlert('alert-error',errorMsg);
	    	// $('div.container-errormsg').html('<strong>Error
				// unfollow!</strong>'+emsg);
	    }
	  });
		$('.deleteStreamButton').unbind('click').on("click", deleteStreamButton);
	  // return false;
	};
	$('.deleteStreamButton').unbind('click').on("click", deleteStreamButton);

	streamButtonsHandler = function (e) {
		$('.deleteStreamButton').unbind('click').on("click", deleteStreamButton);
		$('.clearStreamButton').unbind('click').on("click", clearStreamButton);
		$('.set_public_search_stream').unbind('click').on("click", togglePublicSearchStreamButton);
		$('.remove_public_search_stream').unbind('click').on("click", togglePublicSearchStreamButton);
		$('.set_public_access_stream').unbind('click').on("click", togglePublicAccessStreamButton);
		$('.remove_public_access_stream').unbind('click').on("click", togglePublicAccessStreamButton);
		$('.unfollow_stream').unbind('click').on("click", toggleFollowStreamButton);
		$('.follow_stream').unbind('click').on("click", toggleFollowStreamButton);
	};
  // -- renumber fields

  // Rename fields to have a coherent payload like:
  //
  // streamParserWrappers[0].label
  // streamParserWrappers[0].inputParser
  // ...
  //
  // This is probably not the easiest way to do it. A jQuery plugin would help.

  renumberParsers = function () {
		console.debug("renumberParsers");
		$('.parser').each(function(i) {
			console.debug("renumbering parser " + i + $(this).attr('class'));
			$(this).find('input').each(function(j) {
				console.debug("renumbering field " + $(this).attr('name'));
				$(this).attr('name', $(this).attr('name').replace(/streamParserWrappers\[.+?\]/g, 'streamParserWrappers[' + i + ']'));
				console.debug("to " + $(this).attr('name'));
			});
			$(this).find('select').each(function(j) {
				console.debug("renumbering field " + $(this).attr('name'));
				$(this).attr('name', $(this).attr('name').replace(/streamParserWrappers\[.+?\]/g, 'streamParserWrappers[' + i + ']'));
				console.debug("to " + $(this).attr('name'));
			});
    });
 	};

 	streamParsersToDelete = new Array();
 	Array.prototype.destroy = function(obj){
    while (this.indexOf(obj) != -1) {
    	this.splice(this.indexOf(obj), 1);
    };
 	}

 	// remove from form and renumber the form
  removeParser = function (e) {
 		var requestDelete = $(this).attr('data-delete');
 		var dataparserId = $(this).attr('data-parserId');
 		console.debug("data-parserId " + dataparserId);
 		if(dataparserId > 0 && requestDelete=='true') {
 			streamParsersToDelete.destroy(dataparserId);
 			// $(this).toggleClass('icon-trash icon-trash-white');
 			$(this).attr('data-delete','false');
 			$(this).parents('.parser').toggleClass('overlay');
  		console.debug("Parser " + dataparserId + " request delete true --> false");
 		} else if (dataparserId > 0 && requestDelete=='false'){
 			streamParsersToDelete.push(dataparserId);
 			// $(this).toggleClass('icon-trash icon-trash-white');
 			$(this).attr('data-delete','true');
 			$(this).parents('.parser').toggleClass('overlay');
  		console.debug("Parser " + dataparserId + " request delete false --> true");
 		} else if(dataparserId <= 0 || typeof dataparserId === 'undefined'){
 		// delete form field only
			var streamParserWrappers = $(this).parents('.parsers');
			$(this).parent().remove();
			renumberParsers();
		  $('.removeParser').unbind('click').on("click", removeParser);
 		}
 		for (var i=0; i<streamParsersToDelete.length; i++) {
  		console.debug(streamParsersToDelete[i]);
  	}
		// deleteParser(dataparserId);
  };

  // send the delete request to server
  deleteParser = function (dataparserId) {
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
  $('.removeParser').unbind('click').on("click", removeParser);

  // send the create request to server
// public static Result addParser(Long resourceId, String inputParser, String
// inputType, String streamPath) {
  createParser = function (resourceId, inputParser, inputType, streamPath) {
  	var smsg;
  	jsRoutes.controllers.CtrlResource.addParser(resourceId, inputParser, inputType, streamPath).ajax({
	    success: function(msg) {
	  		console.debug("success");
	    	smsg="Parser " + dataparserId + " added ";
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
  // insert streamParser form field

  insertParser = function (e) {
  	$('.streamParsersLabels').removeClass("hidden");
  	$('.streamParsersLabelsAddParserText').addClass("hidden");
		var template = $('.parsers_template');
		template.before('<div new="true">' + template.html() + '</div>');
		renumberParsers();
		// bind button functionality
	  $('.removeParser').unbind('click').on("click", removeParser);
  };

  $('.addParser').unbind('click').on("click", insertParser);

  resourceStreamPath = function (e) {
  	 if(!($(this).hasClass('resourceStreamPath'))) {
       this.select();
       return false;
     }

  	if( $(this).attr('readonly') ) {
  		var path = $(this).val();
    	var myID = $(this).attr('id');
    	var debugMsg = myID + " Browsing: " + path;
    	console.debug(debugMsg);

  		jsRoutes.controllers.CtrlFile.miniBrowse(path).ajax({
  	    success: function(msg) {
        	StreamPlots.stopActivePlots();
        	var mainPane = $('#mainPane').html(msg);
        	initializeMaps();
        	fileMenuButtonHandlers();
        	selectPathInTree(null, path);
        	// window.history.pushState(“string”, “Title”, “newUrl”);
        	// msg = msg.replace(/(\r\n|\n|\r)/gm,'').replace(/^$/,
					// '');//.replace(/(<!DOCTYPE html>*<body>)/,
					// '').replace(/(<.body>*<.html>)/, '');
  	    },
  	    error: function(msg) {
  	    	var errorMsg = "Failed to browse: " + path;
  	    	console.debug( errorMsg );
  	    	showAlert('alert-error',errorMsg);
  	    }
  	  });
  	   // alert($(data.args[0]).text());
  		// window.location.hash=path;
    	// event.preventDefault();
    	//event.stopPropagation();
  		return false;
  	}
  };

  $('input[readonly]').unbind('click').on('click', resourceStreamPath);

   updateResource = function (e) {
// var $resource_form = $('#resource_form');
// if (!$resource_form[0].checkValidity()) {
// // If the form is invalid, submit it. The form won't actually submit;
// // this will just cause the browser to display the native HTML5 error
// messages.
// console.debug("form invalid!");
// //$resource_form.submit();
// } else {
	  	$('.parsers_template').remove();
	  	var msg;
	  	for (var i=0; i<streamParsersToDelete.length; i++) {
	  		var msg = "Remove DOM: " + $('[data-parserId="'+streamParsersToDelete[i]+'"]').attr('id');
	  		deleteParser(streamParsersToDelete[i]);
	  		console.debug(msg);
	  		$('[data-parserId="'+streamParsersToDelete[i]+'"]').remove();
				console.debug(msg);
// showAlert("alert-success", msg);
	  	}
	  	streamParsersToDelete = new Array();
			renumberParsers();
			showAlert("alert-success", "Resource updated.");
// }
  };
  $('#updateResource').unbind('click').on("click", updateResource);
// $('.addResourceQuick').on("click", function(e) {
// var msg = "Added a new resource successfully.";
// console.debug(msg);
// showAlert("alert-success", msg);
// });
  addResourceFunction = function(e) {
  	$('.parsers_template').remove();
		renumberParsers();
		var msg = "Adding new resource.";
		console.debug(msg);
		showAlert("alert-success", msg);
};
  $('#addResource').unbind('click').on("click", addResourceFunction);

	resourceButtonsHandler = function (e) {
	  $('.removeParser').unbind('click').on("click", removeParser);
	  $('input[readonly]').unbind('click').on('click', resourceStreamPath);
	  $('.addParser').unbind('click').on("click", insertParser);
	  $('#updateResource').unbind('click').on("click", updateResource);
	  $('#addResource').unbind('click').on("click", addResourceFunction);
	  $('.resourceListItem').unbind('click').on('click', browseResourceList);
	  $('.resourceRegenerateKey').unbind('click').on('click', resourceRegenerateKey);

	};

  deleteFileButton = function(event){
		var $this_button=$(this);
		var my_file_path=$this_button.attr('data-filepath');
		var my_file_name=$this_button.attr('data-filename');

		console.debug("Trying: DeleteFile! " + my_file_path );

	  jsRoutes.controllers.CtrlFile.delete(my_file_path).ajax({
	  	dataType : "text",
	    success: function(msg) {
	    	// remove all DOM elements related to the deleted stream
				//
	    	// $("[id^='stream'][id$='"+my_stream_id+"']").remove();
				console.debug("Success: DeleteFile! " + my_file_path + msg);
	    	$("#fileRow"+my_file_name).remove();
				location.reload(true);
				// should refresh Drive list!
	    },
	    error: function(emsg) {
	    	var errorMsg = "Error deleting file " + my_file_path;
	    	console.debug( errorMsg );
	    	showAlert('alert-error',errorMsg);
	    	// $('div.container-errormsg').html('<strong>Error
				// unfollow!</strong>'+emsg);
	    }
	  });
		$('.deleteFileButton').on("click", deleteFileButton);
	  // return false;
	};
	$('.deleteFileButton').on("click", deleteFileButton);


	removeSelection = function(e) {
		var sel;
		if(document.selection && document.selection.empty) {
			document.selection.empty();
			}
		else {
			if(window.getSelection) {
				sel = window.getSelection();
				try {
					sel.removeAllRanges();
					// sel.collapse();
				} catch (err) { }
			}
		}
	}

	browseFolder = function(event, data) {
		if( $(this).hasClass('fileNode') )
			return false;
		var path = $(this).attr('data-filepath');
  	var myID = $(this).attr('id');
  	var debugMsg = myID + " Browsing: " + path;
  	console.debug(debugMsg);

  	jsRoutes.controllers.CtrlFile.miniBrowse(path).ajax({
	    success: function(msg) {
      	StreamPlots.stopActivePlots();
      	$('#mainPane').html(msg);
	    	fileMenuButtonHandlers();
      	selectPathInTree(null, path);
	    	// window.history.pushState(“string”, “Title”, “newUrl”);
	    },
	    error: function(msg) {
	    	var errorMsg = "Failed to browse folder: " + path;
	    	console.debug( errorMsg );
	    	showAlert('alert-error',errorMsg);
	    }
	  });
  	// event.preventDefault();
  	event.stopPropagation();
  	return false;
	};

	browseFile = function(event, data) {
		// event.stopPropagation();
		// removeSelection();
		var path = $(this).attr('data-filepath');
  	var myID = $(this).attr('id');
		// should test if myID is undefined
  	var debugMsg = myID + " Browsing: " + path;
  	console.debug(debugMsg);

		jsRoutes.controllers.CtrlFile.miniBrowse(path).ajax({
	    success: function(msg) {
      	StreamPlots.stopActivePlots();
      	var mainPane = $('#mainPane').html(msg);
      	initializeMaps();
      	fileMenuButtonHandlers();
      	selectPathInTree(null, path);
      	// window.history.pushState(“string”, “Title”, “newUrl”);
      	// msg = msg.replace(/(\r\n|\n|\r)/gm,'').replace(/^$/,
				// '');//.replace(/(<!DOCTYPE html>*<body>)/,
				// '').replace(/(<.body>*<.html>)/, '');
	    },
	    error: function(msg) {
	    	var errorMsg = "Failed to browse: " + path;
	    	console.debug( errorMsg );
	    	showAlert('alert-error',errorMsg);
	    }
	  });
	   // alert($(data.args[0]).text());
		// window.location.hash=path;
  	// event.preventDefault();
  	event.stopPropagation();
		return false;
	};

	hideFolderFunction = function(event) {
		// removeSelection();
		if( $(this).hasClass('fileNode') || $(this).hasClass('jstree-leaf')) {
	  	event.preventDefault(); event.stopPropagation();
			return false;
		}
		$(this).parent().children().find('i .hideFolder').toggleClass('icon-folder-close icon-folder-open');
		$(this).parent().children().find('li').toggle();
		$(this).toggleClass('icon-folder-close icon-folder-open');
		$(this).removeClass('selectedFile');

  	// event.preventDefault();
  	// event.stopPropagation();
		return false;
		// $(this).toggle();
	};

	hideAllFolderFunction = function(event) {
		removeSelection();
		if(! $(this).hasClass('showRoot')) {
			$(this).addClass('showRoot')
			$('#vfileTree').find('.hideFolder').each(function(event) {
				$(this).removeClass('icon-folder-open');
				$(this).siblings('span').removeClass('selectedFile');
				if( !($(this).hasClass('icon-folder-close')) ) {
					$(this).addClass('icon-folder-close');
				}
				$(this).parent().children().find("li").hide();
				$(this).siblings('selectedFile').removeClass('selectedFile');
			});
		} else {
			$(this).removeClass('showRoot');
			$('#vfileTree').find('.hideFolder').each(function(event) {
				$(this).removeClass('icon-folder-close');
				$(this).siblings('span').removeClass('selectedFile');
				if( !($(this).hasClass('icon-folder-open')) ) {
					$(this).addClass('icon-folder-open');
				}
				$(this).parent().children().find("li").show();
				$(this).siblings('selectedFile').removeClass('selectedFile');
			});
		}
		return false;
		// $(this).toggle();
	};

	utilShowPath = function(event, path, root) {
		$('#vfileTree').find("[data-filepath='"+path+"']").each( function(e) {
			$(this).parent().children().find('li').show();
			$(this).parent().find('.icon-folder-close').toggleClass('icon-folder-open icon-folder-close');
			if(root) {
				$('.fileNode').removeClass('selectedFile');
				$(this).addClass('selectedFile');
			}
			// alert(path);
			var parentFolders=path.split("/");
			if(parentFolders.length > 2) {
					var subpath="";
					for(var i=1; i<parentFolders.length-1; i++){
						subpath+="/"+parentFolders[i];
					}
					utilShowPath(event, subpath, false);
			}
		});
	}

	selectPathInTree = function(event, path) {
		$('.hideRoot').siblings('span').removeClass('selectedFile');
		$('#vfileTree').find('.hideFolder').each(function(event) {
			$(this).removeClass('icon-folder-open');
			$(this).siblings('span').removeClass('selectedFile');
			if( !($(this).hasClass('icon-folder-close')) ) {
				$(this).addClass('icon-folder-close');
			}
			$(this).parent().children().find("li").hide();
			$(this).siblings('selectedFile').removeClass('selectedFile');

		});
		utilShowPath(event, path, true);
		};

  fileMenuButtonHandlers = function(e) {
  	// unbind first then bind again so the event does not fire twice and the
		// handlers get reassociated
  	$('.deleteFileButton').unbind('click').on("click", deleteFileButton);
		$('.clearStreamButton').unbind('click').on("click", clearStreamButton);
		$('.unfollow_stream').unbind('click').on("click", toggleFollowStreamButton);
		$('.follow_stream').unbind('click').on("click", toggleFollowStreamButton);
		$('.renameMenuButton').unbind('click').on("click", renameConfirmBox);
	  $('.folder-list-button').unbind('click').on("click", fileListFolderClick);
	  $('.rowlink').unbind('click').on('click', fileRowClick);
	  $('.rowlink').unbind('contextmenu').on('contextmenu', fileRowRightClick);
		$('.dirNode').unbind('click').on('click', browseFolder);
		$('.fileNode').unbind('click').on('click', browseFile);
		$('.file-list-button').unbind('click').on('click', browseFile);
		$('.hideFolder').unbind('click').on('click', hideFolderFunction);
		$('.hideRoot').unbind('click').on('click', hideAllFolderFunction);
  }

  fileRowRightClick = function(event) {
  // console.debug("fileRowRightClick: ");
    $(this).find('.dropdown-toggle').dropdown();
    $(this).parent().find('.dropdown').removeClass('open');
    $(this).find('.dropdown').toggleClass('open');
    fileMenuButtonHandlers();
  	// event.preventDefault();
  	// event.stopPropagation();
    return false;
};

fileRowClick = function(e) {
// console.debug("fileRowClick: ");
  $(this).find('.dropdown').removeClass('open');
};

  fileListFolderClick = function (e) {
  	var path = $(this).attr('data-folderpath');
  	var myID = $(this).attr('id');
  	var debugMsg = myID + " Browsing: " + path;
  	console.debug(debugMsg);
  	// alert(debugMsg);
  	jsRoutes.controllers.CtrlFile.miniBrowse(path).ajax({
      success: function(msg) {
      	// stop active plots
      	StreamPlots.stopActivePlots();
      	// load file list
      	$('#mainPane').html(msg);
      	selectPathInTree(null, path.substring(0,path.length-1));
      	fileMenuButtonHandlers();
      },
      error: function(msg) {
      	console.debug("Failed to browse folder: " + path + " Response: "+ msg);
      }
    });
  	// return false;
  };
  fileMenuButtonHandlers();

  findAndReplaceInSideTree = function(oldPath, newPath, newName) {
		$('#vfileTree').find("[data-filepath='"+oldPath+"']").each( function(e) {
			$(this).attr("data-filepath", newPath);
			var oldName = $(this).html(" "+ newName);
		});
  };

	renameConfirmBox = function (e){
   // confirmMessage = confirmMessage || '';
		var $this_button=$(this);
		var my_file_name=$this_button.attr('data-filename');
		var my_file_parentpath=$this_button.attr('data-fileParentPath');
		var my_file_path=my_file_parentpath+"/"+my_file_name;
   // $('#renameBox').modal({show:true, backdrop:false, keyboard: false,});
   // $('#confirmMessage').html(confirmMessage);
    $('#renameConfirmTrue').click(function(){
      	var newFileName=$('#renameInputBox').val();
    		var new_file_path=my_file_parentpath+"/"+newFileName;
    		console.debug("Trying: RenameFile! " + my_file_path + " to: "+new_file_path);
    		$('#renameBox').modal('hide');
    	  jsRoutes.controllers.CtrlFile.move(my_file_path, new_file_path).ajax({
    	  	dataType : "text",
    	    success: function(msg) {
    	    	// remove all DOM elements related to the deleted stream
    	    	// $("[id^='stream'][id$='"+my_stream_id+"']").remove();
    				console.debug("Success: RenamingFile! " + my_file_path + " to: "+new_file_path);
    		  	// stop active plots
    		  	StreamPlots.stopActivePlots();
          	$('#mainPane').html(msg);
          	findAndReplaceInSideTree(my_file_path, new_file_path, newFileName);
          	fileMenuButtonHandlers();
    	    	var errorMsg = "Renamed file successfully to: " + newFileName;
    	    	console.debug( errorMsg );
    	    	showAlert('alert-success',errorMsg);
    	    },
    	    error: function(emsg) {
    	    	var errorMsg = "Error renaming file: " + my_file_path;
    	    	console.debug( errorMsg );
    	    	showAlert('alert-error',errorMsg);
    	    }
    	  });
    });
    $('#renameConfirmFalse').click(function(){
        $('#renameBox').modal('hide');
    });
};
loadResourceList = function(msg, resId) {
	// load
	//.replace(/<!DOCTYPE html>[\r\n\s]*.*<div id="mainContainer"/igm, '<div id="mainContainer"').replace(/(<\/body>.*<\/html>)/igm, '');
	//alert(msg)
	msg = $.trim(msg);
	msg = $("#mainPane",$('<div></div>').append(msg));
	$('#mainPane').html(msg.html());
	$('.selectedResourceListItem').each(function(e){$(this).removeClass('selectedResourceListItem');});
	$('#resourcelist-sidenav').find('[data-resourceId="'+resId+'"]').addClass('selectedResourceListItem');
  resourceButtonsHandler();
};

browseResourceList = function(e) {
	var resId=$(this).attr('data-resourceId');
	var debugMsg = "Browsing resource: " + resId;
	console.debug(debugMsg);
	// alert(debugMsg);
	jsRoutes.controllers.CtrlResource.getById(resId).ajax({
    success: function(msg) {loadResourceList(msg, resId);},
    error: function(msg) {
    	console.debug("Failed to browse folder: " + resId + " Response: "+ msg);
    }
  });
};
$('.resourceListItem').unbind('click').on('click', browseResourceList);

resourceRegenerateKey = function(e) {
	var resId=$(this).attr('data-resourceId');
	var debugMsg = "Regenerating resource key for id: " + resId;
	console.debug(debugMsg);
	// alert(debugMsg);
	jsRoutes.controllers.CtrlResource.regenerateKey(resId).ajax({
    success: function(msg) {
    	$('#'+resId+'KeyURL').val(msg);
    	showAlert('alert-success',debugMsg+" succeeded. <strong>New key:</strong> " +msg);
    },
    error: function(msg) {
    	console.debug(msg);
    	showAlert('alert-error',msg);
    }
  });
};
$('.resourceRegenerateKey').unbind('click').on('click', resourceRegenerateKey);


streamRegenerateKey = function(e) {
	var resId=$(this).attr('data-streamId');
	var debugMsg = "Regenerating stream key for id: " + resId;
	console.debug(debugMsg);
	// alert(debugMsg);
	jsRoutes.controllers.CtrlStream.regenerateKey(resId).ajax({
    success: function(msg) {
    	$('#streamKey'+resId).val(msg);
    	showAlert('alert-success',debugMsg+" succeeded. <strong>New key:</strong> " +msg);
    },
    error: function(msg) {
    	console.debug(msg);
    	showAlert('alert-error',msg);
    }
  });
};
$('.streamRegenerateKey').unbind('click').on('click', streamRegenerateKey);

// function add_resource_form_handler(e) {
// $('.parsers_template').remove();
// renumberParsers();
// console.debug("Adding new resource.");
// }
// var add_resource_form = document.getElementById("add_resource_form");
// add_resource_form.addEventListener("submit", add_resource_form_handler,
// false);

// -----------jsTree

// $("#vfileTree").jstree();

// $("#vfileTree").load($(this).jstree());


// $("#vfileTree").bind("open_node.jstree select_node.jstree", browseFolder);

// used to ensure clean resource names
function alphanum(e) {
    var k;
    document.all ? k = e.keyCode : k = e.which;
    return ((k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32 || (k >= 48 && k <= 57));
}


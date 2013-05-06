	var showHidePlots;
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
		//$showHidePlotsButton.on("click", showHidePlots);
	}
	$('#showHidePlotsButton').on("click", showHidePlots);
	
function showAlert(type, msg) {
	$('#mainContainer').before('<div class="container-errormsg"><div class="alert ' + type + '"><a class="close" data-dismiss="alert">×</a>' + msg.data + '</div></div>');
	//$('.container').after('<div class="container-errormsg><div class="' + type + '"><a class="close" data-dismiss="alert">×</a>' + msg + '</div></div>');
}	

function toggleFollowStreamButton(event){
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
	    	//$('div.container-errormsg').html('<strong>Reparing Error!</strong>'+fmsg);
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
	    	//$('div.container-errormsg').html('<strong>Error unfollow!</strong>'+emsg);
				console.debug("Error: Follow stream! " + my_stream_id + emsg);
	    	jsRoutes.controllers.CtrlUser.isFollowingStream(my_stream_id).ajax({
	    		dataType : "text",
	  	    success: function(fmsg) {
	  	    	//$('div.container-errormsg').html('<strong>Reparing Error!</strong>'+fmsg);
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
	  	    	//$('div.container-errormsg').html('<strong>Error!</strong>'+fmsg);
	  	    	console.debug("Error: isFollowingStream! " + my_stream_id + fmsg);
	  	    }
	  	  });
	    }
	  });
		$('.unfollow_stream').on("click", toggleFollowStreamButton);
		$('.follow_stream').on("click", toggleFollowStreamButton);
	  //return false;
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
	  	    		$this_button.removeClass("set_public_access_stream");
	  	    		$this_button.addClass("btn-success disabled remove_public_access_stream");
	  	    	} else if(fmsg=="false") {
	  	    		$this_button.removeClass("btn-success disabled remove_public_access_stream");
	  	    		$this_button.addClass("set_public_access_stream");
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
	  	    		$this_button.removeClass("set_public_search_stream");
	  	    		$this_button.addClass("btn-success disabled remove_public_search_stream");
	  	    	} else if(fmsg=="false") {
	  	    		$this_button.removeClass("btn-success disabled remove_public_search_stream");
	  	    		$this_button.addClass("set_public_search_stream");
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
		var my_stream_id=$this_button.attr('data-parent_id');

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
	  //return false;
	};
	$('.clearStreamButton').on("click", clearStreamButton);
	
//controllers.CtrlStream.delete(stream.id)
	function deleteStreamButton(event){
		var $this_button=$(this);
		var my_stream_id=$this_button.attr('data-parent_id');

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
	  //return false;
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
  
  var deleteFileButton;
  deleteFileButton = function(event){
		var $this_button=$(this);
		var my_file_path=$this_button.attr('data-filepath');
		var my_file_name=$this_button.attr('data-filename');

		console.debug("Trying: DeleteFile! " + my_file_path );

	  jsRoutes.controllers.CtrlFile.delete(my_file_path).ajax({
	  	dataType : "text",
	    success: function(msg) {
	    	//remove all DOM elements related to the deleted stream
	    	//$("[id^='stream'][id$='"+my_stream_id+"']").remove();
				console.debug("Success: DeleteFile! " + my_file_path + msg);
	    	$("#fileRow"+my_file_name).remove();
	    },
	    error: function(emsg) {
				console.debug("Error: DeleteFile! " + my_file_path + emsg);
	    	//$('div.container-errormsg').html('<strong>Error unfollow!</strong>'+emsg);
	    }
	  });
		$('.deleteFileButton').on("click", deleteFileButton);
	  //return false;
	};
	$('.deleteFileButton').on("click", deleteFileButton);
	
	var removeSelection; 
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
					//sel.collapse();
				} catch (err) { }
			}
		}
	}
	var browseFolder, browseFile, initializeMaps, hideFolderFunction, selectPathInTree;
	
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
	    	//window.history.pushState(“string”, “Title”, “newUrl”);
	    },
	    error: function(msg) {
	    	console.debug("Failed to browse folder: " + path + " Response: "+ msg);
	    }
	  });
  	//event.preventDefault(); 
  	event.stopPropagation();
  	return false;
	};  
	
	browseFile = function(event, data) {
		//event.stopPropagation();
		//removeSelection();
		var path = $(this).attr('data-filepath');
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
      	//window.history.pushState(“string”, “Title”, “newUrl”);
      	//msg = msg.replace(/(\r\n|\n|\r)/gm,'').replace(/^$/, '');//.replace(/(<!DOCTYPE html>*<body>)/, '').replace(/(<.body>*<.html>)/, '');      	
	    },
	    error: function(msg) {
	    	console.debug("Failed to browse: " + path );
	    }
	  });
	   //alert($(data.args[0]).text());
		//window.location.hash=path;
  	//event.preventDefault(); 
  	event.stopPropagation();
		return false;
	};
	
	hideFolderFunction = function(event) {
		//removeSelection();
		if( $(this).hasClass('fileNode') || $(this).hasClass('jstree-leaf')) {
	  	event.preventDefault(); event.stopPropagation();
			return false;
		}
		$(this).parent().children().find('i .hideFolder').toggleClass('icon-folder-close icon-folder-open');
		$(this).parent().children().find('li').toggle();
		$(this).toggleClass('icon-folder-close icon-folder-open');
		$(this).removeClass('selectedFile');

  	//event.preventDefault(); 
  	//event.stopPropagation();
		return false;
		//$(this).toggle();
	};
	
	var utilShowPath = function(event, path, root) {
		$('#vfileTree').find("[data-filepath='"+path+"']").each( function(e) { 
			$(this).parent().children().find('li').show();
			$(this).parent().find('.icon-folder-close').toggleClass('icon-folder-open icon-folder-close');
			if(root) {
				$('.fileNode').removeClass('selectedFile');
				$(this).addClass('selectedFile');
			}
			//alert(path);
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
	
	var fileRowClick, fileRowRightClick, fileMenuButtonHandlers, fileListFolderClick;
  fileMenuButtonHandlers = function(e) {
  	//unbind first then bind again so the event does not fire twice and the handlers get reassociated
  	$('.deleteFileButton').unbind('click').on("click", deleteFileButton);
		$('.clearStreamButton').unbind('click').on("click", clearStreamButton);
		$('.unfollow_stream').unbind('click').on("click", toggleFollowStreamButton);
		$('.follow_stream').unbind('click').on("click", toggleFollowStreamButton);
		$('.renameMenuButton').unbind('click').on("click", renameConfirmBox);
	  $('.folder-list-button').unbind('click').on("click", fileListFolderClick);
	  $('.rowlink').unbind('click').on('click', fileRowClick);
	  $('.rowlink').unbind('click').on('contextmenu', fileRowRightClick);
		$('.dirNode').unbind('click').on('click', browseFolder);  
		$('.fileNode').unbind('click').on('click', browseFile);
		$('.file-list-button').unbind('click').on('click', browseFile);
		$('.hideFolder').unbind('click').on('click', hideFolderFunction); 

  }
 
  fileRowRightClick = function(event) {
  //	console.debug("fileRowRightClick: ");
    $(this).find('.dropdown-toggle').dropdown();
    $(this).parent().find('.dropdown').removeClass('open');
    $(this).find('.dropdown').toggleClass('open');
    fileMenuButtonHandlers();
  	//event.preventDefault(); 
  	//event.stopPropagation();
    return false;
};

fileRowClick = function(e) {
//	console.debug("fileRowClick: ");
  $(this).find('.dropdown').removeClass('open');
};

  fileListFolderClick = function (e) {  	
  	var path = $(this).attr('data-folderpath');
  	var myID = $(this).attr('id');
  	var debugMsg = myID + " Browsing: " + path;
  	console.debug(debugMsg);
  	//alert(debugMsg);  	
  	jsRoutes.controllers.CtrlFile.miniBrowse(path).ajax({
      success: function(msg) {  
      	//stop active plots
      	StreamPlots.stopActivePlots();
      	//load file list
      	$('#fileTableBody').html(msg);       
      	selectPathInTree(null, path.substring(0,path.length-1));
      	fileMenuButtonHandlers();
      },
      error: function(msg) {
      	console.debug("Failed to browse folder: " + path + " Response: "+ msg);
      }
    });
  	//return false;
  };
  fileMenuButtonHandlers();
  
  var findAndReplaceInSideTree = function(oldPath, newPath, newName) {
		$('#vfileTree').find("[data-filepath='"+oldPath+"']").each( function(e) { 
			$(this).attr("data-filepath", newPath);
			var oldName = $(this).html(" "+ newName);
		});
  };
  
	var renameConfirmBox = function (e){
   // confirmMessage = confirmMessage || '';
		var $this_button=$(this);
		var my_file_name=$this_button.attr('data-filename');
		var my_file_parentpath=$this_button.attr('data-fileParentPath');
		var my_file_path=my_file_parentpath+"/"+my_file_name;
   //$('#renameBox').modal({show:true, backdrop:false, keyboard: false,});
   // $('#confirmMessage').html(confirmMessage);
    $('#renameConfirmTrue').click(function(){   
      	var newFileName=$('#renameInputBox').val();
    		var new_file_path=my_file_parentpath+"/"+newFileName;
    		console.debug("Trying: RenameFile! " + my_file_path + " to: "+new_file_path);
    		$('#renameBox').modal('hide');
    	  jsRoutes.controllers.CtrlFile.move(my_file_path, new_file_path).ajax({
    	  	dataType : "text",
    	    success: function(msg) {
    	    	//remove all DOM elements related to the deleted stream
    	    	//$("[id^='stream'][id$='"+my_stream_id+"']").remove();
    				console.debug("Success: RenamingFile! " + my_file_path + " to: "+new_file_path);
    		  	//stop active plots
    		  	StreamPlots.stopActivePlots();
          	$('#mainPane').html(msg);
          	findAndReplaceInSideTree(my_file_path, new_file_path, newFileName);
          	fileMenuButtonHandlers();
    	    },
    	    error: function(emsg) {
    				console.debug("Error: DeleteFile! " + my_file_path + emsg);
    	    	//$('div.container-errormsg').html('<strong>Error unfollow!</strong>'+emsg);
    	    }
    	  });
    });
    $('#renameConfirmFalse').click(function(){
        $('#renameBox').modal('hide');
    });
};

//  function add_resource_form_handler(e) {	
//  	$('.parsers_template').remove();
//		renumberParsers();
//		console.debug("Adding new resource.");
//  }
//  var add_resource_form = document.getElementById("add_resource_form");
//  add_resource_form.addEventListener("submit", add_resource_form_handler, false);
  
//-----------jsTree

//$("#vfileTree").jstree();

//$("#vfileTree").load($(this).jstree());


//$("#vfileTree").bind("open_node.jstree select_node.jstree", browseFolder);

function alphanum(e) {
    var k;
    document.all ? k = e.keyCode : k = e.which;
    return ((k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32 || (k >= 48 && k <= 57));
}


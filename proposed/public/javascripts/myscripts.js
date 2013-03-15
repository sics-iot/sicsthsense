// 	function followButton(event){
// 		var $this_button=$(this);
// 		var my_endpoint_id=$this_button.attr('parent_id');
// 	  jsRoutes.controllers.CtrlEndPoint.follow(my_endpoint_id).ajax({
// 	  	success: function() {
// 	    	$this_button.toggleClass("icon-star unfollow_endpoint icon-star-empty follow_endpoint");    	
// 	    	$('div.container-errormsg').html('<strong>Cool follow!</strong>');
// 	    	event.stopImmediatePropagation();
// 	    	event.stopPropagation();
// 	    	event.preventDefault();
// 	    	//$this_button.on("click", unfollowButton);
// 	    	return false;
// 	    },
// 	    error: function() {
// 	    	$('div.container-errormsg').html('<strong>Error follow!</strong>');
// 	    }
// 	  });
// 	};
	
// 	function unfollowButton(event){
// 		var $this_button=$(this);
// 		var my_endpoint_id=$this_button.attr('parent_id');
// 	  jsRoutes.controllers.CtrlEndPoint.unfollow(my_endpoint_id).ajax({
// 	    success: function() {
// 	    	$this_button.toggleClass("icon-star-empty follow_endpoint icon-star unfollow_endpoint");
// 	    	$('div.container-errormsg').html('<strong>Cool unfollow!</strong>');
// 	    	event.stopImmediatePropagation();
// 	    	event.stopPropagation();	    	
// 	    	event.preventDefault();
// 	    	//$this_button.on("click", followButton);
// 	    	return false;
// 	    },
// 	    error: function() {
// 	    	$('div.container-errormsg').html('<strong>Error unfollow!</strong>');
// 	    }
// 	  });
// 	};
	
	function toggleFollowEndpointButton(event){
		var $this_button=$(this);
		var my_endpoint_id=$this_button.attr('parent_id');
		var current_tooltip_title=$this_button.attr('title');
		var next_tooltip_title=$this_button.attr('inactive_title');
	  jsRoutes.controllers.CtrlEndPoint.toggleFollow(my_endpoint_id).ajax({
	    success: function(msg) {
	    	$this_button.toggleClass("icon-star-empty follow_endpoint icon-star unfollow_endpoint");
	    	$this_button.attr('title', next_tooltip_title);
	    	$this_button.attr('inactive_title', current_tooltip_title);
	    	//event.stopImmediatePropagation();
	    	//event.stopPropagation();	    	
	    },
	    error: function(emsg) {
	    	$('div.container-errormsg').html('<strong>Error unfollow!</strong>').text(emsg);
	    	jsRoutes.controllers.CtrlEndPoint.isFollowing(my_endpoint_id).ajax({
	  	    success: function(fmsg) {
	  	    	$('div.container-errormsg').html('<strong>Reparing Error!</strong>'+fmsg).fadeIn(1000).fadeOut(1000);
	  	    	if(fmsg=="1") {
	  	    		$this_button.removeClass("icon-star-empty follow_endpoint");
	  	    		$this_button.addClass("icon-star unfollow_endpoint");
	  	    	} else if(fmsg=="0") {
	  	    		$this_button.removeClass("icon-star unfollow_endpoint");
	  	    		$this_button.addClass("icon-star-empty follow_endpoint");
	  	    	} 
	  	    },
	  	    error: function(fmsg) {
	  	    	$('div.container-errormsg').html('<strong>Error!</strong>'+fmsg);
	  	    }
	  	  });
	    }
	  });
	  return false;
	};
	$('.unfollow_endpoint').on("click", toggleFollowEndpointButton);
	$('.follow_endpoint').on("click", toggleFollowEndpointButton);

	function toggleFollowResourceButton(event){
		var $this_button=$(this);
		var my_resource_id=$this_button.attr('parent_id');
		var current_tooltip_title=$this_button.attr('title');
		var next_tooltip_title=$this_button.attr('inactive_title');
	  jsRoutes.controllers.CtrlResource.toggleFollow(my_resource_id).ajax({
	    success: function(msg) {
	    	$this_button.toggleClass("icon-star-empty follow_resource icon-star unfollow_resource");  
	    	$this_button.attr('title', next_tooltip_title);
	    	$this_button.attr('inactive_title', current_tooltip_title);
	    	//event.stopImmediatePropagation();
	    	//event.stopPropagation();	    	
	    },
	    error: function(emsg) {
	    	$('div.container-errormsg').html('<strong>Error unfollow!</strong>'+emsg);
	    	jsRoutes.controllers.CtrlResource.isFollowing(my_endpoint_id).ajax({
	  	    success: function(fmsg) {
	  	    	$('div.container-errormsg').html('<strong>Reparing Error!</strong>'+fmsg);
	  	    	if(fmsg=="1") {
	  	    		$this_button.removeClass("icon-star-empty follow_resource");
	  	    		$this_button.addClass("icon-star unfollow_resource");
	  	    	} else if(fmsg=="0") {
	  	    		$this_button.removeClass("icon-star unfollow_resource");
	  	    		$this_button.addClass("icon-star-empty follow_resource");
	  	    	} 
	  	    },
	  	    error: function(fmsg) {
	  	    	$('div.container-errormsg').html('<strong>Error!</strong>'+fmsg);
	  	    }
	  	  });
	    }
	  });
	  return false;
	};
	$('.unfollow_resource').on("click", toggleFollowResourceButton);
	$('.follow_resource').on("click", toggleFollowResourceButton);

	function hideResourceList(event){
		$(this).toggleClass("icon-chevron-down icon-chevron-up");
		//$(this).children().find('.icon-chevron-up').toggleClass("icon-chevron-down").toggleClass("icon-chevron-up");
		$(this).parent().find('.resource_list').toggle();
		return false;
	};

	$('.hide_resources').on("click", hideResourceList);
	
	//@routes.CtrlResource.setPublicAccess(id)
		function togglePublicAccessResourceButton(event){
		var $this_button=$(this);
		var my_resource_id=$this_button.attr('parent_id');
		var current_tooltip_title=$this_button.attr('title');
		var next_tooltip_title=$this_button.attr('inactive_title');
	  jsRoutes.controllers.CtrlResource.togglePublicAccess(my_resource_id).ajax({
	    success: function(msg) {
	    	$this_button.toggleClass("icon-white set_public_access_resource remove_public_access_resource");
	    	$this_button.attr('title', next_tooltip_title);
	    	$this_button.attr('inactive_title', current_tooltip_title);
	    	//event.stopImmediatePropagation();
	    	//event.stopPropagation();	    	
	    },
	    error: function(emsg) {
	    	$('div.container-errormsg').html('<strong>Error unfollow!</strong>'+emsg);
	    	jsRoutes.controllers.CtrlResource.isPublicAccess(my_endpoint_id).ajax({
	  	    success: function(fmsg) {
	  	    	$('div.container-errormsg').html('<strong>Reparing Error!</strong>'+fmsg);
	  	    	if(fmsg=="1") {
	  	    		$this_button.removeClass("icon-white set_public_access_resource");
	  	    		$this_button.addClass("remove_public_access_resource");
	  	    	} else if(fmsg=="0") {
	  	    		$this_button.removeClass("remove_public_access_resource");
	  	    		$this_button.addClass("icon-white set_public_access_resource");
	  	    	} 
	  	    },
	  	    error: function(fmsg) {
	  	    	$('div.container-errormsg').html('<strong>Error!</strong>'+fmsg);
	  	    }
	  	  });
	    }
	  });
	  return false;
	};
	$('.set_public_access_resource').on("click", togglePublicAccessResourceButton);
	$('.remove_public_access_resource').on("click", togglePublicAccessResourceButton);
	
  // -- renumber fields

  // Rename fields to have a coherent payload like:
  //
  // informations[0].label
  // informations[0].email
  // informations[0].phones[0]
  // informations[0].phones[1]
  // ...
  //
  // This is probably not the easiest way to do it. A jQuery plugin would help.
	function renumberParsers() {
		console.debug("renumberParsers");
		$('.parser').each(function(i) {
			console.debug("renumbering parser " + i + $(this).attr('class'));
			$(this).children().find('input').each(function(j) {
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
 		var requestDelete = $(this).attr('delete');
 		var parserId = $(this).attr('parserId');
 		console.debug("parserID " + parserId);
 		if(requestDelete=='true') {
 			streamParsersToDelete.push(parserId);
 			//$(this).toggleClass('icon-trash icon-trash-white');
 			$(this).attr('delete','false');
 			$(this).parents('.parser').toggleClass('overlay');
  		console.debug("Parser " + parserId + " request delete");
 		} else if (requestDelete=='false'){
 			streamParsersToDelete.destroy(parserId);
 			//$(this).toggleClass('icon-trash icon-trash-white');
 			$(this).attr('delete','true');
 			$(this).parents('.parser').toggleClass('overlay');
  		console.debug("Parser " + parserId + " request delete false");
 		} 
 		//delete field only
 		if(parserId <= 0 || typeof parserId === 'undefined'){
			var streamParserWrapers = $(this).parents('.parsers');
			$(this).parents('.parser').remove();
			renumberParsers();
		  $('.removeParser').on("click", removeParser);
 		}
 		for (var i=0; i<streamParsersToDelete.length; i++) { 
  		console.debug(streamParsersToDelete[i]);
  	}
		//deleteParser(parserId);
  };
  
  //send the delete request to server
  function deleteParser(parserId) {
  	jsRoutes.controllers.CtrlSource.deleteParser(parserId).ajax({
	    success: function(msg) {
	  		console.debug("Parser " + parserId + " deleted: " + msg);
	    },
	    error: function(msg) {
	    	console.debug("Parser " + parserId + " delete error: " + msg);
	    }
	  });	
  };
  $('.removeParser').on("click", removeParser);

  //insert streamParser field
  function insertParser(e) {
		var streamParserWrapers = $(this).parents('.parsers')
		var template = $('.parsers_template');
		template.before('<div class="twipsies well parser">' + template.html() + '</div>');
		renumberParsers();
	  $('.removeParser').on("click", removeParser);
  };
  $('.addParser').on("click", insertParser);
  
  function updateSource(e) {
  	$('.parsers_template').remove();
  	for (var i=0; i<streamParsersToDelete.length; i++) { 
  		deleteParser(streamParsersToDelete[i]);
  		$(this).siblings('[parserId="'+streamParsersToDelete[i]+'"]').remove();
  	}
  	streamParsersToDelete = new Array();
		renumberParsers();
  };
  $('#updateSource').on("click", updateSource);
  //$('#modify_source_form').submit(updateSource);
  
  $('#addSource').on("click", function(e) {
  	$('.parsers_template').remove();
		renumberParsers();
		console.debug("Adding new source.");
  });
//  $('#add_source_form').submit(function() {
//  	$('.parsers_template').remove();
//		renumberParsers();
//  });

//-----------jsTree
$("#vfileTree").bind("select_node.jstree", function(event, data) {
    alert($(data.args[0]).text());
});  
$("#vfileTree").jstree();
$("#vfileTree").load($(this).jstree());



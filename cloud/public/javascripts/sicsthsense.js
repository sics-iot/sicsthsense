$(function(){
    $('a.delete').click(function(){
    	$.ajax(
    			{
    				url: this.getAttribute('href'),
    				type: 'DELETE',
    				async: false,
    				complete: function(response, status) {
    					window.location.reload();
    				}
    			}
    	)
        return false
    })
})

$(function(){
    $('a.post').click(function(){
    	$.ajax(
    			{
    				url: this.getAttribute('href'),
    				type: 'POST',
    				async: false,
    				complete: function(response, status) {
    					window.location.reload();
    				}
    			}
    	)
        return false
    })
})

$(function(){
    $('a.put').click(function(){
    	$.ajax(
    			{
    				url: this.getAttribute('href'),
    				type: 'PUT',
    				async: false,
    				complete: function(response, status) {
    					window.location.reload();
    				}
    			}
    	)
        return false
    })
})

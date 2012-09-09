function getUserInfo() {
		$.get("/user-info", 
		      function(data) {
		      	$("#message").text("Welcome " + data.firstname + " " + data.lastname);});
	}
	
	function login() {
		$("#message").text("sending login request");
		$.post("/login", 
		       {user: $("#user").val(), pass: $("#pass").val()}, 
	    	   function(){	
	    	   		window.location.reload(true);	    	   		    	   	    	    	       	    	       
	    	   },
	    	   "json")
	     .error( function(xhr, textStatus, errorThrown) {       			 
       			 $("#message").text(xhr.responseText + " " + textStatus);
    	 });
	}
	
	function logout() {
		$.post("/logout", 
		       function() {
					window.location.reload(true);
				});					  	
	}
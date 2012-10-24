$(document).ready(function() {
	
	console.log("doc.URL=", document.URL);
	console.log("doc.URL=", document.URL + 'testapp');
	var sock = new SockJS(document.URL + 'testapp');
	
	function ClockModel() {
	  self.ticker = ko.observable();
	
	  sock.onmessage = function (e) {
	  							console.log('message', e.data);
	    						self.ticker(e.data);
	  				   };
	
	};
	
	ko.applyBindings(new ClockModel());

});

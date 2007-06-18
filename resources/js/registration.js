window.addEvent('domready', function() {

	var inputs = new Array();

	$$('input.personalID').each(
	function(element, index){
		inputs.push(element);
		element.addEvent('keyup', function() {
		
			var lastIndex = element.value.length;
			var lastChar = element.value.charAt(lastIndex-1);
			
			if (lastChar == '-') {
				element.value = element.value.substring(0, lastIndex-1);
			}
		
			if (element.value.length == 10) {
				LandsmotEventBusiness.getUserNameDWR(element.value, element.fillSpan);
			} else {
				element.spaner.innerHTML = '';
			}
		});
		
		element.fillSpan = function(data) {
			element.spaner.innerHTML = data;
		}

	});

	$$('span.userResults').each(
	function(element, index){
		inputs[index].spaner = element;
	});
	
});

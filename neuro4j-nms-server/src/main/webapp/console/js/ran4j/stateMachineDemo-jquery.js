;(function() {

   jsPlumbDemo.initEndpoints = function(nextColour) {
        $(".ep").each(function(i,e) {
			var p = $(e).parent();
			jsPlumb.makeSource($(e), {
				parent:p,
				//anchor:"BottomCenter",
				anchor:"Continuous",
				connector:[ "StateMachine", { curviness:20 } ],
				connectorStyle:{ strokeStyle:nextColour(), lineWidth:2 },
				maxConnections:-1
			});
		});	

        jsPlumb.connect({
        	source:"welcome",
        	target:"search",
        	anchors:["TopCenter", "Continuous"]
        });

    };
})();
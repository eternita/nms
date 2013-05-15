var fd;

var entityDetailsNodeId;
function loadEntity(storage, query, depth, edni) {
	 var req = newXMLHttpRequest();
	 entityDetailsNodeId = edni;

	 req.onreadystatechange = getReadyStateHandlerText(req, updateCanvas);
	 
	 req.open("GET", "view-data?storage=" + storage + "&q=" + query + "&d=" + depth, true);
	  req.setRequestHeader("Content-Type", "application/json");
	 req.send();

	 if (null != edni)
	 {
	     loadEntityDetails(storage, query, edni); // query = entity id 
	 }
	 
	}

function loadEntityDetails(storage, q, id) {
	 var req = newXMLHttpRequest();
	 req.onreadystatechange = getReadyStateHandlerText(req, updateEntityDetails);
	 req.open("GET", "entity-details-more-data?storage=" + storage + "&q=" + q + "&eid=" + id, true);
	 req.send();
}

var entityLinksAccordion = new TINY.accordion.slider("entityLinksAccordion");

function updateEntityDetails(html) {	
	
	$jit.id('inner-details').innerHTML = html; 

	entityLinksAccordion.init("acc_video","h3",0,10);
//	 alert(videoAccordion);
}

function updateCanvas(json) {	
//	alert(json);
//	alert(jQuery.parseJSON( json ));

	// load JSON data.
	  fd.loadJSON(jQuery.parseJSON( json ));
	  
	  
	  // compute positions incrementally and animate.
	  fd.computeIncremental({
	    iter: 100,
	    property: 'end',
	    onStep: function(perc){
	      Log.write(perc + '% loaded...');
	    },
	    onComplete: function(){
	      Log.write('done');
	      fd.plot();
	      fd.refresh();
	    }
	  });
	  
	  setTimeout(function() {
		  var node = fd.graph.getNode(entityDetailsNodeId);
		  if (null != node)
		  {
		  showEntityOnGraph(node);
		  }
        }, 1500);
	  
}

function showEntityOnGraph(node) {

      node.selected = true;
      node.setData('dim', 17, 'end');
      node.eachAdjacency(function(adj) {
        adj.setDataset('end', {
          lineWidth: 3,
          color: adj.getData("color")
        });
      });
      
      //trigger animation to final styles
      fd.fx.animate({
        modes: ['node-property:dim',
                'edge-property:lineWidth:color'],
        duration: 500
      });      	
}

var labelType, useGradients, nativeTextSupport, animate;

(function() {
  var ua = navigator.userAgent,
      iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
      typeOfCanvas = typeof HTMLCanvasElement,
      nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
      textSupport = nativeCanvasSupport 
        && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
  //I'm setting this based on the fact that ExCanvas provides text support for IE
  //and that as of today iPhone/iPad current text support is lame
  labelType = (!nativeCanvasSupport || (textSupport && !iStuff))? 'Native' : 'HTML';
  nativeTextSupport = labelType == 'Native';
  useGradients = nativeCanvasSupport;
  animate = !(iStuff || !nativeCanvasSupport);
})();

var Log = {
  elem: false,
  write: function(text){
    if (!this.elem) 
      this.elem = document.getElementById('log');
    this.elem.innerHTML = text;
    this.elem.style.left = (500 - this.elem.offsetWidth / 2) + 'px';
  }
};


function initView(storage, q){
//  // init data
//	  var json = [
//	              {
//	                "adjacencies": [
//	                ],
//	                "data": {
//	                  "$color": "#83548B",
//	                  "$type": "circle",
//	                  "$dim": 10
//	                },
//	                "id": "graphnode0",
//	                "name": "graphnode0"
//	              }
//	              
//	          ];
//  // end
  // init ForceDirected
	fd = new $jit.ForceDirected({
	    //id of the visualization container
	    injectInto: 'infovis',
	    //Enable zooming and panning
	    //with scrolling and DnD
	    Navigation: {
	      enable: true,
	      //Enable panning events only if we're dragging the empty
	      //canvas (and not a node).
	      panning: 'avoid nodes',
	      zooming: 10 //zoom speed. higher is more sensible
	    },
	    // Change node and edge styles such as
	    // color and width.
	    // These properties are also set per node
	    // with dollar prefixed data-properties in the
	    // JSON structure.
	    Node: {
	      overridable: true,
	      dim: 7
	    },
	    Edge: {
	      overridable: true,
	      color: '#23A4FF',
	      lineWidth: 0.4
	    },
	    // Add node events
	    Events: {
	      enable: true,
	      type: 'Native',
	      //Change cursor style when hovering a node
	      onMouseEnter: function() {
	        fd.canvas.getElement().style.cursor = 'move';
	      },
	      onMouseLeave: function() {
	        fd.canvas.getElement().style.cursor = '';
	      },
	      //Update node positions when dragged
	      onDragMove: function(node, eventInfo, e) {
	        var pos = eventInfo.getPos();
	        node.pos.setc(pos.x, pos.y);
	        fd.plot();
	      },
	      //Implement the same handler for touchscreens
	      onTouchMove: function(node, eventInfo, e) {
	        $jit.util.event.stop(e); //stop default touchmove event
	        this.onDragMove(node, eventInfo, e);
	      },
	      //Add also a click handler to nodes
	      onClick: function(node) {
	        if(!node) return;
	        
	        // get details data
	        loadEntityDetails(storage, q, node.id);
        
	        //set final styles
	        fd.graph.eachNode(function(n) {
	          if(n.id != node.id) delete n.selected;
	          n.setData('dim', 7, 'end');
	          n.eachAdjacency(function(adj) {
	            adj.setDataset('end', {
	              lineWidth: 0.4,
	              color: adj.getData("color")
	            });
	          });
	        });
	        if(!node.selected) {
		          node.selected = true;
		          node.setData('dim', 17, 'end');
		          node.eachAdjacency(function(adj) {
		            adj.setDataset('end', {
		              lineWidth: 3,
		              color: adj.getData("color")
		            });
		          });
		        } else {
		          delete node.selected;
		        }
		        //trigger animation to final styles
		        fd.fx.animate({
		          modes: ['node-property:dim',
		                  'edge-property:lineWidth:color'],
		          duration: 500
		        });	        
	        
	      }	      
	    },
	    //Number of iterations for the FD algorithm
	    iterations: 200,
	    //Edge length
	    levelDistance: 130,
	    // This method is only triggered
	    // on label creation and only for DOM labels (not native canvas ones).
	    onCreateLabel: function(domElement, node){
	      // Create a 'name' and 'close' buttons and add them
	      // to the main node label
	      var nameContainer = document.createElement('span'),
	          closeButton = document.createElement('span'),
	          style = nameContainer.style;
	      nameContainer.className = 'name';
	      nameContainer.innerHTML = node.name;
	      closeButton.className = 'close';
	      closeButton.innerHTML = 'x';
	      domElement.appendChild(nameContainer);
	      domElement.appendChild(closeButton);
	      style.fontSize = "0.8em";
	      style.color = "#000"; // label text color
	      //Fade the node and its connections when
	      //clicking the close button
	      closeButton.onclick = function() {
	        node.setData('alpha', 0, 'end');
	        node.eachAdjacency(function(adj) {
	          adj.setData('alpha', 0, 'end');
	        });
	        
	        fd.fx.animate({
	          modes: ['node-property:alpha',
	                  'edge-property:alpha'],
	          duration: 500
	        });
	      };
	      //Toggle a node selection when clicking
	      //its name. This is done by animating some
	      //node styles like its dimension and the color
	      //and lineWidth of its adjacencies.
	      nameContainer.onclick = function() {
	        //set final styles
	        fd.graph.eachNode(function(n) {
	          if(n.id != node.id) delete n.selected;
	          n.setData('dim', 7, 'end');
	          n.eachAdjacency(function(adj) {
	            adj.setDataset('end', {
	              lineWidth: 0.4,
	              color: adj.getData("color")
	            });
	          });
	        });
	        if(!node.selected) {
	          node.selected = true;
	          node.setData('dim', 17, 'end');
	          node.eachAdjacency(function(adj) {
	            adj.setDataset('end', {
	              lineWidth: 3,
	              color: adj.getData("color")
	            });
	          });
	        } else {
	          delete node.selected;
	        }
	        //trigger animation to final styles
	        fd.fx.animate({
	          modes: ['node-property:dim',
	                  'edge-property:lineWidth:color'],
	          duration: 500
	        });
	        
	        loadEntityDetails(storage, q, node.id);

//	        // Build the right column relations list.
//	        // This is done by traversing the clicked node connections.
//	        var html = "<a href='/neuro4j/net-browser.htm?eid=" + node.id + "'>" + node.name + "</a> <br/> <b> connections:</b><ul><li>",
//	            list = [];
//	        node.eachAdjacency(function(adj){
//	          if(adj.getData('alpha')) list.push(adj.nodeTo.name);
//	        });
//	        //append connections information
//	        $jit.id('inner-details').innerHTML = html + list.join("</li><li>") + "</li></ul>";
	      };
	    },
	    // Change node styles when DOM labels are placed
	    // or moved.
	    onPlaceLabel: function(domElement, node){
	      var style = domElement.style;
	      var left = parseInt(style.left);
	      var top = parseInt(style.top);
	      var w = domElement.offsetWidth;
	      style.left = (left - w / 2) + 'px';
	      style.top = (top + 10) + 'px';
	      style.display = '';
	    }
	  });

//  getEntitiesData();
//  // load JSON data.
//  fd.loadJSON(json);
//  // compute positions incrementally and animate.
//  fd.computeIncremental({
//    iter: 40,
//    property: 'end',
//    onStep: function(perc){
//      Log.write(perc + '% loaded...');
//    },
//    onComplete: function(){
//      Log.write('done');
//      fd.animate({
//        modes: ['linear'],
//        transition: $jit.Trans.Elastic.easeOut,
//        duration: 2500
//      });
//    }
//  });
  // end
}



// TODO: rework - use JQUery


/*
 * Returns an new XMLHttpRequest object, or false if the browser
 * doesn't support it
 */
function newXMLHttpRequest() {

  var xmlreq = false;

  // Create XMLHttpRequest object in non-Microsoft browsers
  if (window.XMLHttpRequest) {
    xmlreq = new XMLHttpRequest();

  } else if (window.ActiveXObject) {

    try {
      // Try to create XMLHttpRequest in later versions
      // of Internet Explorer

      xmlreq = new ActiveXObject("Msxml2.XMLHTTP");
      
    } catch (e1) {

      // Failed to create required ActiveXObject
      
      try {
        // Try version supported by older versions
        // of Internet Explorer
      
        xmlreq = new ActiveXObject("Microsoft.XMLHTTP");

      } catch (e2) {

        // Unable to create an XMLHttpRequest by any means
        xmlreq = false;
      }
    }
  }

return xmlreq;
}

 /*
	* Returns a function that waits for the specified XMLHttpRequest
	* to complete, then passes it XML response to the given handler function.
  * req - The XMLHttpRequest whose state is changing
  * responseXmlHandler - Function to pass the XML response to
  */
 function getReadyStateHandler(req, responseXmlHandler) {

   // Return an anonymous function that listens to the XMLHttpRequest instance
   return function () {

     // If the request's status is "complete"
     if (req.readyState == 4) {
       
       // Check that we received a successful response from the server
       if (req.status == 200) {

         // Pass the XML payload of the response to the handler function.
         responseXmlHandler(req.responseXML);

       } else {

         // An HTTP problem has occurred
//         alert("HTTP error "+req.status+": "+req.statusText);
       }
     }
   }
 }

 function getReadyStateHandlerText(req, responseTextHandler) {
	   // Return an anonymous function that listens to the XMLHttpRequest instance
	   return function () {

	     // If the request's status is "complete"
	     if (req.readyState == 4) {
	       
	       // Check that we received a successful response from the server
	       if (req.status == 200) {

	         // Pass the XML payload of the response to the handler function.
	         responseTextHandler(req.responseText);

	       } else {

	         // An HTTP problem has occurred
//	         alert("HTTP error "+req.status+": "+req.statusText);
	       }
	     }
	   }
	 }

//Custom node 
 $jit.ForceDirected.Plot.NodeTypes.implement({ 
   //// this node type is used for plotting resource types (web) 
    'custom': 
        { 'render': function(node, canvas) { 
            var ctx = canvas.getCtx(); 
            var img = new Image(); 
            var pos = node.getPos(); 
            img.onload = function(){ 
                ctx.drawImage(img,pos.x-16, pos.y-16); 
            } 
            img.src = node.getData('chn_img_url'); 
        }, 
             'contains': 
             function(node, pos) 
                     { 
                         var npos = node.pos.getc(true), 
                         dim = node.getData('dim'); 
                         return this.nodeHelper.square. contains(npos, pos, 
 dim); 
                     } 
          } 
 });  

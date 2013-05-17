var StreamPlots = {
		activePlots : [ ],
		clear : function(stream) {
			var WIN_5M = 5*60*1000;
			var streamID = $('#'+stream.id).attr('stream_id');
			//StreamPlots.setWindow(stream, WIN_5M);
			$("input:radio[name$='"+streamID+"']").filter("[value='"+WIN_5M+"']").attr('checked', true);
		//	$("input:radio[name$='stream']").filter("[value='"+WIN_5M+"']").attr('checked', true);
			StreamPlots.setWindow(stream, WIN_5M);
			//StreamPlots.setup(stream);

			//StreamPlots.getStream(stream);
			stream.plot.setData([stream.points]);
			stream.plot.setupGrid();
  		stream.plot.draw();
		},
		clearSelection : function(event, plot) {
			var streamID = $(this).attr('stream_id');
			var overviewPlot = window['streamplot'+streamID].overview;
			overviewPlot.clearSelection();
		},
	setup : function(stream) {
		var streamID = $('#'+stream.id).attr('stream_id');
		var streamplot = '#streamplot' + streamID; // == '#'+stream.id
		StreamPlots.activePlots.push(streamID);
		var overview = '#overview' + streamID;
		$('#'+stream.id).bind("plothover", StreamPlots.plotHoverHandler);
		//console.debug("Hover bind.. ");
		$('#'+stream.id).bind("plotpan", StreamPlots.plotPanHandler);
		$('#'+stream.id).bind("plotzoom", StreamPlots.plotPanHandler);
		$(overview).bind("plotselected", StreamPlots.plotSelectHandler); 
		$('#'+stream.id).bind("click", StreamPlots.clearSelection);

	},
	plotPanHandler : function (event, plot) {
		var streamID = $(this).attr('stream_id');
		var streamplot = '#streamplot' + streamID;
		var overview = '#overview' + streamID;
		var overviewPlot = window['streamplot'+streamID].overview;
		var axes = plot.getAxes();
		overviewPlot.setSelection({ xaxis: { from: axes.xaxis.min, to: axes.xaxis.max }, yaxis: { from: axes.yaxis.min, to: axes.yaxis.max } }, true);
		//$('#tooltip'+streamID).remove();

		console.debug("Panning to x: "  + axes.xaxis.min.toFixed(2)
		+ " &ndash; " + axes.xaxis.max.toFixed(2)
		+ " and y: " + axes.yaxis.min.toFixed(2)
		+ " &ndash; " + axes.yaxis.max.toFixed(2));
	},
	plotSelectHandler : function (event, ranges) {
		var streamID = $(this).attr('stream_id');
		var streamplot = '#streamplot' + streamID;
		var overview = '#overview' + streamID;
		console.debug('plotSelectHandler ' + streamID);
		// clamp the zooming to prevent eternal zoom
		if (ranges.xaxis.to - ranges.xaxis.from < 0.00001) {
			ranges.xaxis.to = ranges.xaxis.from + 0.00001;
		}
		if (ranges.yaxis.to - ranges.yaxis.from < 0.00001) {
			ranges.yaxis.to = ranges.yaxis.from + 0.00001;
		}
		// do the zooming
		//stream.plot.setData([stream.points]);
		var plot = window['streamplot'+streamID].plot;
		var xaxis = plot.getAxes().xaxis;
        var opts = xaxis.options,
            min = ranges.xaxis.from,
            max = ranges.xaxis.to;
        if (min > max) {
            // make sure min < max
            var tmp = min;
            min = max;
            max = tmp;
        }
        opts.min = min;
        opts.max = max;
        plot.setupGrid();
        plot.draw();
		// don't fire event on the overview to prevent eternal loop
		//window['streamplot'+streamID].overview.setSelection(ranges, true);
	},
	
	poll : function(stream) {
		this.getStream(stream);
		stream.timeout = setTimeout(function() { StreamPlots.poll(stream); }, 10000);
	},
	
	setWindow : function(stream, window) {
		if (stream.timeout) clearTimeout(stream.timeout);
		$('#'+stream.id).css('background-color', 'silver');
		
		stream.points = [];
		stream.since = parseInt(((new Date()).getTime() - window));
		stream.window = window;
		this.poll(stream);

	},
	
	dateFormatter: function (val) {
	 val = val + (new Date(val)).getTimezoneOffset()*60*1000 ;
	 var date = new Date(val);
	 return date.toLocaleString();
	},
	
	plotHoverHandler : function (event, pos, item) {
		var previousPoint = null;	
		var streamID = this.id;
		var str = "(" + pos.x.toFixed(2) + ", " + pos.y.toFixed(2) + ")";
		//$("#hoverdata").text(str);
		//console.debug(streamID + ' Hover: '+str);
		if (item) {
			if (previousPoint != item.dataIndex) {

				previousPoint = item.dataIndex;

				$('#tooltip'+streamID).remove();
				//$('#'+streamID).tooltip('hide');
				var x = item.datapoint[0],
				y = item.datapoint[1].toFixed(2);
				StreamPlots.showTooltip(streamID, item.pageX, item.pageY,
				    y + " at " + StreamPlots.dateFormatter(x));
				//console.debug(streamID + ' y: '+y+ 'x: '+(new Date(x)).toLocaleString()+(new Date(x)).toUTCString());
			}
		} else {
			$('#tooltip'+streamID).remove();
			//$('#'+streamID).tooltip('hide');
			previousPoint = null;            
		}
	},
	//
	showTooltip : function(streamID,x, y, contents) {
		//console.debug(streamID + ' Tooltip2: x: '+ x + ' y: ' + y);
		//var tooltipOptions = {'title': contents};
		//$('#'+streamID).tooltip(tooltipOptions).tooltip('show');
		$("<div class='streampoint_tooltip' id='tooltip"+streamID+"'>" + contents + "</div>")
		.css({
			top: y + 5,
			left: x + 5
		})
		.appendTo('body').show();
	},

	getStream : function(stream) {		
		$.ajaxSetup({async:true});

		$.ajax({url: stream.uri+"?since="+stream.since, async: true, success: function(data) {
				//console.debug(data);
				var time = data["time"].reverse();
				var data = data["data"].reverse();
				console.debug('Number of points to push: '+time.length);
				for (var i=0; i< time.length; i++){
					
					stream.points.push(new Array(parseInt(time[i]) + StreamPlots.timezone, data[i]));
					//console.debug('pushed: '+parseInt(i));
				}
				
				if (stream.points.length>0) {
				
					stream.since = (stream.points[stream.points.length-1][0]-StreamPlots.timezone) + 1;
					console.debug('since: '+stream.since);				
	
					while (stream.points[0][0] < stream.points[stream.points.length-1][0] - stream.window) {
						// remove old plot points
						console.debug('shifted: '+stream.points.shift());						
					}
				

				var xaxis = stream.plot.getAxes().xaxis,
						overview_xaxis = stream.overview.getAxes().xaxis;
        var opts = xaxis.options,
        		overview_opts = overview_xaxis.options,
            min = stream.points[0][0],
            max = stream.points[stream.points.length-1][0];
        if (min > max) {
            // make sure min < max
            var tmp = min;
            min = max;
            max = tmp;
        }
        opts.min = min;
        opts.max = max;
        overview_opts.min = min;
        overview_opts.max = max;
				}
				stream.plot.setData([stream.points]);
				//will not redraw plot if overview is selected
				if(stream.overview.getSelection() == null) {
					stream.plot.setupGrid();
	    		stream.plot.draw();
				}
    		stream.overview.setData([stream.points]);
				stream.overview.setupGrid();
    		stream.overview.draw();
				$('#'+stream.id).css('background-color', '');
				//$('#tooltip'+streamID).remove();
			}});
	},

	timezone: -(new Date()).getTimezoneOffset()*60*1000, // in ms, distance to UTC (so negative)
	
	options: {
		series: {
			lines: { show: true },
			points: { show: false }
		},
		colors: [ "#88b" ],
		xaxis: {
			mode: "time",
			//timeformat: "%y-%m-%d %H:%M:%S",
			tickLength: 5,
			minTickSize: [1, "second"],
			//ticks: 6,
			max: parseInt(new Date().getTime())-new Date().getTimezoneOffset()*60*1000,
			zoomRange: [this.minTickSize, this.max],
			panRange: [null, this.max]
		},
		yaxis: {
			zoomRange: false,
			panRange: false
		},
		zoom: {
			interactive: true
		},
		pan: {
			interactive: true
		},
//		selection: {
//			mode: "xy"
//		},
		grid: {
			hoverable: true,
			clickable: false
		}
	},
	overview_options : {
		legend: {
			show: false
		},
		series: {
			lines: {
				show: true,
				lineWidth: 1
			},
			shadowSize: 0
		},
		xaxis: {
			mode: "time",
			timeformat: "%y-%m-%d",
			//minTickSize: [1, "second"],
			max: parseInt(new Date().getTime())-new Date().getTimezoneOffset()*60*1000,
			ticks: 1
		},
		yaxis: {
		},
		colors: [ "#77a" ],
		selection: {
			mode: "x"
		}
	}, 
	stopActivePlots : function(keep) {
		var streamplotToStop = "", streamToStop = null;
		if(StreamPlots.activePlots !== [ ]) {
	  	for (var ii=0; ii<StreamPlots.activePlots.length; ii++) {
	  		var sp = StreamPlots.activePlots[ii];
	  		if (sp != "destroy") {
	    		streamplotToStop = 'streamplot' + sp;
	  			//console.debug("1 plot: " + streamplotToStop);
	    		streamToStop =window['streamplot' + sp];
	    		if (typeof streamToStop !== 'undefined'  && streamToStop != null) {
	    			clearTimeout(streamToStop.timeout);
	    			console.debug("Stopping plot: " + streamplotToStop);
	    		}
	  		}
	  	}
	  	if(!keep) {
	  		StreamPlots.activePlots = [ ];
	  	}
		}
	},
	restartActivePlots : function() {
		var streamplotToStart = "", streamToStart = null;
		if(StreamPlots.activePlots !== [ ]) {
	  	for (var i=0; i<StreamPlots.activePlots.length; i++) {
	  		var sp = StreamPlots.activePlots[i];
	  		if (sp != "destroy") {
	    		streamplotToStart = 'streamplot' + sp;
	  			//console.debug("1 plot: " + streamplotToStart);
	    		streamToStart =window['streamplot' + sp];
	    		if (typeof streamToStart !== 'undefined'  && streamToStart != null) {
	    			StreamPlots.poll(streamToStart);
	    			console.debug("Restarting plot: " + streamplotToStart);
	    		}
	  		}
	  	}
		}
	}
};




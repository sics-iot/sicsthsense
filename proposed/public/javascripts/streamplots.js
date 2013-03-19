var StreamPlots = {
		clear : function(stream) {
			var WIN_5M = 5*60*1000;
			//StreamPlots.setWindow(stream, WIN_5M);
			$("input:radio[name='stream']").filter("[value='"+WIN_5M+"']").attr('checked', true);
			StreamPlots.setWindow(stream, WIN_5M);
			StreamPlots.setup(stream);

			StreamPlots.getStream(stream);
		},
	setup : function(stream) {
		var streamID = $('#'+stream.id).attr('stream_id');
		var streamplot = '#streamplot' + streamID; // == '#'+stream.id
		var overview = '#overview' + streamID;
		$('#'+stream.id).bind("plotclick", StreamPlots.plotHoverHandler);
		//console.debug("Hover bind.. ");
		$('#'+stream.id).bind("plotpan", StreamPlots.plotPanHandler);
		$('#'+stream.id).bind("plotzoom", StreamPlots.plotPanHandler);
		$(overview).bind("plotselected", StreamPlots.plotSelectHandler); 
	},
	plotPanHandler : function (event, plot) {
		var streamID = $(this).attr('stream_id');
		var streamplot = '#streamplot' + streamID;
		var overview = '#overview' + streamID;
		var overviewPlot = window['streamplot'+streamID].overview;
		var axes = plot.getAxes();
		overviewPlot.setSelection({ xaxis: { from: axes.xaxis.min, to: axes.xaxis.max }, yaxis: { from: axes.yaxis.min, to: axes.yaxis.max } }, true);
		
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
		.appendTo('body').fadeIn(200);
	},

	getStream : function(stream) {		
		$.ajaxSetup({async:true});

		$.ajax({url: stream.uri+"?since="+stream.since, async: true, success: function(data) {
				//console.debug(data);
				var time = data["time"].reverse();
				var data = data["data"].reverse();
				
				for (var i=0; i< time.length; i++){
					
					stream.points.push(new Array(parseInt(time[i]) + StreamPlots.timezone, data[i]));
					console.debug('pushed: '+parseInt(i));
				}
				
				if (stream.points.length>0) {
				
					stream.since = (stream.points[stream.points.length-1][0]-StreamPlots.timezone) + 1;
					console.debug('since: '+stream.since);				
	
					while (stream.points[0][0] < stream.points[stream.points.length-1][0] - stream.window) {
						// remove old plot points
						console.debug('shifted: '+stream.points.shift());
					}
				}

				stream.plot.setData([stream.points]);
				stream.plot.setupGrid();
    			stream.plot.draw();
    			stream.overview.setData([stream.points]);
				stream.overview.setupGrid();
    			stream.overview.draw();
				$('#'+stream.id).css('background-color', '');
			}});
	},

	timezone: -(new Date()).getTimezoneOffset()*60, // in ms, distance to UTC (so negative)
	
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
			max: parseInt(new Date().getTime())-new Date().getTimezoneOffset()*60,
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
			hoverable: false,
			clickable: true
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
			max: parseInt(new Date().getTime())-new Date().getTimezoneOffset()*60,
			ticks: 3
		},
		yaxis: {
		},
		colors: [ "#77a" ],
		selection: {
			mode: "x"
		}
	}
};




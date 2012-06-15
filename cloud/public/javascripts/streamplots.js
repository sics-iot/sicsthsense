var StreamPlots = {
	poll : function(stream) {
		$('#'+stream.id).css('background-color', 'red');
		this.getStream(stream);
		stream.timeout = setTimeout(function() { StreamPlots.poll(stream); }, 1000);
		$('#'+stream.id).css('background-color', '');
	},
	
	setWindow : function(stream, window) {
		if (stream.timeout) clearTimeout(stream.timeout);
		stream.points = [];
		stream.since = parseInt(((new Date()).getTime() - window) / 1000);
		stream.window = window;
		this.poll(stream);
	},
	
	getStream : function(stream) {
		$.get(stream.uri+"?since="+stream.since, function(data) {
				//console.debug(data);
				var update = data[stream.path];
				for (var point in update) {
					for (var t in update[point]) {
						// add reading to plot points
						stream.points.push(new Array(parseInt(t)*1000 + StreamPlots.timezone, update[point][t]));
						console.debug('pushed: '+parseInt(t));
					}
				}
				
				if (stream.points.length>0) {
				
					stream.since = (stream.points[stream.points.length-1][0]-StreamPlots.timezone)/1000 + 1;
					console.debug('since: '+stream.since);
	
					while (stream.points[0][0] < stream.points[stream.points.length-1][0] - stream.window) {
						// remove old plot points
						console.debug('shifted: '+stream.points.shift());
					}
					//console.debug(points);
					stream.plot.setData([stream.points]);
					stream.plot.setupGrid();
	    			stream.plot.draw();
				}
			});
	},

	timezone: -(new Date()).getTimezoneOffset()*60*1000, // in ms, distance to UTC (so negative)
	
	options: {
		series: {
			lines: { show: true },
			points: { show: false },
		},
		colors: [ "#88b" ],
		xaxis: {
			mode: "time",
			timeformat: "%y-%m-%d %H:%M:%S",
			minTickSize: [1, "second"]
		},
	}
};


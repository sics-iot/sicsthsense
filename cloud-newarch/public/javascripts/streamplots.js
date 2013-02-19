var StreamPlots = {
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
				$('#'+stream.id).css('background-color', '');
			}});
	},

	timezone: -(new Date()).getTimezoneOffset()*60, // in ms, distance to UTC (so negative)
	
	options: {
		series: {
			lines: { show: true },
			points: { show: false },
		},
		colors: [ "#88b" ],
		xaxis: {
			mode: "time",
			timeformat: "%y-%m-%d %H:%M:%S",
			minTickSize: [1, "second"],
			ticks: 6,
			max: parseInt(new Date().getTime())-new Date().getTimezoneOffset()*60
		},
	}
};


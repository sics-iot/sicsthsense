package 
{
	import flash.display.BitmapData;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.KeyboardEvent;
	import flash.events.SampleDataEvent;
	import flash.media.Camera;
	import flash.media.Video;
	import flash.net.URLLoader;
	import flash.text.TextField;
	import flash.utils.setInterval;
	import flash.net.*;
	import flash.net.URLLoaderDataFormat;
	import flash.media.Microphone;
	/**
	 * ...
	 * @author dfg
	 */
	public class Main extends Sprite 
	{
		static var instance:Main;
		public var vid:Video = new Video(); 
		var inputField:TextField;
		public var service:String;
		
		public function Main():void 
		{
			instance = this;
			var cam:Camera = Camera.getCamera(); 
			cam.setMode(10, 10, 24);
			var mic:Microphone = Microphone.getMicrophone();
			
			mic.addEventListener(SampleDataEvent.SAMPLE_DATA, micSampleDataHandler);
			
			vid.attachCamera(cam); 
			addChild(vid);
		
			this.y = 100;
			inputField = new TextField();

			addChild(inputField);
			inputField.border = true;
			inputField.width = 200;
			inputField.height = 25;
			inputField.x = 50;
			inputField.y = 250;
			inputField.type = "input";	
			inputField.text="http://sense.sics.se/streams/niwi/test/"
			inputField.stage.focus = inputField;
			inputField.addEventListener(KeyboardEvent.KEY_DOWN, listener);
			
		}
		
		public var volume:Number = 0;
		function micSampleDataHandler(event:SampleDataEvent):void
	{
		var mem:Number = 0;
		var n:int = 0;
	    while (event.data.bytesAvailable)
	    {
	        var sample:Number = event.data.readFloat();
	        mem += sample * sample;
			n++;
	    }
		volume=Math.sqrt(mem/n);
	}
		
		function listener(event:KeyboardEvent):void {
			if (event.keyCode == 13) {
				service = inputField.text;
			}
		}
		static function w():void {					
			var vid:Video = instance.vid;
			var snapshot:BitmapData = new BitmapData(vid.width, vid.height);
			snapshot.draw(vid);
			var sum:int;
			for (var i:int = 1; i <= vid.width; i++) { 
				for (var j:int=1; j<=vid.height;j++){ 
				var nc:uint = snapshot.getPixel(i, j);
			
				var nr:int =nc>>16&0xff; 
				//green channel 
				var ng:int =nc>>8&0xff; 
				//blue channel 
				var nb:int =nc&0xff; 
				//brightness 
				sum += Math.sqrt(nr * nr + ng * ng + nb * nb);
				}
			}
			sum= sum / (vid.width * vid.height);
			trace(sum);
			if (instance.service != null && instance.service.length>0) {
				var req:URLRequest  = new URLRequest(instance.service+"/cam");
				//var req:URLRequest  = new URLRequest("http://sense.sics.se/streams/niwi/test/t3");
				req.method = URLRequestMethod.POST;
				//req.contentType= 
				req.data = sum.toString();
				var loader:URLLoader = new URLLoader();
				loader.dataFormat = URLLoaderDataFormat.TEXT;
				req.contentType = "application/json; charset=UTF-8";
				loader.addEventListener(Event.COMPLETE, handleComplete);
				loader.addEventListener(IOErrorEvent.IO_ERROR, onIOError);
				loader.load(req);

				var req2:URLRequest  = new URLRequest(instance.service+"/mic");
				//var req:URLRequest  = new URLRequest("http://sense.sics.se/streams/niwi/test/t3");
				req2.method = URLRequestMethod.POST;
				//req.contentType= 
				req2.data = instance.volume.toString();
				var loader2:URLLoader = new URLLoader();
				loader2.dataFormat = URLLoaderDataFormat.TEXT;
				req2.contentType = "application/json; charset=UTF-8";
				loader2.addEventListener(Event.COMPLETE, handleComplete);
				loader2.addEventListener(IOErrorEvent.IO_ERROR, onIOError);
				loader2.load(req2);

			}
			
		}	
		setInterval(w, 1000);
	
		private static function handleComplete(event:Event):void {
			var loader:URLLoader = URLLoader(event.target);
		trace("Par: " + loader.data);
		trace("Message: " + loader.data);
	}
	private static function onIOError(event:IOErrorEvent):void {
	trace("Error loading URL. " + event.toString());
	instance.inputField.text = "Error loading URL. " + event.toString();	
	}
		
	}
	
}
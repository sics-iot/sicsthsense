package se.sics.sense.filebased;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class Poller extends Thread{

	private LineConsumer consumer;
	private long lastTime, interval;
	private Resource resource;

	
	public Poller(LineConsumer consumer,Resource resource){
		this(consumer, 0, 500, resource);
			
	}
	
	public Poller(LineConsumer consumer, long lastTime, long interval, Resource resource){
		this.consumer = consumer;		
		this.lastTime=lastTime;
		this.resource=resource;
		this.interval = interval;
	}
	
	@Override
	public void run() {
	
			
		while(true){
			try{
				String urlstr = resource.fullUrl+"?since="+(lastTime+1);
				System.out.println(urlstr);
				URL url = new URL(urlstr);
			//	URL url = new URL("http://localhost:9000/streams/niwi/test/t1?tail=10");
				//	System.out.println(url);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				InputStream is = conn.getInputStream();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line;		
				
				while((line=br.readLine())!=null){	
				
				
				
					JsonObject jo = new JsonParser().parse(line).getAsJsonObject();
				
					//JsonArray ja = jo.get(resource.devices).getAsJsonArray();
					JsonArray time = jo.get("time").getAsJsonArray();
					JsonArray data = jo.get("data").getAsJsonArray();
					
					for(int i =0 ; i< time.size(); i++){
						long t = time.get(i).getAsLong();
						double d = data.get(i).getAsDouble();
						//long time = Long.parseLong(entry.getKey());
						if(lastTime<t)
							lastTime=t;
						consumer.consume(t+" "+d);
					}
				}
//					for(JsonElement e : ja)
//						for(Map.Entry<String, JsonElement> entry :e.getAsJsonObject().entrySet()){
//							long time = Long.parseLong(entry.getKey());
//							if(time<=lastTime)
//								continue;
//							String value= entry.getValue().getAsString();
//							//System.out.println((i++)+": " +entry.getKey()+" "+value);
//							if(lastTime<time)
//								lastTime=time;
//							consumer.consume(time+" "+value);				
//						}					
//				}				
				Thread.sleep(interval);
			
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	static class StdOutLineConsumer implements LineConsumer{
		@Override
		public void consume(String line) {
			System.out.println(line);
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		if(args.length>0){
			LineConsumer consumer = args.length==1 ? new StdOutLineConsumer() : new FileAppendingLineConsumer(args[1]);
			Resource resource = new Resource(args[0]);
			new Poller(consumer,resource).start();
		}else{
			//Resource resource = new Resource("http://sense.sics.se/streams/simon/phone/sensors/orientation/x");
			Resource resource = new Resource("http://localhost:9000/streams/niwi/test/t1");
			new Poller(new FileAppendingLineConsumer("test.in"),resource).start();
		}

	}
}
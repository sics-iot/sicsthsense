package se.sics.sense.filebased;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Poster extends Thread{
	private InputStream is; 
	private LineConsumer consumer;
	private boolean skipHistory;
	public Poster(InputStream is, LineConsumer consumer, boolean skipHistory) {
		this.is =is;
		this.consumer=consumer;
		this.skipHistory=skipHistory;
	}
	@Override
	public void run() {
		try{			
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			if(skipHistory)
				while((line=br.readLine())!=null);
			
			while(true){
				while((line=br.readLine())==null){
					Thread.sleep(200);
				}
				
				consumer.consume(line);

			}
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	static class MyLineConsumer implements LineConsumer{
		@Override
		public void consume(String line) {
			System.out.println(line);
		}
	
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		if(args.length>0){
			InputStream in = args.length==1 ? System.in : new FileInputStream(args[1]);
			Resource resource = new Resource(args[0]);
			LineConsumer consumer = new PostingLineConsumer(resource);
			new Poster(in,consumer,true).start();
		}else{
			Resource resource = new Resource("sense.sics.se/streams/niwi/test/t1");
			LineConsumer consumer = new PostingLineConsumer(resource);
			new Poster(new FileInputStream("test.out"),consumer,true).start();
		}
		
	}
}
package se.sics.sense.filebased;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class Engine {
	
	
	static void parse(String file) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while((line=br.readLine())!=null){
			//System.out.println(line);
			String[] w = line.split("\\s+");
			if(w.length!=3)
				continue;
			Resource resource = new Resource(w[1]);
			if(w[0].equals("poll")){
				LineConsumer consumer =  new FileAppendingLineConsumer(w[2]);				
				new Poller(consumer,resource).start();
			}else if(w[0].equals("post")){
				InputStream in = new FileInputStream(w[2]);
				LineConsumer consumer = new PostingLineConsumer(resource);
				new Poster(in,consumer,true).start();
			}
			
		}
	}
	
	public static void main(String[] args) throws IOException {
		if(args.length!=0)
			parse(args[0]);
		else
			parse("config_example");
	}
}

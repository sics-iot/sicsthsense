package se.sics.sense.filebased;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class PostingLineConsumer implements LineConsumer{
	private URL url;
	

	private Resource resource;


	public PostingLineConsumer(Resource resource) throws MalformedURLException {
		super();
		this.resource = resource;
		url=new URL(resource.baseUrl);
		
		
	}


	public void consume(String line) {		

		try {
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			System.out.println(url);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");			
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			System.out.println(line);
			String json=resource.getJson(line);
			System.out.println("posting json: "+json);
			os.write(json.getBytes());
			os.flush();
			os.close();
			System.out.println("response: "+conn.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
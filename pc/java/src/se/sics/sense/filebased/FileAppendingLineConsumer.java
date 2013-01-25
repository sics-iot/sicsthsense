package se.sics.sense.filebased;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class FileAppendingLineConsumer implements LineConsumer {

	private PrintWriter writer;
	public FileAppendingLineConsumer(String fileName) throws FileNotFoundException{
		this(new FileOutputStream(fileName,true));
		
	}
	public FileAppendingLineConsumer(OutputStream os) {
		
		writer = new PrintWriter(os);
	}
	
	public void consume(String line) {		
		writer.println(line);
		writer.flush();
		
	}
}

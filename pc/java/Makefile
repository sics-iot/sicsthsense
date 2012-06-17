



clean:
	rm -rf bin/se
		
compile:
	javac -d bin -cp bin/:lib/google-gson-2.2.1/gson-2.2.1.jar src/se/sics/sense/filebased/*.java

upjar:
	(cd bin; jar -uf ../sicsthdrive.jar  se/)
	
	
run:
	java -jar sicsthdrive.jar
	
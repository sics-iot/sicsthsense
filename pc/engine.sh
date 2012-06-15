
javac -d bin -cp bin/:lib/google-gson-2.2.1/gson-2.2.1.jar src/se/sics/sense/filebased/*.java
java -cp bin/:lib/google-gson-2.2.1/gson-2.2.1.jar se.sics.sense.filebased.Engine $1
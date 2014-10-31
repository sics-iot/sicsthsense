SicsthSense Engine
===========

The Engine is a Java implementation of a RESTful HTTP server representing the SicsthSense cloud presence.

It uses DropWizard, which is a distribution of popular Java frameworks (Jetty, Jersey, Jackson, JDBI, slf4j, etc.). Other external libraries used include: Atmosphere (for websockets). A MySQL server is required for the data storage.

The build system is Maven, allowing automatic dependency management for all of the included 3rd party libraries.

To build the engine from the /engine directory simply:

$ mvn package

This will create the server in a self-contained .jar file "/engine/target/engine-1.0-SNAPSHOT-shaded.jar"

To run this server:

$ ./run.sh
or
$ java -jar target/engine-1.0-SNAPSHOT.jar server 6sense.yml


The github Wiki "https://github.com/sics-iot/sicsthsense/wiki" also contains a breakdown of the codebase

There should now be an HTTP server running on port 8080. Visiting this with a web browser should confirm it. From here you may authenticate using OpenID or a username/password combination.


SSL 
===

The keysotre server.key is merely a placeholder (everyone should not use the
same keystore). It protected with the pass phrase “password”. You should create
a new keystore before production deployment, probably with a command such as:
openssl genrsa -des3 -out server.key 1024


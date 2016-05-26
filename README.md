SICSthSense
===========

We have a documentation site describing the project and how to get started!
http://docs.sense.sics.se

This repository contain many different aspects of the SICSthSense system:

* `Engine`: The core server of SicsthSense, a lightweight RESTful HTTP server in Java.
* `Web`: A web based engine providing the SICSthSense engine and a more attractive web interface also in Java.
* `Library`: A python library for easily interacting with the SICSthSense engine.
* `Contiki`: Code for installing on small devices that can then talk to SICSthSense.
* `Android`: Preliminary code to run on Android phones.
* `Deprecated`: old code and prototypes.

The `INSTALL.sh` script will build the database (using `buildDB.sql`) and copy the required configuration files (for Play in `web`)



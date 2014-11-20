To build this code you must point the __CONTIKI__ variable in the Makefile to
your Contiki source directory.

To configure the code in order that it posts to the correct SicsthSense
Resource you must set the following variables in the json-6sense.c file:
* SicsthSense IP (optional)
* USERID (your User ID on the relevant SicsthSense instance)
* RESOURCEID (the resource the POST should be sent to)
* KEY (your user/resource secret UUID key)

The code will periodically HTTP POST a static JSON document to the SicsthSense server.

------------------------------------

Short description on how to set-up a sensor network for global IPv6 addresses.
NOTE: this assumes that you do not have a native IPv6 connection.

You will need:
* PC with Ubuntu (Linux) - 11 or 12 versions
* A node for the RPL-Border-Router (examples/ipv6/rpl-border-router)
* A node for the json webservice (examples/ipv6/json-ws)

Set-up IPv6 tunnel and Border Router
------------------------------------
1. Ensure that you have gogo6c installed.

> sudo apt-get install gogoc

2. Register an account at gogo6 and Freenet6 (http://www.gogo6.com).
   The account at Freenet6 is needed by the gogo6c client.

3. Edit the gogoc.conf and set your own Freenet6 user and password by
   changing the lines with "userid" and "passwd".

4. Start gogoc at command line

> cd contiki/examples/ipv6/json-ws
> sudo gogoc -f gogoc.conf -n

This will print your prefix - TSP_PREFIX.
In my case TSP_PREFIX=2001:05c0:1517:e400 (prefixlen is 56).

5. Connect one of the nodes to the PC (via USB or serial) and program
   it with the RPL-border-router (assumes Z1 node).

> cd contiki/examples/ipv6/rpl-border-router
> make DEFINES=DEFINES=NETSTACK_RDC=nullrdc_driver,NULLRDC_CONF_802154_AUTOACK=1 TARGET=z1 border-router.upload

6. Run tunslip6 which will forward IP from the RPL network to
   the IPv6 tunnel (and to the Internet).

> cd contiki/examples/ipv6/rpl-border-router
> make connect-router PREFIX=<TSP_PREFIX>::1/64

   When you start this you should get a printout from the border-router
   which give you the IPv6 address of it.

Server IPv6 addresses:
 2001:05c0:1517:e400:c30c::10a
 fe80::c30c:0:0:10a

7. Browse using Mozilla Firefox (or any other browser) to the IPv6 address
   given by the border router. This will show you the list of other nodes
   connected to the RPL network.

   http://[2001:05c0:1517:e400:c30c::10a]/

   NOTE: this is a global IPv6 address so it should also be reachable from
   any machine on the Internet.



# The following needs to be set to your Contiki source directory
CONTIKI=/home/user/contiki-2.7

WITH_UIP6=1 
UIP_CONF_IPV6=1 
SMALL=1 

#PROJECT_SOURCEFILES += json-6sense.c
APPS = httpd-ws

ifeq ($(TARGET),)
 -include Makefile.target
endif

ifndef CONTIKI
$(error CONTIKI directory path is not set!)
endif

include $(CONTIKI)/Makefile.include

#This needs to be defined! 
CONTIKI=/Users/ljjm/code/contiki


UIP_CONF_IPV6=1
SMALL=1

PROJECT_SOURCEFILES += json-6sense.c


APPS += httpd-ws json

CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"


ifeq ($(TARGET),)
 -include Makefile.target
endif

ifneq ($(TARGET),)
all: websense-$(TARGET)
endif

ifndef CONTIKI
$(error CONTIKI directory path is not set!)
endif

include $(CONTIKI)/Makefile.include

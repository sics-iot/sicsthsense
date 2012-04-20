all: sicsthsense

CONTIKI=/home/user/contiki

WITH_UIP6=1
UIP_CONF_IPV6=1
CFLAGS+= -DUIP_CONF_IPV6_RPL

CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"
PROJECT_SOURCEFILES += httpd-simple.c
CFLAGS += -DPLATFORM_NAME_STR=\"$(TARGET)\"

include $(CONTIKI)/Makefile.include

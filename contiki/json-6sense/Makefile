
# The following needs to be set to your Contiki source directory
CONTIKI=/.../code/contiki

PROJECT_SOURCEFILES += json-6sense.c

ifeq ($(TARGET),)
 -include Makefile.target
endif

ifndef CONTIKI
$(error CONTIKI directory path is not set!)
endif

include $(CONTIKI)/Makefile.include

/*
 * Copyright (c) 2010, Swedish Institute of Computer Science.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * $Id: websense-remote.c,v 1.2 2010/06/14 14:12:43 nifi Exp $
 */

/**
 * \file
 *         A SicsthSense-ready webserver
 * \author
 *         Simon Duquennoy <simonduq@sics.se>
 */

#include <stdio.h>
#include "contiki.h"
#include "httpd-simple.h"

#include "dev/leds.h"
#include "dev/light-sensor.h"
#include "dev/battery-sensor.h"
#include "dev/sht11-sensor.h"

/* Device unique identifier */
static char uid[64];
/* Shared response buffer */
static char buff[UIP_BUFSIZE];

PROCESS(sicsthsense_process, "SicsthSense");
AUTOSTART_PROCESSES(&sicsthsense_process);

/* List of resources */
static char *get_discover() {
  snprintf(buff, sizeof(buff),
      "{"
      "\"uid\": \"%s\","
      "\"resources\": ["
      "\"/sensors/temp\","
      "\"/sensors/humidity\","
      "\"/sensors/light\""
       "]"
       "}", uid
    );

  return buff;
}

static char *get_temp() {
  snprintf(buff, sizeof(buff), "%d", sht11_sensor.value(SHT11_SENSOR_TEMP));
  return buff;
}

static char *get_humidity() {
  snprintf(buff, sizeof(buff), "%d", sht11_sensor.value(SHT11_SENSOR_HUMIDITY));
  return buff;
}

static char *get_light() {
  snprintf(buff, sizeof(buff), "%d", light_sensor.value(LIGHT_SENSOR_PHOTOSYNTHETIC));
  return buff;
}

/*---------------------------------------------------------------------------*/
static
PT_THREAD(handle_get(struct httpd_state *s))
{
  PSOCK_BEGIN(&s->sout);

  /* really ugly static declaration of resources. to be replaced by erbium resources soon */
         if(strcmp(s->filename, "/discover") == 0) {
    SEND_STRING(&s->sout, get_discover());
  } else if(strcmp(s->filename, "/sensors/temp") == 0) {
    SEND_STRING(&s->sout, get_temp());
  } else if(strcmp(s->filename, "/sensors/humidity") == 0) {
    SEND_STRING(&s->sout, get_humidity());
  } else if(strcmp(s->filename, "/sensors/light") == 0) {
    SEND_STRING(&s->sout, get_light());
  } else {
    SEND_STRING(&s->sout, "Page not found");
  }

  PSOCK_END(&s->sout);
}
/*---------------------------------------------------------------------------*/
httpd_simple_script_t
httpd_simple_get_script(const char *name)
{
  return handle_get;
}

/*---------------------------------------------------------------------------*/
PROCESS_THREAD(sicsthsense_process, ev, data)
{
  static struct etimer periodic;
  PROCESS_BEGIN();

  httpd_init();
  SENSORS_ACTIVATE(light_sensor);
  SENSORS_ACTIVATE(sht11_sensor);

  snprintf(uid, sizeof(uid), "Contiki_%s_%02x-%02x-%02x-%02x-%02x-%02x-%02x-%02x",
        PLATFORM_NAME_STR,
        rimeaddr_node_addr.u8[0], rimeaddr_node_addr.u8[1],
        rimeaddr_node_addr.u8[2], rimeaddr_node_addr.u8[3],
        rimeaddr_node_addr.u8[4], rimeaddr_node_addr.u8[5],
        rimeaddr_node_addr.u8[6], rimeaddr_node_addr.u8[7]
  );

  /* Main loop */
  while(1) {
    PROCESS_WAIT_EVENT_UNTIL(ev == tcpip_event);
    httpd_appcall(data);
  }

  PROCESS_END();
}
/*---------------------------------------------------------------------------*/

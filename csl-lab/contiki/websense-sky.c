/*
 * Copyright (c) 2011-2012, Swedish Institute of Computer Science.
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
 * This file is part of the Contiki operating system.
 */

/**
 * \file
 *         Websense for Sky mote
 * \author
 *         Niclas Finne    <nfi@sics.se>
 *         Joakim Eriksson <joakime@sics.se>
 *         Joel Hoglund    <joel@sics.se>
 */

#include "contiki.h"
#include "net/uip-ds6.h"
#include "dev/leds.h"
#include "dev/sht11-sensor.h"
#include "dev/light-sensor.h"
#include "jsontree.h"
#include "json-ws.h"
#include <stdio.h>

#define DEBUG 0
#if DEBUG
#define PRINTF(...) printf(__VA_ARGS__)
#else
#define PRINTF(...)
#endif

PROCESS(websense_process, "Websense (sky)");
AUTOSTART_PROCESSES(&websense_process);

/*---------------------------------------------------------------------------*/
static void
output_float(struct jsontree_context *path, int x100) {
  char buf[10];
  int tmp;
  int tmp2;
  tmp2 = x100;
  tmp = tmp2 / 100;

  snprintf(buf, sizeof(buf), "%2d.%02d", tmp, tmp2 - (100 * tmp));
  jsontree_write_atom(path, buf);
}
/*---------------------------------------------------------------------------*/
static int
output_temp(struct jsontree_context *path)
{
  output_float(path, sht11_sensor.value(SHT11_SENSOR_TEMP) - 3960);
  return 0;
}
static struct jsontree_callback temp_sensor_callback =
  JSONTREE_CALLBACK(output_temp, NULL);
/*---------------------------------------------------------------------------*/
static int
output_hum(struct jsontree_context *path)
{
  int ms, hum;
  ms = sht11_sensor.value(SHT11_SENSOR_HUMIDITY);
  /* this is in * 10000 */
  /* -2.0468  + 0.0367 * ms +  -1.5955e-6 * ms * ms ...too small value...  */
  hum = (-20468L + 367L * ms) / 100L;
  output_float(path, hum);
  return 0;
}
static struct jsontree_callback hum_sensor_callback =
  JSONTREE_CALLBACK(output_hum, NULL);
/*---------------------------------------------------------------------------*/
static int
output_light(struct jsontree_context *path)
{
  int v;
  v = light_sensor.value(LIGHT_SENSOR_TOTAL_SOLAR);
  jsontree_write_int(path, v);
  return 0;
}
static struct jsontree_callback light_sensor_callback =
  JSONTREE_CALLBACK(output_light, NULL);
/*---------------------------------------------------------------------------*/
/* static int */
/* output_url(struct jsontree_context *path) */
/* { */
/*   char buf[40]; */
/*   uip_ds6_addr_t *addr = uip_ds6_get_global(-1); */
/*   snprintf(buf, 40, "http://[%04x:%04x:%04x:%04x:%04x:%04x:%04x:%04x]/", */
/* 	   UIP_HTONS(addr->ipaddr.u16[0]), UIP_HTONS(addr->ipaddr.u16[1]), */
/* 	   UIP_HTONS(addr->ipaddr.u16[2]), UIP_HTONS(addr->ipaddr.u16[3]), */
/* 	   UIP_HTONS(addr->ipaddr.u16[4]), UIP_HTONS(addr->ipaddr.u16[5]), */
/* 	   UIP_HTONS(addr->ipaddr.u16[6]), UIP_HTONS(addr->ipaddr.u16[7])); */
/*   jsontree_write_string(path, buf); */
/*   return 0; */
/* } */
/* static struct jsontree_callback url_callback = */
/*   JSONTREE_CALLBACK(output_url, NULL); */
/*---------------------------------------------------------------------------*/
static struct jsontree_string desc = JSONTREE_STRING("Tmote Sky");

JSONTREE_OBJECT(node_tree,
/*                 JSONTREE_PAIR("url", &url_callback), */
                JSONTREE_PAIR("description", &desc),
                JSONTREE_PAIR("time", &json_time_callback));


JSONTREE_OBJECT(sensor_tree,
                JSONTREE_PAIR("temperature", &temp_sensor_callback),
                JSONTREE_PAIR("humidity", &hum_sensor_callback),
                JSONTREE_PAIR("light", &light_sensor_callback));

JSONTREE_OBJECT(act_tree,
                JSONTREE_PAIR("leds", &json_leds_callback));

/* complete node tree */
JSONTREE_OBJECT(tree,
                JSONTREE_PAIR("node", &node_tree),
                JSONTREE_PAIR("sensors", &sensor_tree),
                JSONTREE_PAIR("actuators", &act_tree),
                JSONTREE_PAIR("cfg", &json_subscribe_callback));

/*---------------------------------------------------------------------------*/
/* for cosm plugin */
#if WITH_COSM
/* set COSM value callback to be the temp sensor */
struct jsontree_callback cosm_value_callback =
  JSONTREE_CALLBACK(output_temp, NULL);
#endif

PROCESS_THREAD(websense_process, ev, data)
{
  static struct etimer timer;
  PROCESS_BEGIN();

  json_ws_init(&tree, "TmoteSky");

  SENSORS_ACTIVATE(sht11_sensor);
  SENSORS_ACTIVATE(light_sensor);

  json_ws_set_callback("sensors");
  

  while(1) {
    /* Alive indication with the LED */
    etimer_set(&timer, CLOCK_SECOND * 5);
    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));
    leds_invert(LEDS_RED);
    etimer_set(&timer, CLOCK_SECOND / 8);
    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));
    leds_invert(LEDS_RED);
  }

  PROCESS_END();
}
/*---------------------------------------------------------------------------*/

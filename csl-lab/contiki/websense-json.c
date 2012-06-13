/*
 * Copyright (c) 2011, Swedish Institute of Computer Science.
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
 * $Id: websense-sky.c 279 2012-04-16 03:00:26Z nfi $
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
#include "dev/sht11-sensor.h"
#include "dev/light-sensor.h"
#include "websense-json.h"
#include "node-id.h"
#include <stdio.h>
#include <string.h>

#define DEBUG 0
#if DEBUG
#include <stdio.h>
#define PRINTF(...) printf(__VA_ARGS__)
#else
#define PRINTF(...)
#endif

PROCESS(websense_process, "Websense (sky)");
AUTOSTART_PROCESSES(&websense_process);

static int
write_int(struct json_context *path, int value)
{
  char buf[10];
  snprintf(buf, sizeof(buf), "%5d", value);
  json_write_atom(path, buf);
  return 0;
}
static int
light_output(struct json_context *path)
{
  int light = 10 * light_sensor.value(LIGHT_SENSOR_PHOTOSYNTHETIC) / 7;
  return write_int(path, light);
}
static struct json_callback light_callback =
  JSON_CALLBACK(light_output, NULL);
static int
temp_output(struct json_context *path)
{
  int temp = ((sht11_sensor.value(SHT11_SENSOR_TEMP) / 10) - 396) / 10;
  return write_int(path, temp);
}
static struct json_callback temp_callback =
  JSON_CALLBACK(temp_output, NULL);
/*---------------------------------------------------------------------------*/

JSON_OBJECT(rsc_tree,
	    JSON_PAIR("light", &light_callback),
	    JSON_PAIR("temp", &temp_callback));

/* only a sub node below the current configuration */
JSON_OBJECT(cfg_tree,
	    JSON_PAIR("sub", &json_subscribe_callback));

/* complete node tree */
JSON_OBJECT(tree,
	    JSON_PAIR("rsc", &rsc_tree),
	    JSON_PAIR("cfg", &cfg_tree));
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(websense_process, ev, data)
{
  PROCESS_BEGIN();

  websense_json_init(&tree);

  SENSORS_ACTIVATE(light_sensor);
  SENSORS_ACTIVATE(sht11_sensor);

  websense_json_set_callback("rsc");

  PROCESS_END();
}
/*---------------------------------------------------------------------------*/

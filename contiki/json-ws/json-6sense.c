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
 *         JSON webservice util
 * \author
 *         Niclas Finne    <nfi@sics.se>
 *         Joakim Eriksson <joakime@sics.se>
 *         Joel Hoglund    <joel@sics.se>
 *         Liam McNamara   <ljjm@sics.se>
 */

#include "contiki.h"
#if PLATFORM_HAS_LEDS
#include "dev/leds.h"
#endif
#include "httpd-ws.h"
#include "json-ws.h"
#include <stdio.h>
#include <string.h>

#define DEBUG 0
#if DEBUG
#define PRINTF(...) printf(__VA_ARGS__)
#else
#define PRINTF(...)
#endif

static const char HOST[] = "http://localhost:8080";
static const char USERID[] = "2";
static const char RESOURCEID[] = "3";
static const char http_content_type_json[] = "application/json";

/* Maximum 40 chars in host name?: 5 x 8 */
static char callback_host[40] = "[aaaa::1]";
static uint16_t callback_port = CALLBACK_PORT;
static uint16_t callback_interval = SEND_INTERVAL;
static char callback_path[80] = "/debug/";
static char callback_appdata[80] = "";
static char callback_proto[8] = CALLBACK_PROTO;
static const char *callback_json_path = NULL;
static struct jsontree_object *tree;
static struct ctimer periodic_timer;
long json_time_offset = 0;

/*---------------------------------------------------------------------------*/


char* make_json() {
	return "{ \"value\":999 }\n";
}

char* make_url() {
	return HOST+"/users/"+USERID+"/resources/"+RESOURCEID+"/data";
}

void post_json(char* json) {
	char* data = make_json();
	char* url = make_url();

	PRINTF("JSON data: %s\n",data);
	PRINTF("URL: %s\n",url);
	/*
	s = httpd_ws_request(HTTPD_WS_PUT, callback_host, HOST,
		callback_port, callback_path,
		http_content_type_json, callback_size, send_values);
	*/
}

PROCESS(program_process, "JSON poster");
static struct etimer timer;

PROCESS_THREAD(program_process, ev, data) {
  PROCESS_BEGIN();
	while (1) {
		etimer_set(&timer, CLOCK_SECOND);
		PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));

		post_json();
	}
  PROCESS_END();
}



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
 */

/**
 * \file
 *         JSON POSTing Contiki code.
 *         Periodically HTTP POST a JSON document to the specified SicsthSense
 *         host and resource using the supplied authorisation key.
 * \author
 *         Liam McNamara   <ljjm@sics.se>
 */

#include "contiki.h"
#include "httpd-ws.h"
#include <stdio.h>
#include <string.h>

#define DEBUG 1
#if DEBUG
#define PRINTF(...) printf(__VA_ARGS__)
#else
#define PRINTF(...)
#endif

/*
  The following block of variables should be customised to your setup!
*/
static const char HOST[] = "localhost"; // hostname of server
static const char IP[] = "[aaaa::1]"; // IPv6 address of server
static const uint16_t PORT = 8080;
static const char USERID[] = "1"; // ID of SicsthSense account
static const char RESOURCEID[] = "1"; // Destination resource ID
static const char KEY[]="b2591047-defd-4076-96d5-cd411969badf"; // User or Resource key
static const int  PERIOD=10; // number of seconds between POSTs

static const char http_content_type_json[] = "application/json";
char json[255]; // storage for JSON document to be POST\d
char url[255];  // storage for constructed URL containing USER + RESOURCE IDs

/*---------------------------------------------------------------------------*/

// Populate the json variable with a JSON string to be POST'd
// This should replaced with whatever sensor collection you do
void make_json(void) {
	strcpy(json,"{ \"value\":999 }\n");
}

// Build the URL that the JSON will be posted to, likely containing a key
void make_url(void) {
	strcpy(url,"/users/");
	strcat(url,USERID);
	// this shortened form is allowed by SicsthSense, it helps with the restricted
	// Contiki HTTP path length restriction
	strcat(url,"/r/");
	strcat(url,RESOURCEID);
	strcat(url,"/d");
	if (KEY!=NULL) {
		strcat(url,"?key=");
		strcat(url,KEY);
	}
}

static PT_THREAD(send_json(struct httpd_ws_state *s)) {

  PSOCK_BEGIN(&s->sout);

  memcpy(s->outbuf, json, strlen(json));
  s->outbuf_pos = strlen(json);

  while(s->outbuf_pos > 0) {
    if(s->outbuf_pos >= UIP_TCP_MSS) {
      //PRINTF("SENDING MSS STRING: ");
      SEND_STRING(&s->sout, s->outbuf, UIP_TCP_MSS);
      s->outbuf_pos -= UIP_TCP_MSS;
    } else {
      SEND_STRING(&s->sout, s->outbuf, s->outbuf_pos);
      //PRINTF("SENDING SHORT STRING: %s\n",s->outbuf);
      s->outbuf_pos = 0;
    }
  }
  PSOCK_END(&s->sout);
}

void post_json(void) {
	struct httpd_ws_state *s;

	make_json();

	//PRINTF("JSON data: %s of size: %d\n",json,strlen(json));
	//PRINTF("URL: %s\n",url);

	s = httpd_ws_request(HTTPD_WS_POST, IP, HOST, PORT, url, http_content_type_json, strlen(json), send_json);
	if (s==NULL) {
		PRINTF("httpd_ws_request returned NULL\n");
	} else {
		PRINTF(s->outbuf);
	}
}

// Horrid hack to avoid linking error
httpd_ws_script_t httpd_ws_get_script(struct httpd_ws_state *s) {return NULL;}


static struct etimer timer;


  PROCESS(sense_process, "SicsthSense process");
  AUTOSTART_PROCESSES(&sense_process);

  PROCESS_THREAD(sense_process, ev, data) {
  PROCESS_BEGIN();

  etimer_set(&timer, CLOCK_SECOND*20);
  make_url();

  while (1) {
    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));

    process_start(&httpd_ws_process, NULL);

    post_json();
    etimer_reset(&timer);
  }

  PROCESS_END();
}




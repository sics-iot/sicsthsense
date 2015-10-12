/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

/* Description: MQTT client interface
 * TODO:
 * */
package se.sics.sicsthsense.core;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.jdbi.StorageDAO;

public class MQTT {
	private static MQTT singleton=null;

	private final Logger logger = LoggerFactory.getLogger(MQTT.class);
	private StorageDAO storage;
	int qos             = 2;
	String broker       = "tcp://iot.eclipse.org:1883";
	String clientId     = "SicsthSenseEngine";
	MemoryPersistence persistence = new MemoryPersistence();
	MqttClient sampleClient = null;

	public static MQTT getInstance(StorageDAO storage) {
		if (singleton==null) {
			singleton = new MQTT(storage);
			singleton.connect();
		}
		return singleton;
	}

	public MQTT(StorageDAO storage) {
		this.storage = storage;
	}

	public boolean connect() {
		try {
			sampleClient = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			System.out.println("Connecting to broker: "+broker);
			sampleClient.connect(connOpts);
			System.out.println("Connected");
			return true;
		} catch(MqttException me) {
			return false;
		}
	}
	public void disconnect() {
		try {
			sampleClient.disconnect();
			System.out.println("Disconnected");
		} catch(MqttException me) {
			System.out.println("Problem disconnecting!");
		}
	}

	public void subscribeAll() {
		//String[] topics;
		List<String> topics = storage.findSubscriptions();
		//TODO: register subscriptions
	}

	public void publish(String topic, String json) {
	}

}

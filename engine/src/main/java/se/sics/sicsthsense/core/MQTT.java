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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import se.sics.sicsthsense.core.*;
import se.sics.sicsthsense.Utils;
import se.sics.sicsthsense.jdbi.StorageDAO;

public class MQTT implements MqttCallback {
	private static MQTT singleton=null;

	private final Logger logger = LoggerFactory.getLogger(MQTT.class);
	private StorageDAO storage;
	int qos             = 2;
	//String broker       = "tcp://iot.eclipse.org:1883";
	String broker       = "tcp://localhost:1883";
	String clientId     = "SicsthSenseEngine";
	MemoryPersistence persistence = new MemoryPersistence();
	MqttClient client = null;

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
			client = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			client.setCallback(this); // set self as callback handler
			System.out.println("Connecting to broker: "+broker);
			client.connect(connOpts);
			System.out.println("Connected");
			// probably should not do this here, may take some time...
			subscribeAll();
			return true;
		} catch(MqttException me) {
			return false;
		}
	}
	public void disconnect() {
		try {
			client.disconnect();
			System.out.println("Disconnected");
		} catch(MqttException me) {
			System.out.println("Problem disconnecting!");
		}
	}

	public void subscribeAll() {
		//String[] topics;

		System.out.println("Subscribing!!");
		//List<String> topics = storage.findSubscriptionTopics();
		List<String> topics= new ArrayList<String>();
		topics.add("test");
		String[] tmp = topics.toArray(new String[topics.size()]);
		System.out.println(Arrays.toString(tmp));
		try {
			client.subscribe(tmp);
		} catch(MqttException me) {
			System.out.println("Problem subscribing!");
		}
	}

	public void publish(String topic, String json) {
	}

	// add datapoint(s) to a resource/stream
	public void consumeMessage(String topic, Subscription subscription, MqttMessage message) {

		if (subscription.getStreamId()==-1) { // if its for a resource
			Resource resource = storage.findResourceById(subscription.getResourceId());
			Utils.applyParsers(storage, resource, message.toString());
		} else { // else its for a stream
			DataPoint datapoint;
			try {
				Utils.insertDataPoint(storage, datapoint);
			} catch (Exception e) {}
		}
	}


	//MqttCallback interface methods connectionList() deliveryComplete() messageArrived()

	// Callback upon recipt of an MQTT message
	public void messageArrived(String topic, MqttMessage message) {
		System.out.println(topic+" Message Arrived!"+message);
		List<Subscription> subs = storage.findSubscriptions(topic);
		for (Iterator<Subscription> i=subs.iterator(); i.hasNext(); ) {
			consumeMessage(topic, i.next(), message);
		}
	}

	public void connectionLost(Throwable cause) {
		System.out.println("Connection Lost!");
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("Delivery complete!");
	}


}

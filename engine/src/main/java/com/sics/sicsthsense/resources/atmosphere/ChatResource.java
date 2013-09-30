package com.sics.sicsthsense.resources.atmosphere;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.annotation.Subscribe;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.jersey.Broadcastable;
import org.atmosphere.jersey.SuspendResponse;

//@Path("/")
@Path("/users/{userId}/resources/{resourceId}/ws")
public class ChatResource {

    private
    @PathParam("resourceId") Broadcaster topic;

    @GET
    @Suspend(contentType = MediaType.APPLICATION_JSON)
    public SuspendResponse<String> subscribe() {
        return new SuspendResponse.SuspendResponseBuilder<String>()
                .broadcaster(topic)
                .outputComments(true)
//                .addListener(new EventsLogger())
                .build();
    }

    @POST
    @Broadcast
    @Produces(MediaType.APPLICATION_JSON)
    //@Produces("text/html;charset=ISO-8859-1")
    //public Broadcastable publish(@FormParam("message") String message) {
    public Broadcastable publish() {
				System.out.println("publish");
        return new Broadcastable("message", "", topic);
    }	

	/*
    @Suspend(contentType = MediaType.APPLICATION_JSON)
    @GET
    public Broadcastable suspend(@PathParam("resourceId") long resourceId, @PathParam("topic") Broadcaster topic, @Context Request request, @Context AtmosphereResource atres) {
        //return "listening on resource: "+resourceId;
        System.out.println("listening on resource: "+resourceId);
				atres.setBroadcaster(topic);
        return new Broadcastable("",topic);
    }
    @Broadcast(writeEntity = false)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response broadcast(@PathParam("resourceId") long resourceId, Message message) {
			System.out.println("Got a broadcast message! on resourceId: "+resourceId);
      return new Response(message.author, message.message);
    }*/

	/*
		@Suspend
    @GET
    @Path("/{topic}")
    @Produces("text/plain;charset=ISO-8859-1")
    public Broadcastable subscribe(@PathParam("topic") Broadcaster topic) {
        return new Broadcastable("",topic);
    }

		@GET
    @Path("/{topic}/{message}")
    @Produces("text/plain;charset=ISO-8859-1")
    @Broadcast
    public Broadcastable publish(@PathParam("topic") Broadcaster topic,
                                 @PathParam("message") String message){

        return new Broadcastable(message,topic);
    }
	*/
}

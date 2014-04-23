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

/* Description:
 * TODO:
 * */

package controllers;

import java.util.List;

import models.DataPoint;
import models.FileSystem;
import models.Function;
import models.Stream;
import models.User;
import models.Vfile;

//import org.codehaus.jackson.node.ArrayNode;
//import org.codehaus.jackson.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class CtrlFunction extends Controller {

	static private Form<Function> functionForm = Form.form(Function.class);

	@Security.Authenticated(Secured.class)
	public static Result attach() {		
		Form<Function> theForm = functionForm.bindFromRequest();
		if(theForm.hasErrors()) {
		  return badRequest("Bad request");
		} else {
			Function operator = theForm.get();
			User currentUser = Secured.getCurrentUser();
			Function.create(currentUser,theForm.field("output").value(),theForm.field("inputStream1").value(),theForm.field("inputStream2").value());
		  return redirect(routes.CtrlResource.resources(0));
		}
	}


	@Security.Authenticated(Secured.class)      
  public static Result edit() {
    return TODO; //ok(accountPage.render(getUser(), userForm));
  }
	
	@Security.Authenticated(Secured.class)
	public static Result modify(Long id) {
		/*
		 * TODO: Create resource from Form or update existing Create a parser from an
		 * embedded form and associate the parser with the new resource
		 */
		Form<Function> theForm = functionForm.bindFromRequest();
		if (theForm.hasErrors()) {
			return badRequest("Bad request");
		} else {
			User currentUser = Secured.getCurrentUser();
			Function submitted = theForm.get();
			try {
				//Resource.get(id, currentUser).updateResource(submitted);
			} catch (Exception e) {
				return badRequest("Bad request");
			}
		  return redirect(routes.CtrlResource.resources(0));
		}    
  }
	

	@Security.Authenticated(Secured.class)
	public static Result getById(Long id) {
		User currentUser = Secured.getCurrentUser();
		//Operator operator = Operator.get(id, currentUser);
		return TODO;
	}

	public static Result getData(String ownerName, String path, Long tail,
			Long last, Long since) {
		final User user = Secured.getCurrentUser();
		final User owner = User.getByUserName(ownerName);
		// if(user == null) return notFound();
		return getData(user, owner, path, tail, last, since);
	}

	public static Result getDataById(Long id, Long tail, Long last, Long since) {
		final User user = Secured.getCurrentUser();
		// if(user == null) return notFound();
		Stream stream = Stream.get(id);
		if (stream == null) {
			return notFound();
		}
		return getData(user, stream, tail, last, since);
	}

	public static Result getDataByUserKey(String user_token, String path,
			Long tail, Long last, Long since) {
		final User user = Secured.getCurrentUser();
		final User owner = User.getByToken(user_token);
		// if(user == null) return notFound();
		return getData(user, owner, path, tail, last, since);
	}

	// @Security.Authenticated(Secured.class)
	private static Result getData(User currentUser, User owner, String path,
			Long tail, Long last, Long since) {
		Vfile f = FileSystem.readFile(owner, path);
		if (f == null) {
			return notFound();
		}
		Stream stream = f.getLink();
		if (stream == null) {
			return notFound();
		}
		return getData(currentUser, stream, tail, last, since);

	}

	private static Result getData(User currentUser, Stream stream, Long tail,
			Long last, Long since) {
		if (stream == null) {
			return notFound();
		}

		if (!stream.canRead(currentUser))
			return unauthorized("Private stream!");

		List<? extends DataPoint> dataSet = null;
		if (tail < 0 && last < 0 && since < 0) {
			tail = 1L;
		}
		if (tail >= 0) {
			dataSet = stream.getDataPointsTail(tail);
		} else if (last >= 0) {
			dataSet = stream.getDataPointsLast(last);
		} else if (since >= 0) {
			dataSet = stream.getDataPointsSince(since);
		} else {
			throw new RuntimeException("This cannot happen!");
		}

		ObjectNode result = Json.newObject();
		ArrayNode time = result.putArray("time");
		ArrayNode data = result.putArray("data");

		for (DataPoint dataPoint : dataSet) {
			time.add(dataPoint.timestamp);
			if (stream.getType() == Stream.StreamType.DOUBLE) {
				data.add((Double) dataPoint.getData());
			}
		}

		return ok(result);
	}
}

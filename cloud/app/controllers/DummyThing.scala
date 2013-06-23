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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/* Description:
 * TODO:
 * */

package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._
import java.util.Random

object DummyThing extends Controller {

  val rand = new Random(System.currentTimeMillis())

  def discover(id: Long) = Action {
    val jsonObject = Json.toJson(
            Map(
              "uid" -> Json.toJson("SicsthSense_Dummy_" + id),
              "streams" -> Json.toJson(Seq(
                    Json.toJson("/sensors"),
                    Json.toJson("/sensors/temperature"),
                    Json.toJson("/sensors/energy"),
                    Json.toJson("/actuators/print")
                  )
               )
            )
      )
     Ok(jsonObject)
  }

  def currTemperature = 42 + rand.nextInt() % 20
  def currEnergy = 42 + rand.nextInt() % 20

  def temperature(id: Long) = Action {
    Ok(currTemperature.toString)
  }

  def energy(id: Long) = Action {
    Ok(currEnergy.toString)
  }

  def sensors(id: Long) = Action {
    Ok(currEnergy.toString)
    val jsonObject = Json.toJson(
            Map("temperature" ->  Json.toJson(currTemperature),
                "energy" ->  Json.toJson(currEnergy))
      )
     Ok(jsonObject)
  }

  def print(id: Long) = Action { request =>
    val str = "DummyThing" + id + " prints: " + request.body.asText.getOrElse("[no body]");
    Ok(str)
  }

}

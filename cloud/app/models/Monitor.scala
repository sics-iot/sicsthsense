package models

import play.api._
import play.api.libs.ws._
import play.api.libs.concurrent._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import akka.actor.Actor
import scala.compat.Platform

class Monitor(var id: Long, var resourceId: Long, var period: Long, var lastUpdate: Long) {
            
  /*** Basic SQL operation on Monitor instances ***/
  
  /* Save object to database */
  def insert(): Long = try {
    DB.withConnection { implicit c => 
      SQL("insert into monitor (resourceId, period, lastUpdate) values ({resourceId}, {period}, {lastUpdate})")
        .on('resourceId -> resourceId, 'period -> period, 'lastUpdate -> lastUpdate)
        .executeUpdate()
      SQL("select scope_identity()")().collect { case Row(id: Long) => id }.head
    }
  } catch { case e => 0 }

  /* Update object fields in database */
  def update(): Boolean = {
    DB.withConnection { implicit c =>
      SQL("update monitor set resourceId = {resourceId}, period = {period}, lastUpdate = {lastUpdate} where id = {id}")
        .on('id -> id, 'resourceId -> resourceId, 'period -> period, 'lastUpdate -> lastUpdate)
        .executeUpdate() == 1
    }
  }

  /* Delete object from database */
  def delete(): Boolean = {
    DB.withConnection { implicit c =>
    SQL("delete from monitor where id = {id}")
      .on('id -> id )
      .executeUpdate() == 1
    }  
  }
  
  /*** End of SQL operations ***/
  
  def periodic() = {
    val current = Monitor.currentTime
    Logger.info("Periodic " + id + " " + resourceId + " " + period + " " + (current - lastUpdate))
    if (current >= lastUpdate + period) {
      val resource = Resource.getById(resourceId)
      val thing = Thing.getById(resource.thingId)
      Logger.info("Now sampling " + thing.name + resource.path)
      WS.url(thing.url + resource.path).get().map { response =>
        Logger.info("New sample for " + thing.name + resource.path + ": " + response.body)
        Sample.create(resourceId, current, response.body.toInt)
      }
      lastUpdate = current
      update()
    }
  }
  
  def resource = Resource.getById(resourceId)
  
}

object Monitor {
  
  /*** Basic SQL operation on the Monitor class ***/
  
  val monitorParser = { long("id") ~ long("resourceId") ~ long("period") ~ long("lastUpdate") map {
      case id ~ resourceId ~ period ~ lastUpdate => new Monitor(id, resourceId, period, lastUpdate)
    }
  }
  
  /* Get a Monitor from its id */
  def getById(id: Long): Monitor = try {
      DB.withConnection { implicit c =>
      SQL("select * from monitor where id = {id}").on('id -> id).as(monitorParser *).head
    }
  } catch { case e => null }
  
  /* Get a Monitor by resource */
  def getByResourceId(resourceId: Long): Monitor = try {
    DB.withConnection { implicit c =>
    SQL("select * from monitor where resourceId = {resourceId}").on('resourceId -> resourceId).as(monitorParser *).head
    }
  } catch { case e => null }
  
  def deleteByResourceId(resourceId: Long) {
    val monitor = getByResourceId(resourceId)
    if (monitor != null) monitor.delete()
  }
  
  /* Get all things */
  def all(): List[Monitor] = DB.withConnection { implicit c =>
    SQL("select * from monitor").
    as(monitorParser *)
  }
  
  /*** End of SQL operations ***/
  
    /* Create a new Monitor */
  def create(resourceId: Long, period: Long): Boolean = {
    val curr = getByResourceId(resourceId)
    if (curr != null)     { curr.period = period; curr.update(); } /* update */
    else                  new Monitor(-1, resourceId, period, Platform.currentTime / (1000 * 60)).insert() != 0 /* create */
  }

  /* Delete a Monitor by id */
  def delete(id: Long): Boolean = try {
   getById(id).delete()
  } catch { case e => false }
  
  def currentTime = Platform.currentTime / (1000 * 60)
  
}

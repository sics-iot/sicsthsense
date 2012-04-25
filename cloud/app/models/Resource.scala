package models

import play.api.libs.ws._
import play.api.libs.concurrent._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

class Resource(var id: Long, var thingId: Long, var path: String) {
    
  /*** Basic SQL operation on Resource instances ***/
  
  /* Save object to database */
  def insert(): Long = try {
    DB.withConnection { implicit c =>
    SQL("insert into resource (thingId, path) values ({thingId}, {path} )")
    .on('thingId -> thingId, 'path -> path)
    .executeUpdate()
    SQL("select scope_identity()")().collect { case Row(id: Long) => id }.head
    }
  } catch { case e => 0 }

  /* Delete object from database */
  def delete(): Boolean = {
    DB.withConnection { implicit c =>
    SQL("delete from resource where id = {id}")
      .on('id -> id )
      .executeUpdate() == 1
    }
  }
  
  /*** End of SQL operations ***/
  
  def monitor = Monitor.getByResourceId(id)
  def thing = Thing.getById(thingId)
}

object Resource {
  
  /*** Basic SQL operation on the Resource class ***/
  
  /* Parser instanciating a Resource from a DB response row */
  val resourceParser = { long("id") ~ long("thingId") ~ str("path") map {
      case id ~ thingId ~ path => new Resource(id, thingId, path)
    }
  }
   
  /* Get a Resource from its id */
  def getById(id: Long): Resource = try {
    DB.withConnection { implicit c =>
      SQL("select * from resource where id = {id}").on('id -> id).as(resourceParser *).head
    }
  } catch { case e => null }
   
  def getByThingId(thingId: Long): List[Resource] = DB.withConnection { implicit c =>
    SQL("select * from resource where thingId = {thingId}").on('thingId -> thingId).as(resourceParser *)
  }
  
  def deleteByThingId(thingId: Long) {
    SQL("delete from resource where thingId = {thingId}").on('thingId -> thingId)
  }
  
  /*** End of SQL operations ***/
 
  def register(thingId: Long, paths: Seq[String]) {
   paths.map((path) => new Resource(-1, thingId, path).insert())
  }
  
  /* Delete a Resource by id */
  def delete(id: Long) {
   getById(id).delete()
   Monitor.deleteByResourceId(id)
   Sample.deleteByResourceId(id)
  }
  
}

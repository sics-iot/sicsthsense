package models

import play.api.libs.ws._
import play.api.libs.concurrent._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

class Resource(var id: Long, var thingId: Long, var path: String) {
    
  /*** SQL helpers ***/
  
  def SQLon(query: String) = SQL(query).on('id -> id, 'thingId -> thingId, 'path -> path )
  
  /*** End of SQL helpers ***/
  
  /* Save object to database */
  def insert(): Long = DB.withConnection { implicit c =>
    SQLon("insert into resource (thingId, path) values ({thingId}, {path} )").execute()
    SQLon("select scope_identity()")().collect { case Row(id: Long) => id }.headOption.getOrElse(0)
  }

  /* Delete object from database */
  def delete(): Boolean = DB.withConnection { implicit c =>
    SQLon("delete from resource where id = {id}").execute()
  }
  
  def monitor = Monitor.getByResourceId(id)
  def thing = Thing.getById(thingId)
}

object Resource {
  
  /*** SQL helpers ***/

  def SQLas(query: String) = DB.withConnection { implicit c => 
    SQL(query).as(resourceParser *)
  }

  /* Parser instanciating a Resource from a DB response row */
  val resourceParser = { long("id") ~ long("thingId") ~ str("path") map {
      case id ~ thingId ~ path => new Resource(id, thingId, path)
    }
  }  
  
  /*** End of SQL helpers ***/
   
  /* Get a Resource from its id */
  def getById(id: Long): Resource = SQLas("select * from resource where id = " + id)
      .headOption.getOrElse(null)
   
  def getByThingId(thingId: Long): List[Resource] = SQLas("select * from resource where thingId = " + thingId)
     
  def register(thingId: Long, paths: Seq[String]) {
   paths.map((path) => new Resource(-1, thingId, path).insert())
  }
  
  /* Delete a Resource by id */
  def delete(id: Long) {
   getById(id).delete()
  }
  
}

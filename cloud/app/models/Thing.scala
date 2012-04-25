package models

import play.api.libs.ws._
import play.api.libs.concurrent._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

class Thing(var id: Long, var url: String, var uid: String, var name: String) {
  
  /*** Basic SQL operation on Thing instances ***/
  
  /* Save object to database */
  def insert(): Long = try {
    DB.withConnection { implicit c =>
      SQL("insert into thing (url, uid, name) values ({url}, {uid}, {name})")
        .on('url -> url, 'uid -> uid, 'name -> name )
        .executeUpdate()
      SQL("select scope_identity()")().collect { case Row(id: Long) => id }.head
    }
  } catch { case e => 0 }
  
  /* Update object fields in database */
  def update(): Boolean = {
    DB.withConnection { implicit c =>
      SQL("update thing set url = {url}, uid = {uid}, name = {name} where id = {id}")
        .on('id -> id, 'url -> url, 'uid -> uid, 'name -> name)
        .executeUpdate() == 1
    }
  }

  /* Delete object from database */
  def delete(): Boolean = {
    DB.withConnection { implicit c =>
    SQL("delete from thing where id = {id}")
      .on('id -> id )
      .executeUpdate() == 1
    }  
  }
  
  /*** End of SQL operations ***/
  
  def resources = Resource.getByThingId(id)
  
}

object Thing {
  
  /*** Basic SQL operation on the Thing class ***/
  
  /* Parser instanciating a Thing from a DB response row */
  val thingParser = { long("id") ~ str("url") ~ str("uid") ~ str("name") map {
      case id ~ url ~ uid ~ name => new Thing(id, url, uid, name)
    }
  }
  
  /* Get a Thing from its id */
  def getById(id: Long): Thing = try {
    DB.withConnection { implicit c =>
      SQL("select * from thing where id = {id}").on('id -> id).as(thingParser *).head
        }
  } catch { case e => null }
  
  /* Get a Thing from its URL */
  def getByUrl(id: Long): Thing = try { 
      DB.withConnection { implicit c =>
      SQL("select * from thing where id = {id}").on('id -> id).as(thingParser *).head
    }
  } catch { case e => null }
    
  /* Get all things */
  def all(): List[Thing] = DB.withConnection { implicit c =>
    SQL("select * from thing").
    as(thingParser *)
  }
  
  /*** End of SQL operations ***/

  /* Register a new Thing from its URL */
  def register(url: String): Long = {
    new Thing(-1, url, "no uid", url).insert()
  }
  
  /* Discover a Thing */
  def discover(id: Long): Promise[Boolean] = {
    val thing = getById(id)
    if (thing == null) Akka.future { false }
    
    /* Get /discover to get a uid of the Thing being registered */
    WS.url(thing.url + "/discover").get().map { response =>
      /* Get string uid */
      thing.uid = (response.json \ "uid")
        .asOpt[String].getOrElse("nodesc")
      /* Update Thing in database */
      thing.update()
      /* Get resources set */
      val paths = (response.json \ "resources")
        .asOpt[Seq[String]].getOrElse(Seq[String]())
      /* Save resources to database */
      Resource.register(thing.id, paths)
      true
    }
  }
  
  /* Set Thing name */
  def setName(id: Long, name: String): Boolean = {
   val thing = getById(id)
   if(thing == null) false
   else {
     thing.name = name
     println(name)
     thing.update()
     true
   }
  }
  
  /* Delete a Thing by id */
  def delete(id: Long) {
   getById(id).delete()
   Resource.deleteByThingId(id)
  }
    
}

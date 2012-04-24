package models

import play.api.libs.ws._
import play.api.libs.concurrent._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

class Thing(var id: Long, var url: String, var description: String, var label: String) {
    
  /*** Basic SQL operation on Thing instances ***/
  
  /* Save object to database */
  def save(): Long = {
    DB.withConnection { implicit c =>
      if (SQL("insert into thing (url, description, label) values ({url}, {description}, {label})")
        .on('url -> url, 'description -> description, 'label -> label )
        .executeUpdate() == 1)
          SQL("select scope_identity()")().collect { case Row(id: Long) => id }.head
      else 0
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
  val thingParser = { long("id") ~ str("url") ~ str("description") ~ str("label") map {
      case id ~ url ~ description ~ label => new Thing(id, url, description, label)
    }
  }
  
  /* Get a Thing from its id */
  def getById(id: Long): Thing = {
    val list = DB.withConnection { implicit c =>
      SQL("select * from thing where id = {id}").on('id -> id).as(thingParser *)
    }
    if (list.length == 1) list(0)
    else null
  }
  
  /* Get a Thing from its URL */
  def getByUrl(id: Long): Thing = DB.withConnection { implicit c =>
    SQL("select * from thing where id = {id}").on('id -> id).as(thingParser *).head
  }
  
  /* Get all things */
  def all(): List[Thing] = DB.withConnection { implicit c =>
    SQL("select * from thing").
    as(thingParser *)
  }
  
  /*** End of SQL operations ***/

  /* Register a new Thing from the URL of its service description */
  def register(url: String): Promise[Boolean] = {
    /* Get /discover to get a description of the Thing being registered */
    WS.url(url + "/discover").get().map { response =>
      /* Get string description */
      val description = (response.json \ "description")
        .asOpt[String].getOrElse("nodesc")
      /* Get resources set */
      val resources = (response.json \ "resources")
        .asOpt[Seq[String]].getOrElse(Seq[String]())
      /* Save Thing to database */
      val id = new Thing(-1, url, description, description).save()
      println(id)
      if (id != 0)
        Resource.register(id, resources)
      id != 0
    }  
  }
  
  /* Remove a Thing by id */
  def remove(id: Long): Boolean = {
   if (getById(id).delete()) true
   else false
  }
    
}

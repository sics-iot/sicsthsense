package models

import play.api.libs.ws._
import play.api.libs.concurrent._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

class Thing(var id: Long, var url: String, var uid: String, var name: String) {
  
  /*** SQL helpers ***/
  
  def SQLon(query: String) = SQL(query).on('id -> id, 'url -> url, 'uid -> uid, 'name -> name )
  
  /*** End of SQL helpers ***/
  
  /* Save object to database */
  def insert(): Long = DB.withConnection { implicit c =>
    /* Insert */
    SQLon("insert into thing (url, uid, name) values ({url}, {uid}, {name})").execute()
    /* Return id */
    SQLon("select scope_identity()")().collect { case Row(id: Long) => id }
      .headOption.getOrElse(0)
  }
  
  /* Update object fields in database */
  def update(): Boolean = DB.withConnection { implicit c =>
    SQLon("update thing set url = {url}, uid = {uid}, name = {name} where id = {id}").execute()
  }

  /* Delete object from database */
  def delete(): Boolean = DB.withConnection { implicit c =>
    SQLon("delete from thing where id = {id}").execute()
  }
  
  def resources = Resource.getByThingId(id)
  def monitors: List[Monitor] = Resource.getByThingId(id).map( r =>
        Monitor.getByResourceId(r.id)
      ).filter( m =>
        m != null
      )
}

object Thing {
  
  /*** SQL helpers ***/
  
  def SQLas(query: String) = DB.withConnection { implicit c => 
    SQL(query).as(thingParser *)
  }
  
  /* Parser instanciating a Thing from a DB response row */
  val thingParser = { long("id") ~ str("url") ~ str("uid") ~ str("name") map {
      case id ~ url ~ uid ~ name => new Thing(id, url, uid, name)
    }
  }
  
  /*** End of SQL helpers ***/
  
  /* Get a Thing from its id */
  def getById(id: Long): Thing = SQLas("select * from thing where id = " + id)
    .headOption.getOrElse(null)
  
  /* Get a Thing from its URL */
  def getByUrl(url: String): Thing = SQLas("select * from thing where url = " + url)
    .headOption.getOrElse(null)
    
  /* Get all things */
  def all(): List[Thing] = SQLas("select * from thing")
  
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
     thing.update()
     true
   }
  }
  
  /* Delete a Thing by id */
  def delete(id: Long) {
   getById(id).delete()
  }
    
}

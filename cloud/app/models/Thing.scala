package models

import play.api.libs.ws._
import play.api.libs.concurrent._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

class Thing(var id: Long, var url: String, var description: String, var label: String, var resources: String) {
    
  def save(): Boolean = {
    DB.withConnection { implicit c =>
      SQL("insert into thing (url, description, label, resources) values ({url}, {description}, {label}, {resources})")
        .on('url -> url, 'description -> description, 'label -> label, 'resources-> resources )
        .executeUpdate() == 1
    }
  }

  def delete(): Boolean = {
    DB.withConnection { implicit c =>
    SQL("delete from thing where id = {id}")
      .on('id -> id )
      .executeUpdate() == 1
    }
  }
  
}

object Thing {
  
  val thingParser = { long("id") ~ str("url") ~ str("description") ~ str("label") ~ str("resources") map {
      case id ~ url ~ description ~ label ~ resources => new Thing(id, url, description, label, resources)
    }
  }
  
  def get(id: Long): Thing = DB.withConnection { implicit c =>
    SQL("select * from thing where id = {id}")
      .on('id -> id)
      .as(thingParser *)
      .head
  }
  
  def all(): List[Thing] = DB.withConnection { implicit c =>
    SQL("select * from thing").
    as(thingParser *)
  }

  def register(url: String): Promise[Boolean] = {
    WS.url(url + "/discover").get().map { response =>
      val description = (response.json \ "description").asOpt[String].getOrElse("nodesc")
      println(description)
      val resources = (response.json \ "resources").asOpt[Seq[String]].getOrElse(Seq[String]()).reduce((acc, curr) => acc + "\n" + curr)
      if (new Thing(-1, url, description, description, resources).save()) true
      else false
    }  
  }
  
  def remove(id: Long): Boolean = {
   if (Thing.get(id).delete()) true
   else false
  }
    
}

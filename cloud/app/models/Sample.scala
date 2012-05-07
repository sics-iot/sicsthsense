package models

import play.api.libs.ws._
import play.api.libs.concurrent._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

class Sample(var resourceId: Long, var timestamp: Long, var value: Double) {
    
  /*** SQL helpers ***/
  
  def SQLon(query: String) = SQL(query).on('resourceId -> resourceId, 'timestamp -> timestamp, 'value -> value )
  
  /*** End of SQL helpers ***/
  
  /* Save object to database */
  def insert(): Long = DB.withConnection { implicit c =>
    SQLon("insert into sample (resourceId, timestamp, value) values ({resourceId}, {timestamp}, {value} )").execute()
    SQL("select scope_identity()")().collect { case Row(id: Long) => id }.headOption.getOrElse(0)
  }

  /* Delete object from database */
  def delete(): Boolean = DB.withConnection { implicit c =>
    SQLon("delete from resource where resourceId = {resourceId} and timestamp = {timestamp}").execute()
  }
  
}

object Sample {
  
  /*** SQL helpers ***/

  def SQLas(query: String) = DB.withConnection { implicit c => 
    SQL(query).as(sampleParser *)
  }

  /* Parser instanciating a Sample from a DB response row */
  val sampleParser = { long("resourceId") ~ long("timestamp") ~ get[Double]("value") map {
      case resourceId ~ timestamp ~ value => new Sample(resourceId, timestamp, value)
    }
  }  
  
  /*** End of SQL helpers ***/
    
  def getByResourceId(resourceId: Long): List[Sample] = SQLas("select * from sample where resourceId = " + resourceId) 
     
  def create(resourceId: Long, timestamp: Long, value: Double) {
   val s = new Sample(resourceId, timestamp, value).insert()
   println("create sample " + s + " " + resourceId + " " + timestamp + " " + value)
  }
  
}

package models

import play.api.libs.ws._
import play.api.libs.concurrent._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

class Sample(var resourceId: Long, var timestamp: Long, var value: Double) {
    
  /*** Basic SQL operation on Sample instances ***/
  
  /* Save object to database */
  def insert(): Long = try {
    DB.withConnection { implicit c =>
    SQL("insert into sample (resourceId, timestamp, value) values ({resourceId}, {timestamp}, {value} )")
    .on('resourceId -> resourceId, 'timestamp -> timestamp, 'value -> value)
    .executeUpdate()
    SQL("select scope_identity()")().collect { case Row(id: Long) => id }.head
    }
  } catch { case e => 0 }

  /* Delete object from database */
  def delete(): Boolean = {
    DB.withConnection { implicit c =>
    SQL("delete from resource where resourceId = {resourceId} and timestamp = {timestamp}")
      .on('resourceId -> resourceId, 'timestamp -> timestamp )
      .executeUpdate() == 1
    }
  }
  
  /*** End of SQL operations ***/
}

object Sample {
  
  /*** Basic SQL operation on the Sample class ***/
  
  /* Parser instanciating a Resource from a DB response row */
  val sampleParser = { long("resourceId") ~ long("timestamp") ~ get[Double]("value") map {
      case resourceId ~ timestamp ~ value => new Sample(resourceId, timestamp, value)
    }
  }
  
  def getByResourceId(resourceId: Long): List[Sample] = DB.withConnection { implicit c =>
    {
     SQL("select * from sample where resourceId = {resourceId}").on('resourceId -> resourceId).as(sampleParser *) 
    }
  }
  
  def deleteByResourceId(resourceId: Long) {
    getByResourceId(resourceId).map( sample => sample.delete())
    SQL("delete from sample where resourceId = {resourceId}").on('resourceId -> resourceId)
  }
    
  /*** End of SQL operations ***/
 
  def create(resourceId: Long, timestamp: Long, value: Double) {
   val s = new Sample(resourceId, timestamp, value).insert()
   println("create sample " + s + " " + resourceId + " " + timestamp + " " + value)
  }
  
}

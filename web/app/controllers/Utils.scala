package controllers

import scala.Array.canBuildFrom
import scala.collection.JavaConversions.mapAsJavaMap

import play.core.parsers.FormUrlEncodedParser

object ScalaUtils {
  def parseQueryString(queryString: String): java.util.Map[String, Array[String]] = {
    val map = new java.util.HashMap[String, Array[String]]
    
    if (queryString == null || queryString == "")
      return map;
    
    val qs = queryString.split('?').last

    for ((key, values) <- FormUrlEncodedParser.parse(qs)) {
      map.put(key, values.toArray)
    }

    map
  }
}

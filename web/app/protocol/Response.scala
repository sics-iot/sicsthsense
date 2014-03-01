package protocol

import java.net.URI

trait Response {
  def uri: URI
  
  def status: Int
  def statusText: String
  
  def header(key: String): String
  def headers: Map[String, Array[String]]

  def contentType: String
  def contentLength: Long
  def contentEncoding: String
  
  def body: String
}

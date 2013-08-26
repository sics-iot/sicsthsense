/*
 * Copyright (c) 2013, Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * Authors:
 *  26/08/2013 Adrian Kündig (adkuendi@ethz.ch)
 */

package protocol.coap

import java.nio.ByteBuffer

import scala.Array.canBuildFrom
import scala.Option.option2Iterable
import scala.io.Codec
import scala.util.Try

import org.apache.http.entity.ContentType
import org.apache.http.impl.EnglishReasonPhraseCatalog

import ch.ethz.inf.vs.californium.coap.{Message => CoapMessage}
import ch.ethz.inf.vs.californium.coap.{Option => CoapOption}
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry
import play.api.{Logger, Configuration, Play}

object CoapTranslator {
  private val logger = Logger(this.getClass())

  private val KEY_COAP_CODE: String = "coap.response.code."
  private val KEY_COAP_OPTION: String = "coap.message.option."
  private val KEY_COAP_MEDIA: String = "coap.message.media."

  private val KEY_HTTP_CODE: String = "http.response.code."
  private val KEY_HTTP_METHOD: String = "http.request.method."
  private val KEY_HTTP_HEADER: String = "http.message.header."
  private val KEY_HTTP_CONTENT_TYPE: String = "http.message.content-type."

  private val configuration: Configuration = Play.current.configuration

  def getCoapStatusCode(httpStatusCode: Int): Option[Int] =
    configuration.getInt(KEY_HTTP_CODE + httpStatusCode)

  def getCoapStatusText(httpStatusCode: Int): Option[String] =
    getCoapStatusCode(httpStatusCode).map(CodeRegistry.toString(_))

  def getCoapOptions(httpHeaders: java.util.Map[String, Array[String]]): Seq[CoapOption] =
    getCoapOptions(scala.collection.JavaConversions.mapAsScalaMap(httpHeaders))

  def getCoapOptions(httpHeaders: Iterable[(String, Array[String])]): Seq[CoapOption] = {
    val options: Iterable[Vector[CoapOption]] = for {
      (key, values) <- httpHeaders

      headerName = key.toLowerCase
      headerValues = values.map(_.toLowerCase)

      // get the mapping from the property file
      optionCodeString <- configuration.getString(KEY_HTTP_HEADER + headerName)

      // ignore the header if not found in the properties file
      if !optionCodeString.isEmpty

      // get the option number
      // ignore the option if not recognized
      optionNumber <- Try {
        optionCodeString.toInt
      }.toOption

      // ignore the content-type because it will be handled within the payload
      if optionNumber != OptionNumberRegistry.CONTENT_TYPE

    } yield optionNumber match {
        case OptionNumberRegistry.ACCEPT =>
          // iterate for each content-type indicated
          val options = for {
            value <- headerValues

            // translate the content-type
            coapContentTypes = if (value.contains("*"))
              MediaTypeRegistry.parseWildcard(value).to[Vector]
            else
              Vector[Integer](MediaTypeRegistry.parse(value))

            // return present a conversions for the content-types
            coapContentType <- coapContentTypes

            if coapContentType != MediaTypeRegistry.UNDEFINED
          } yield {
            val opt = new CoapOption(optionNumber)
            opt.setIntValue(coapContentType)
            opt
          }

          options.to[Vector]
        case OptionNumberRegistry.MAX_AGE =>
          val option = if (headerValues.contains("no-cache")) {
            val opt = new CoapOption(optionNumber)
            opt.setIntValue(0)
            Some(opt)
          } else {
            for {
              headerValue <- headerValues.headOption

              index = headerValue.indexOf('=')

              if index >= 0

              value <- Try {
                headerValue.substring(index + 1).toInt
              }.toOption
            } yield {
              val opt = new CoapOption(optionNumber)
              opt.setIntValue(value)
              opt
            }
          }

          option.to[Vector]
        case _ =>
          val option = for {
            headerValue <- headerValues.headOption

            opt = new CoapOption(optionNumber)

            _ <- Try {
              OptionNumberRegistry.getFormatByNr(optionNumber) match {
                case OptionNumberRegistry.optionFormats.INTEGER => opt.setIntValue(headerValue.toInt);
                case OptionNumberRegistry.optionFormats.OPAQUE => opt.setValue(headerValue.getBytes(Codec.ISO8859.charSet));
                case _ => opt.setStringValue(headerValue);
              }
            }.toOption
          } yield opt

          option.to[Vector]
      }

    options.flatten.to[Vector]
  }

  def getHttpStatusCode(coapStatusCode: Int): Option[Int] =
    configuration.getInt(KEY_COAP_CODE + coapStatusCode)

  def getHttpStatusText(coapStatusCode: Int): Option[String] =
    getHttpStatusCode(coapStatusCode).map(EnglishReasonPhraseCatalog.INSTANCE.getReason(_, null))

  def getHttpHeaders(options: java.lang.Iterable[CoapOption]): Map[String, Array[String]] =
    getHttpHeaders(scala.collection.JavaConversions.iterableAsScalaIterable(options))

  def getHttpHeaders(options: Iterable[CoapOption]): Map[String, Array[String]] = {
    val headers = for {
      option <- options

      optionNumber = option.getOptionNumber

      if optionNumber != OptionNumberRegistry.CONTENT_TYPE
      if optionNumber != OptionNumberRegistry.PROXY_URI

      headerName <- configuration.getString(KEY_COAP_OPTION + optionNumber)

      if !headerName.isEmpty

      stringOptionValue <- OptionNumberRegistry.getFormatByNr(optionNumber) match {
        case OptionNumberRegistry.optionFormats.STRING => Some(option.getStringValue())
        case OptionNumberRegistry.optionFormats.INTEGER => Some(option.getIntValue().toString)
        case OptionNumberRegistry.optionFormats.UNKNOWN => Some(option.getRawValue().toString)
        case _ => None
      }
    } yield {
      if (optionNumber == OptionNumberRegistry.MAX_AGE) {
        (headerName, Array("max-age=" + stringOptionValue))
      } else {
        (headerName, Array(stringOptionValue))
      }
    }

    headers.toMap
  }

  def getContentType(coapMessage: CoapMessage): ContentType = Try {
    // get the coap content-type
    val coapContentType = coapMessage.getContentType();

    if (coapContentType == MediaTypeRegistry.UNDEFINED) {
      ContentType.APPLICATION_OCTET_STREAM
    } else {
      // search for the media type inside the property file
      val coapContentTypeString =
        configuration.
          getString(KEY_COAP_MEDIA + coapContentType)
          .getOrElse {
          val plain = MediaTypeRegistry.toString(coapContentType);

          // if the coap content-type is printable, it is needed to
          // set the default charset (i.e., UTF-8)
          if (MediaTypeRegistry.isPrintable(coapContentType))
            plain + "; charset=UTF-8"
          else
            plain
        }

      ContentType.parse(coapContentTypeString)
    }
  }.getOrElse(ContentType.APPLICATION_OCTET_STREAM)

  def getContent(coapMessage: CoapMessage): String = {
    // check if coap request has a payload
    val payload = coapMessage.getPayload()

    if (payload == null || payload.length == 0)
      return ""

    // get the charset
    val contentType = getContentType(coapMessage)
    val charset = contentType.getCharset()

    if (charset == null)
      return payload.toString

    // if there is a charset, means that the content is not binary

    // according to the class ContentType the default content-type with
    // UTF-8 charset is application/json. If the content-type
    // parsed is different, or is not ison encoded, it is needed a
    // translation
    if (charset.equals(Codec.ISO8859.charSet) || contentType == ContentType.APPLICATION_JSON)
      return payload.toString

    Try(charset.decode(ByteBuffer.wrap(payload)).toString).getOrElse("")
  }
}

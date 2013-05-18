The get data from a Resource the only valid way is to call Resource.request(...). This returns
a Promise<Response> that then contains the general Response object that is the same for all protocols.

Which Protocol implementation will be used to handle the request is then decided by the
Resource.request(...) method. To get the url for a resource for display, only the method
getUrl() should be called. getUrlPath() is an implementation detail.

Following is a short overview of all scala code.

# package - protocol

Base classes, interfaces and models that are used by all protocol implementations.

### Response.scala

Abstraction on the response of a protocol request. Currently contains

- uri (request)
- headers
- statusCode
- statusText
- contentType
- contentLength
- contentEncoding
- body

The statusCode/-Text, header and content type format is the one of HTTP.

### Protocol.scala

Interface that all protocol implementations need to satisfy. Currently only contains one method:

Promise<Response> request(uri, method, headerMap, paramsMap, body)

It abstracts a request in the corresponding protocol. Not all methods and headers are assumed to
be supported by all protocols.

### Translator.scala

Currently one monolythic singleton class that is responsible for mapping different protocol responses.
A future change will most likely be, that every protocol needs to be able to translate its own response
format into the Response class. Contains:

Int getHttpStatusCode(coapResponse)
String getHttpStatusText(coapResponse)
Map<String, String[]> getHttpHeaders(coapOptions)
Int getCoapStatusCode(httpResponse)
String getCoapStatusText(httpResponse)
List<CoapOptions> getCoapOptions(httpHeaders)
String getContentType(coapResponse)
String getContent(coapResponse)


# package - protocol.coap

Implements the COAP protocol.

### CoapProtocol.scala

Implements the protocol.Protocol Interface.

### CoapServer.scala

Will implement the request handling part of COAP for proxying and push.


# package - protocol.http

Implements the HTTP protocol.

### HttpProtocol.scala

Implements the protocol.Protocol Interface.



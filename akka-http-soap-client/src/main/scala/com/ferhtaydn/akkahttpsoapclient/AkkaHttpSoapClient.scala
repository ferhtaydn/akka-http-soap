package com.ferhtaydn.akkahttpsoapclient

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.Uri.Authority
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

@SuppressWarnings(
  Array("org.wartremover.warts.Any")
)
class AkkaHttpSoapClient(
  config: Config
)(implicit system: ActorSystem, mat: ActorMaterializer)
    extends ScalaXmlSupport {

  private[this] val scheme = config.getString("example.soap.scheme")
  private[this] val host = config.getString("example.soap.host")
  private[this] val port = config.getInt("example.soap.port")

  private[this] val SOAP_ENVELOPE_URI =
    "http://www.w3.org/2003/05/soap-envelope"

  implicit val executionContext: ExecutionContext = system.dispatcher

  private[this] val uri = Uri(
    scheme = scheme,
    authority = Authority(
      host = Uri.Host(host),
      port = port
    )
  )

  def request(
    method: HttpMethod,
    path: Uri.Path,
    action: String,
    entity: Option[NodeSeq]
  ): Future[HttpRequest] = {

    val requestEntityF = entity match {
      case None => Future.successful(HttpEntity.Empty)
      case Some(xml) => Marshal(envelope(xml)).to[RequestEntity]
    }

    requestEntityF.map { requestEntity =>
      HttpRequest(
        method = method,
        uri = uri.withPath(path),
        entity = requestEntity
          .withContentType(
            soap12ContentType(action)
          )
      )
    }
  }

  def sendAndReceive[T](
    request: HttpRequest,
    f: HttpResponse => Future[T]
  ): Future[T] =
    Http()
      .singleRequest(request)
      .flatMap(f)

  def responseBodyXml(response: HttpResponse): Future[NodeSeq] = {

    import soapenvelope12.Envelope
    import scalaxb.DataRecord

    Unmarshal(
      response.entity
        .withContentType(ContentTypes.`text/xml(UTF-8)`)
    ).to[NodeSeq].map { responseXml =>
      val envelope = scalaxb.fromXML[Envelope](responseXml)

      envelope.Body.any.headOption match {
        case Some(DataRecord(_, _, x: scala.xml.Elem))
            if (x.label == "Fault") &&
              (x.scope.getURI(x.prefix) == SOAP_ENVELOPE_URI) =>
          // TODO: much advance failure handling
          // val fault = scalaxb.fromXML[soapenvelope12.Fault](x)
          Nil
        case _ =>
          envelope.Body.any.collect {
            case DataRecord(_, _, x: scala.xml.Node) => x
          }
      }
    }
  }

  private def envelope(xml: NodeSeq): NodeSeq = {

    import com.examples._
    import soapenvelope12.{Body, Envelope}

    import scalaxb.DataRecord

    val bodyRecords = xml.map(DataRecord(None, None, _))
    val envelope = Envelope(
      Header = None,
      Body = Body(bodyRecords, attributes = Map()),
      attributes = Map()
    )

    val scope = scalaxb.toScope(
      (
        (Some("soap12") -> SOAP_ENVELOPE_URI) ::
          scalaxb.fromScope(defaultScope)
      ).distinct: _*
    )

    scalaxb.toXML(
      envelope,
      Some(SOAP_ENVELOPE_URI),
      "Envelope",
      scope
    )

  }

  /**
    * Creates Content-Type of
    * `application/soap+xml; charset=utf-8; action=$targetAction`
    * for Soap v1.2 from given target action
    * @param targetAction Target action
    * @return Content type with target action
    */
  private def soap12ContentType(targetAction: String) = ContentType(
    MediaType.customWithFixedCharset(
      "application",
      "soap+xml",
      HttpCharsets.`UTF-8`,
      params = Map("action" -> targetAction)
    )
  )
}

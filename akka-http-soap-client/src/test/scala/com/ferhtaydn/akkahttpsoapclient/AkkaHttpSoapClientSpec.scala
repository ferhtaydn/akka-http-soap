package com.ferhtaydn.akkahttpsoapclient

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import akka.http.scaladsl.model.{HttpMethods, Uri}
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import com.typesafe.config.ConfigFactory
import org.scalatest.{AsyncFreeSpec, BeforeAndAfterAll, Matchers}
import scala.concurrent.{ExecutionContext, Future}

class AkkaHttpSoapClientSpec
    extends AsyncFreeSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalaXmlSupport {

  private[this] val config = ConfigFactory.load

  private[this] val path = config.getString(
    "example.soap.path"
  )

  private[this] val action = config.getString(
    "example.soap.action"
  )

  implicit val system: ActorSystem =
    ActorSystem("periodic-table-client-spec", config)
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
    super.afterAll()
  }

  private[this] val client = new AkkaHttpSoapClient(config)

  "Spec" - {

    "should answer to `soap call`" in {

      import com.examples._

      object testFormat extends DefaultComexamples_JaxWsTest1Format

      val entity = scalaxb.toXML(
        com.examples.JaxWsTest1("information", 1),
        testFormat.targetNamespace,
        testFormat.typeName,
        defaultScope
      )

      val mockResponse = scalaxb.toXML(
        com.examples.JaxWsTest1Response("MOCK"),
        testFormat.targetNamespace,
        testFormat.typeName,
        defaultScope
      )

      val responseF = for {
        request <- client.request(
          HttpMethods.POST,
          Uri.Path(path),
          action,
          Some(entity)
        )
        bodyXmlF <- client
          .sendAndReceive(
            request,
            // client.responseBodyXml
            _ => Future.successful(mockResponse)
          )
      } yield bodyXmlF

      responseF.flatMap { bodyXml =>
        val response = scalaxb.fromXML[com.examples.JaxWsTest1Response](
          bodyXml.head
        )

        response.returnValue shouldBe "MOCK"
      }
    }
  }
}

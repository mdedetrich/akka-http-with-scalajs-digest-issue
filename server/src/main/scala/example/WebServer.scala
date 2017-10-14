package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.io.Source

object WebServer {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("server-system")
    implicit val materializer = ActorMaterializer()

    val config = ConfigFactory.load()
    val interface = config.getString("http.interface")
    val port = config.getInt("http.port")

    val service = new WebService()

    def loadResource(path: String) = {
      try {
        val stream = getClass.getResourceAsStream(path)
        val content = Source.fromInputStream(stream).getLines.mkString("\n")
        println(s"Successfully loaded $path")
        Some(content)
      } catch {
        case _: NullPointerException =>
          println(s"failed to load $path")
          None
      }
    }

    // These fail
    loadResource("/public/8582ad69409ecf661cc901a281d8a30c-client-fastopt.js")
    loadResource("/public/caf72f3eb2cfcf894c08bc1f205ed45f-client-fastopt.js.map")

    // These succeed
    loadResource("/public/ad2ed4340d1d8eb47b4a/example/e2a7c3ea2325d3f58a43f254e36e7086-ScalaJSExample.scala")
    loadResource("/public/stylesheets/5f59caadb44c8d720a2a5d9fc948eed0-main.css") // Compiled from main.less
    loadResource("/public/client-fastopt.js")


    Http().bindAndHandle(service.route, interface, port)

    println(s"Server online at http://$interface:$port")
  }
}

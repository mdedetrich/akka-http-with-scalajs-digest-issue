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

    def printResourceAsStream(path: String) = {
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

    def printResources(path: String) = {
      val uris = getClass.getClassLoader.getResources(path)
      if (!uris.hasMoreElements) {
        println(s"failed, no resources for $path")
      }
      while (uris.hasMoreElements) {
        println(s"element for $path: ${uris.nextElement().toString}")
      }
    }

    // These fail
    printResourceAsStream("/public/8582ad69409ecf661cc901a281d8a30c-client-fastopt.js")
    printResourceAsStream("/public/caf72f3eb2cfcf894c08bc1f205ed45f-client-fastopt.js.map")

    // These succeed
    printResourceAsStream("/public/ad2ed4340d1d8eb47b4a/example/e2a7c3ea2325d3f58a43f254e36e7086-ScalaJSExample.scala")
    printResourceAsStream("/public/stylesheets/5f59caadb44c8d720a2a5d9fc948eed0-main.css") // Compiled from main.less
    printResourceAsStream("/public/client-fastopt.js")

    // These fail
    printResources("public/8582ad69409ecf661cc901a281d8a30c-client-fastopt.js")

    // These succeed

    printResources("public")
    printResources("public/client-fastopt.js")
    printResources("public/stylesheets/5f59caadb44c8d720a2a5d9fc948eed0-main.css")


    // These succeed but with an incorrect uri

    printResourceAsStream("/public//8582ad69409ecf661cc901a281d8a30c-client-fastopt.js")
    printResourceAsStream("/public//caf72f3eb2cfcf894c08bc1f205ed45f-client-fastopt.js.map")
    printResources("public//8582ad69409ecf661cc901a281d8a30c-client-fastopt.js")


    Http().bindAndHandle(service.route, interface, port)

    println(s"Server online at http://$interface:$port")
  }
}

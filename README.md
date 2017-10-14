# Issue with sbt-digest

Based off [here](https://github.com/vmunier/akka-http-with-scalajs-example)

This example project demonstrates an issue with sbt-diget and loading
assets from `sbt-web`'s classpath. If you check Webserver you will see
this code

```scala
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

```

When you run this project through SBT, you will see that all non-scalajs
assets are found as a resource via the Classpath fine, however any non digest
Scala.js asset can't be found (funnily enough the standard `client-fastopt.js`
can be found)

You can find the `.jar` which contains the assets by simply unzipping
`server/target/scala-2.12/server_2.12-0.1-SNAPSHOT-web-assets.jar`

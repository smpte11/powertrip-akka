lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.11"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.powertrip",
      scalaVersion    := "2.12.4"
    )),
    name := "powertrip",
    libraryDependencies ++= Seq(
      "com.typesafe.akka"      %% "akka-http"                   % akkaHttpVersion,
      "com.typesafe.akka"      %% "akka-http-spray-json"        % akkaHttpVersion,
      "com.typesafe.akka"      %% "akka-http-xml"               % akkaHttpVersion,
      "com.typesafe.akka"      %% "akka-stream"                 % akkaVersion,
      "com.typesafe.akka"      %% "akka-slf4j"                  % "2.5.16",
      
      "com.lightbend.akka"     %% "akka-stream-alpakka-mongodb" % "1.0-M1",

      "org.mongodb.scala"      %% "mongo-scala-driver"          % "2.4.2",

      "io.circe"               %% "circe-core"                  % "0.11.0", 
      "io.circe"               %% "circe-generic"               % "0.11.0",
      "io.circe"               %% "circe-generic-extras"        % "0.11.0",
      "io.circe"               %% "circe-parser"                % "0.11.0", 
      "io.circe"               %% "circe-shapes"                % "0.11.0",
      
      "de.heikoseeberger"      % "akka-http-circe_2.12"         % "1.23.0" ,
      
      "org.typelevel"          %% "alleycats-core"              % "1.5.0", 

      "ch.qos.logback"         % "logback-classic"              % "1.2.3",
      "io.verizon.journal"     % "core_2.11"                    % "3.0.19",

      "de.flapdoodle.embed"    % "de.flapdoodle.embed.mongo"    % "2.2.0"         % Test, 
      "com.typesafe.akka"      %% "akka-http-testkit"           % akkaHttpVersion % Test,
      "com.typesafe.akka"      %% "akka-testkit"                % akkaVersion     % Test,
      "com.typesafe.akka"      %% "akka-stream-testkit"         % akkaVersion     % Test,
      "org.scalatest"          %% "scalatest"                   % "3.0.1"         % Test,
      "org.scalamock"          %% "scalamock"                   % "4.1.0"         % Test, 
      "com.github.simplyscala" %% "scalatest-embedmongo"        % "0.2.4"         % Test,
      "io.circe"               %% "circe-literal"               % "0.11.0"        % Test
    )
    
  )

resolvers += Resolver.sonatypeRepo("releases")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)


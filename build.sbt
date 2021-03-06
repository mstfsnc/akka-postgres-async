ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "akka-postgres-async-rest"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-actor-typed"     % "2.6.19",
  "com.typesafe.akka"          %% "akka-stream"          % "2.6.19",
  "com.typesafe.akka"          %% "akka-http"            % "10.2.9",
  "com.typesafe.akka"          %% "akka-http-spray-json" % "10.2.9",
  "com.typesafe.akka"          %% "akka-stream-kafka"    % "3.0.0",
  "com.typesafe"                % "config"               % "1.4.2",
  "io.getquill"                %% "quill-async-postgres" % "3.12.0",
  "org.apache.solr"             % "solr-solrj"           % "9.0.0",
  "ch.qos.logback"              % "logback-classic"      % "1.2.10",
  "com.typesafe.scala-logging" %% "scala-logging"        % "3.9.4"
)
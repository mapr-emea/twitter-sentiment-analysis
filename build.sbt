
crossPaths := false
scalaVersion := "2.11.8"
resolvers += "Akka Repository" at "http://repo.akka.io/releases/"
resolvers += "MapR Repo" at "http://repository.mapr.com/maven/"
resolvers += "clojars" at "https://clojars.org/repo"
resolvers += "conjars" at "http://conjars.org/repo"

dependencyOverrides ++= Set(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scala-lang" % "scalap" % scalaVersion.value
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

// Scala deps
val sparkVersion = "2.1.0"
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided"
libraryDependencies += "org.apache.bahir" %% "spark-streaming-twitter" % sparkVersion

libraryDependencies += "org.elasticsearch" %% "elasticsearch-spark-20" % "5.2.1"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

// MapR-DB deps
val maprVersion = "5.2.1-mapr"
libraryDependencies += "com.mapr.db" % "maprdb-spark" % maprVersion

// Java deps
val hbaseVersion = "1.1.8-mapr-1703"
libraryDependencies += "org.apache.hbase" % "hbase-common" % hbaseVersion
libraryDependencies += "org.apache.hbase" % "hbase-client" % hbaseVersion
libraryDependencies += "org.apache.hbase" % "hbase-server" % hbaseVersion


libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.16"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.6.2"

libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "4.0.6"

libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0"
libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models"



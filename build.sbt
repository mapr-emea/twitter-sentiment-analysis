val hbaseVersion = "1.1.1-mapr-1602-m7-5.2.0"
libraryDependencies += "org.apache.hbase" % "hbase-common" % hbaseVersion

libraryDependencies += "org.apache.hbase" % "hbase-client" % hbaseVersion

libraryDependencies += "org.apache.hbase" % "hbase-server" % hbaseVersion

val sparkVersion = "2.0.1"
libraryDependencies += "org.apache.spark" %% "spark-core" %  sparkVersion % "provided"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion  % "provided"

libraryDependencies += "org.apache.bahir" %% "spark-streaming-twitter" % sparkVersion

libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "3.0.3"

libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.5.1"

libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.5.1" classifier "models"

libraryDependencies += "org.elasticsearch" % "elasticsearch-spark_2.10" % "2.1.0.Beta3"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

resolvers += "MapR Repo" at "http://repository.mapr.com/maven/"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

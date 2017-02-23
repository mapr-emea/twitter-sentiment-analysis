package com.github.vspiewak

import java.text.SimpleDateFormat

import com.cybozu.labs.langdetect.DetectorFactory
import com.github.vspiewak.util.SentimentAnalysisUtils._
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import com.typesafe.scalalogging._
import org.apache.spark.sql.SparkSession
import org.elasticsearch.spark._

import scala.util.Try

object TwitterSentimentAnalysis extends LazyLogging {

  private val dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ")

   def main(args: Array[String]) {

     if (args.length < 8) {
       System.err.println("Usage: TwitterSentimentAnalysis <consumer key> <consumer secret> " +
         "<access token> <access token secret> <MapRDBOnly|ElasticOnly|MapRDBandElastic> <maprdb table name> <maprdb columnfamily name> <temp output folder> [<filters>]")
       System.exit(1)
     }

     DetectorFactory.loadProfile("/mapr/training.mapr.com/data/twitter-sentiment-analysis/src/main/resources/profiles")
     //DetectorFactory.loadProfile("src/main/resources/profiles")

     val Array(consumerKey, consumerSecret, accessToken, accessTokenSecret, storageType, maprdbTableName, maprdbColumnFamilyName, tempOutputFolder) = args.take(8)
     val filters = args.takeRight(args.length - 8)

     // Set the system properties so that Twitter4j library used by twitter stream
     // can use them to generate OAuth credentials
     System.setProperty("twitter4j.oauth.consumerKey", consumerKey)
     System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret)
     System.setProperty("twitter4j.oauth.accessToken", accessToken)
     System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)

     val spark = SparkSession.builder().appName("TwitterSentimentAnalysis").getOrCreate()

     val streamingContext = new StreamingContext(spark.sparkContext, Seconds(1))
     val tweets = TwitterUtils.createStream(streamingContext, None, filters)

     tweets.print()

    //set JobConfiguration variables for writing to HBase
    //val tableName = "twitter_sentiment"
    //val cfNameBytes = Bytes.toBytes("TwitterSentiment")

    //val conf = HBaseConfiguration.create()
    //val jobConfig: JobConf = new JobConf(conf, this.getClass)
    //jobConfig.set("mapreduce.output.fileoutputformat.outputdir", "/user/user01/out")
    //jobConfig.setOutputFormat(classOf[TableOutputFormat])
    //jobConfig.set(TableOutputFormat.OUTPUT_TABLE, tableName)

    // write tweets to MapR-DB
    if (storageType.toLowerCase() == "maprdbonly" || storageType.toLowerCase() == "maprdbandelastic") {
      //set JobConfiguration variables for writing to HBase
      val tableName = maprdbTableName
      val cfNameBytes = Bytes.toBytes(maprdbColumnFamilyName)

      val conf = HBaseConfiguration.create()
      val jobConfig: JobConf = new JobConf(conf, this.getClass)
      jobConfig.set("mapreduce.output.fileoutputformat.outputdir", tempOutputFolder)
      jobConfig.setOutputFormat(classOf[TableOutputFormat])
      jobConfig.set(TableOutputFormat.OUTPUT_TABLE, tableName)

      // Write to MapR-DB
      tweets.foreachRDD{(rdd, time) =>
         rdd.map(t => {dateFormatter.format(t.getCreatedAt)
           val key = t.getUser.getScreenName + "-" + dateFormatter.format(t.getCreatedAt)
           val p = new Put(Bytes.toBytes(key))

           p.add(cfNameBytes, Bytes.toBytes("user"), Bytes.toBytes(t.getUser.getScreenName))
           p.add(cfNameBytes, Bytes.toBytes("created_at"), Bytes.toBytes(dateFormatter.format(t.getCreatedAt)))
           p.add(cfNameBytes, Bytes.toBytes("location"), Bytes.toBytes(Option(t.getGeoLocation).map(geo => { s"${geo.getLatitude},${geo.getLongitude}" }).toString))
           p.add(cfNameBytes, Bytes.toBytes("text"), Bytes.toBytes(t.getText))
           p.add(cfNameBytes, Bytes.toBytes("hashtags"), Bytes.toBytes(t.getHashtagEntities.map(_.getText).toString))
           p.add(cfNameBytes, Bytes.toBytes("retweet"), Bytes.toBytes(t.getRetweetCount))
           p.add(cfNameBytes, Bytes.toBytes("language"), Bytes.toBytes(detectLanguage(t.getText)))
           p.add(cfNameBytes, Bytes.toBytes("sentiment"), Bytes.toBytes(detectSentiment(t.getText).toString))
           (new ImmutableBytesWritable, p)
         }).saveAsHadoopDataset(jobConfig)
       }
    }

    // Write tweets to Elasticsearch
    if (storageType.toLowerCase() == "elasticonly" || storageType.toLowerCase() == "maprdbandelastic") {
       tweets.foreachRDD{(rdd, time) =>
         rdd.map(t => {
           Map(
             "user"-> t.getUser.getScreenName,
             "created_at" -> dateFormatter.format(t.getCreatedAt),
             "location" -> Option(t.getGeoLocation).map(geo => { s"${geo.getLatitude},${geo.getLongitude}" }),
             "text" -> t.getText,
             "hashtags" -> t.getHashtagEntities.map(_.getText),
             "retweet" -> t.getRetweetCount,
             "language" -> detectLanguage(t.getText),
             "sentiment" -> detectSentiment(t.getText).toString
           )
         }).saveToEs("twitter/tweet")
      }
    }
     streamingContext.start()
     streamingContext.awaitTermination()
   }

  def detectLanguage(text: String) : String = {

    Try {
      val detector = DetectorFactory.create()
      detector.append(text)
      detector.detect()
    }.getOrElse("unknown")

  }

 }
Twitter Sentiment Analysis
==========================

Retrieve tweets using Spark Streaming,    
language detection & sentiment analysis (StanfordNLP),    
live dashboard using Kibana.
Ingest the tweets to MapR-DB.

Launch:

    # Compile the Twitter Sentiment Analysis jar
    JAVA_OPTS=-Xmx2G sbt assembly

    # Create a table in MapR-DB to store the Twitter messages plus the sentiment analysis result
    su - mapr
    hbase shell
    create 'twitter_sentiment', 'TwitterSentiment'

    # Launch the Twitter capture and store the messages in MapR-DB & Elasticsearch
    su - mapr

    /opt/mapr/spark/spark-*/bin/spark-submit \
    --class com.github.vspiewak.TwitterSentimentAnalysis \
    --master local[2] \
    target/twitter-sentiment-analysis-assembly-0.1-SNAPSHOT.jar \
    <consumer_key> \
    <consumer_secret> \
    <access_token> \
    <access_token_secret> \
    <maprdbandelastic|maprdbjsonandelastic|maprdbonly|maprdbjsononly|elasticonly> \
    </path/to/maprdb-binary-table> \
    <ColumnFamily> \
    /user/mapr/out \
    [<filters>]

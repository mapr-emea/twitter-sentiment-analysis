CREATE OR REPLACE VIEW `dfs`.`twitter_view` AS
SELECT
CAST(CONVERT_FROM(Tweets.TwitterSentiment.`created_at`, 'UTF8') AS VARCHAR(20)) AS `created_at`,
CAST(CONVERT_FROM(Tweets.TwitterSentiment.`hashtags`, 'UTF8') AS VARCHAR(140)) AS `hashtags`,
CAST(CONVERT_FROM(Tweets.TwitterSentiment.`language`, 'UTF8') AS VARCHAR(2)) AS `language`,
CAST(CONVERT_FROM(Tweets.TwitterSentiment.`location`, 'UTF8') AS VARCHAR(140)) AS `location`,
CAST(CONVERT_FROM(Tweets.TwitterSentiment.`retweet`, 'UTF8') AS VARCHAR(140)) AS `retweet`,
CAST(CONVERT_FROM(Tweets.TwitterSentiment.`sentiment`, 'UTF8') AS VARCHAR(20)) AS `sentiment`,
CAST(CONVERT_FROM(Tweets.TwitterSentiment.`text`, 'UTF8') AS VARCHAR(140)) AS `text`,
CAST(CONVERT_FROM(Tweets.TwitterSentiment.`user`, 'UTF8') AS VARCHAR(20)) AS `user`
FROM dfs.`twitter_sentiment` Tweets;
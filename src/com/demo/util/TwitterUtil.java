package com.demo.util;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public final class TwitterUtil {

	public static final String CONSUMER_KEY = "NZcAjwwC9LLPZr2tYgBY8w";
	public static final String CONSUMER_SECRET = "LfgoKxUE5PwLShC0StKNd94edZAsWYy37FZG8Qnpc4";
	
	public static final String ACCESS_TOKEN = "558182990-Cy69bp4TDL768B7NUYRo7Y23HsNQ6yGssO5XyNKk";
	public static final String ACCESS_TOKEN_SECRET = "JY22upkULFz16Ph9wWfwgJ57GZFzP4i0PRSjFB535s";
	
	public static ResponseList<Status> getTweets() throws TwitterException {		
		TwitterFactory factory = new TwitterFactory();
		Twitter twitter = factory.getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		twitter.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET));
		return twitter.getHomeTimeline();
	}
	
	public static void tweet(String tweet) throws TwitterException {
		TwitterFactory factory = new TwitterFactory();
		Twitter twitter = factory.getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		twitter.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET));
		twitter.updateStatus(tweet);
	}
}

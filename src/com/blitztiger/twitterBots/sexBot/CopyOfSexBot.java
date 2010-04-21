package com.blitztiger.twitterBots.sexBot;
//version 0.10
import java.util.*;

import com.blitztiger.twitterBots.Twitter;
import com.blitztiger.twitterBots.TwitterBot;
import com.blitztiger.twitterBots.Twitter.Status;

/**
  * @author Jeffrey Marvin
  * Copyright Jeffrey Marvin, released for non-commercial use with attribution.
**/

public class CopyOfSexBot implements TwitterBot {
	
	static long waitBetweenRequestTime = Long.valueOf("30000");//milliseconds
	static long waitIfExceedRateLimitTime = Long.valueOf("120000");//milliseconds
	
	public static void main(String args[]){
		while(true){
			try{
				new CopyOfSexBot().runBot("*****", "*****", true, null);
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("Whoops, there was a fail... let's try that again in a minute...");
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public void runBot(String userName, String password, boolean publicTimeline, String userToGetTimelineOf) throws Exception {
		Twitter	twitter = new Twitter(userName, password);
		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put(" an ex", " a sex");
		replacements.put(" an eX", " a seX");
		replacements.put(" an Ex", " a Sex");
		replacements.put(" an EX", " a SEX");
		replacements.put(" An ex", " A sex");
		replacements.put(" An eX", " A seX");
		replacements.put(" An Ex", " A Sex");
		replacements.put(" An EX", " A SEX");
		replacements.put(" aN ex", " a sex");
		replacements.put(" aN eX", " a seX");
		replacements.put(" aN Ex", " a Sex");
		replacements.put(" aN EX", " a SEX");
		replacements.put(" AN ex", " A sex");
		replacements.put(" AN eX", " A seX");
		replacements.put(" AN Ex", " A Sex");
		replacements.put(" AN EX", " A SEX");
		replacements.put(" ex", " sex");
		replacements.put(" eX", " seX");
		replacements.put(" Ex", " Sex");
		replacements.put(" EX", " SEX");
		replacements.put(" an sex", " a sex");
		replacements.put(" an seX", " a seX");
		replacements.put(" an Sex", " a Sex");
		replacements.put(" an SEX", " a SEX");
		replacements.put(" An sex", " A sex");
		replacements.put(" An seX", " A seX");
		replacements.put(" An Sex", " A Sex");
		replacements.put(" An SEX", " A SEX");
		replacements.put(" aN sex", " a sex");
		replacements.put(" aN seX", " a seX");
		replacements.put(" aN Sex", " a Sex");
		replacements.put(" aN SEX", " a SEX");
		replacements.put(" AN sex", " A sex");
		replacements.put(" AN seX", " A seX");
		replacements.put(" AN Sex", " A Sex");
		replacements.put(" AN SEX", " A SEX");
		replacements.put("\"ex", "\"sex");
		replacements.put("\"eX", "\"seX");
		replacements.put("\"Ex", "\"Sex");
		replacements.put("\"EX", "\"SEX");
		List<Twitter.Status> myStatuses = twitter.getUserTimeline();
		while(true){
			System.out.println(twitter.getRateLimitStatus());
			if(twitter.getRateLimitStatus() < 5){
				System.out.println("Waiting because I went over the rate limit :(");
			}
			while(twitter.getRateLimitStatus() < 5){
				System.out.println("\tstill waiting: rate limit at " + twitter.getRateLimitStatus());
				Thread.sleep(waitIfExceedRateLimitTime);
			}
			List<Status> timeline;
			if(publicTimeline){
				timeline = twitter.getPublicTimeline();
			} else if (userToGetTimelineOf != null){
				timeline = twitter.getUserTimeline(userToGetTimelineOf);
			} else {
				timeline = twitter.getFriendsTimeline();
			}			
			for(Status s : timeline){
				String tweet = s.text;
				String oldTweet;
				do {
					oldTweet = tweet;
					for(String key : replacements.keySet()){
						if(s.text.contains(key)){
							tweet =  tweet.replace(key, replacements.get(key));
						}
					}
				} while(!tweet.equals(oldTweet));
				boolean alreadyTweeted = false;
				if(tweet.equals(s.text)){
					alreadyTweeted = true;
				}
				if(tweet.indexOf('@') == 0){
					tweet = tweet.substring(tweet.indexOf(' ') + 1);
				}
				if(tweet.length() > 140){
					tweet = tweet.substring(0, 140 - ("... #sexd @" + s.user.screenName).length()) + "... #sexd @" + s.user.screenName;
				} else {
					tweet = tweet + " #sexd @" + s.user.screenName;
				}
				for(Status myStatus : myStatuses){
					if(myStatus.getText().equals(tweet)){
						alreadyTweeted = true;
					}
				}
				if(!alreadyTweeted){
					System.out.println(tweet);
					myStatuses.add(twitter.setStatus(tweet));
				}
			}
			System.out.println("Waiting to avoid going over the rate limit");
			Thread.sleep(waitBetweenRequestTime);
		}
	}
}
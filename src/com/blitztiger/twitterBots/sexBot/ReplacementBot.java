package com.blitztiger.twitterBots.sexBot;
//version 0.07
import java.util.*;

import com.blitztiger.twitterBots.Twitter;
import com.blitztiger.twitterBots.TwitterBot;
import com.blitztiger.twitterBots.Twitter.Status;

/**
  * @author Jeffrey Marvin
  * Copyright Jeffrey Marvin, released for non-commercial use with attribution.
**/

public class ReplacementBot implements TwitterBot {
	
	static long waitBetweenRequestTime = Long.valueOf("30000");//milliseconds
	static long waitIfExceedRateLimitTime = Long.valueOf("120000");//milliseconds
	private Map<String, String> replacements;
	
	public static void main(String args[]){
		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put(" ex", " sex");
		replacements.put(" eX", " seX");
		replacements.put(" Ex", " Sex");
		replacements.put(" EX", " SEX");		
		while(true){
			try{
				new ReplacementBot().setMap(replacements).runBot("*****", "*****", false, null);
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("Whoops, there was a fail... let's try that again in a minute...");
				try {
					Thread.sleep(60000); // Wait a minute
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			}
		}
	}
	
	public ReplacementBot setMap(Map<String, String> replacements){
		this.replacements = replacements;
		return this;
	}
	

	public void runBot(String userName, String password, boolean publicTimeline, String userToGetTimelineOf) throws Exception {
		Twitter	twitter = new Twitter(userName, password);
		List<Twitter.Status> myStatuses = twitter.getUserTimeline();
		while(true){
			System.out.println(twitter.getRateLimitStatus());
			if(twitter.getRateLimitStatus() < 5){
				System.out.println("Waiting because I went over the rate limit :(");
			}
			while(twitter.getRateLimitStatus() < 5){
				Thread.sleep(waitIfExceedRateLimitTime);
				System.out.println("\tstill waiting: rate limit at " + twitter.getRateLimitStatus());
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
				for(String key : replacements.keySet() )
				if(s.text.contains(key)){
					boolean alreadyTweeted = false;
					String tweet =  "@" + s.user.screenName + " " + s.text.replace(key, replacements.get(key));
					if(tweet.length() > 140){
						if((tweet.substring(0, 137) + "...").equals(s.text.substring(0, 137) + "...")){
							alreadyTweeted = true;
						}
						else {
							tweet = tweet.substring(0, 137) + "...";
						}
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
			}
			System.out.println("Waiting to avoid going over the rate limit");
			Thread.sleep(waitBetweenRequestTime);
		}
	}
}
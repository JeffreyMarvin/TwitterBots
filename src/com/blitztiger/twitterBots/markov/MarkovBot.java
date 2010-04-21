package com.blitztiger.twitterBots.markov;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.blitztiger.twitterBots.Twitter;
import com.blitztiger.twitterBots.TwitterBot;
import com.blitztiger.twitterBots.Twitter.Status;

/**
  * @author Jeffrey Marvin
  * Copyright Jeffrey Marvin, released for non-commercial use with attribution.
**/

public class MarkovBot implements TwitterBot {
	static long waitBetweenRequestTime = Long.valueOf("30000");//30,000 milliseconds = 30 seconds
	static long waitIfExceedRateLimitTime = Long.valueOf("120000");//120,000 milliseconds = 2 minutes
	
	private MarkovElement<String> head;
	private List<MarkovElement<String>> allElements;
	
	public MarkovBot(){
		head = new MarkovElement<String>("");
		allElements = new ArrayList<MarkovElement<String>>();
	}
	
	public void runBot(String userName, String password, boolean publicTimeline, String userToGetTimelineOf) throws Exception{
		Random rand = new Random();
		int iterations = 0; 
		Twitter	twitter = new Twitter(userName, password);
		List<Status> timeline, savedStatuses = new ArrayList<Status>();
		System.out.println(twitter.getRateLimitStatus());
		if(twitter.getRateLimitStatus() < 5){
			System.out.println("Waiting because I went over the rate limit :(");
		}
		while(twitter.getRateLimitStatus() < 5){
			System.out.println("\tstill waiting: rate limit at " + twitter.getRateLimitStatus());
			Thread.sleep(waitIfExceedRateLimitTime);
		}
		while(true){
			if(publicTimeline){
				timeline = twitter.getPublicTimeline();
			} else if (userToGetTimelineOf != null){
				timeline = twitter.getUserTimeline(userToGetTimelineOf);
			} else {
				timeline = twitter.getFriendsTimeline();
			} 
			for(Status status : timeline){
				if(savedStatuses.contains(status)){
					continue;
				}
				if(status.text.equals("")){
					continue;
				}
				insertSentence(status.text);
			}
			if(iterations-- == 0){
				String sentence = buildSentence();
				if(sentence.length() > (140 - " #markov".length())){
					sentence = sentence.substring(0, 140 - "... #markov".length()) + "... #markov";
				} else {
					sentence = sentence + " #markov";
				}
				twitter.setStatus(sentence);
				System.out.println(sentence);
				iterations = rand.nextInt(60);
			}
			System.out.println("Waiting to avoid going over the rate limit");
			Thread.sleep(waitBetweenRequestTime);
		}
	}
	
	private String buildSentence(){
		String sentence = "";
		for(String word : head.getChain()){
			sentence = sentence + word + " ";
		}
		return sentence.trim();
	}
	
	private void insertSentence(String sentence){
		String[] words = sentence.split(" ");
		MarkovElement<String> ele = head;
		for(String word : words){
			ele = ele.insert(findElement(word));
		}
	}

	private MarkovElement<String> findElement(String word) {
		for(MarkovElement<String> element : allElements){
			if(element.getValue().equals(word)){
				return element;
			}
		}
		MarkovElement<String> element = new MarkovElement<String>(word);
		allElements.add(element);
		return element;
	}
	
	public static void main(String[] args){
		while(true){
			try{
				new MarkovBot().runBot("markovtwain", "markov", false, null);
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
}

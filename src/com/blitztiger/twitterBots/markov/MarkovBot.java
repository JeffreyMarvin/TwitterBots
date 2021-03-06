package com.blitztiger.twitterBots.markov;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.io.*;

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
	private List<String> savedStatuses;
	
	@SuppressWarnings("unchecked")
	public MarkovBot(){
		ObjectInputStream objectIn = null;
		try {
			objectIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream("Markov.bin")));
		} catch (Exception e) {
			head = new MarkovElement<String>("");
			allElements = new ArrayList<MarkovElement<String>>();
			savedStatuses = new ArrayList<String>();
			return;
		}
		try {
			head = (MarkovElement<String>)objectIn.readObject();
			allElements = (List<MarkovElement<String>>)objectIn.readObject();
			savedStatuses = (List<String>)objectIn.readObject();
		} catch (Exception e) {
			head = new MarkovElement<String>("");
			allElements = new ArrayList<MarkovElement<String>>();
			savedStatuses = new ArrayList<String>();
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void runBot(String userName, String password, boolean publicTimeline, String userToGetTimelineOf) throws Exception{
		try {
			ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream("Markov.bin")));
			head = (MarkovElement<String>)objectIn.readObject();
			allElements = (List<MarkovElement<String>>)objectIn.readObject();
			savedStatuses = (List<String>)objectIn.readObject();
		} catch (FileNotFoundException e) {}
		System.out.println(head.toString());
//		System.exit(0);
		Random rand = new Random();
		int iterations = 0; 
		Twitter	twitter = new Twitter(userName, password);
		List<Status> timeline;
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
				boolean alreadyAdded = false;
				for(String saved : savedStatuses){
					if(status.text.equals(saved)){
						alreadyAdded = true;
					}
				}
				if(!alreadyAdded){
					insertSentence(status.text);
					savedStatuses.add(status.text);
				}
			}
			ObjectOutputStream objectOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("Markov.bin")));
			objectOut.writeObject(head);
			objectOut.writeObject(allElements);
			objectOut.writeObject(savedStatuses);
			objectOut.close();
			if(iterations-- == 0){
				String sentence = buildSentence();
				if(sentence.length() > (140 - " #markov".length())){
					sentence = sentence.substring(0, 140 - "... #markov".length()) + "... #markov";
				} else {
					sentence = sentence + " #markov";
				}
				twitter.setStatus(sentence);
				System.out.println(sentence);
				//iterations = 0;
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
			if(!word.equals(""))
				ele = ele.insert(findElement(word));
		}
	}

	private MarkovElement<String> findElement(String word) {
		for(MarkovElement<String> element : allElements){
			if(element.getValue().toLowerCase().equals(word.toLowerCase())){
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
				new MarkovBot().runBot("markovtwain", "****", true, null);
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
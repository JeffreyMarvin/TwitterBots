package com.blitztiger.twitterBots;
import com.blitztiger.twitterBots.Twitter.Status;

/**
  * @author Jeffrey Marvin
  * Copyright Jeffrey Marvin, released for non-commercial use with attribution.
**/

public class TestBot {
	public static void main(String args[]){
		Twitter	twitter = new Twitter("*****", "*****");
		for(Status s : twitter.getFriendsTimeline()){
			if(s.text.contains(" Ex")){
				String tweet =  "@" + s.user.screenName + " " + s.text.replace(" Ex", " Sex");
				System.out.println(tweet);
				boolean alreadyTweeted = false;
				for(Status myStatus : twitter.getUserTimeline()){
					System.out.println(myStatus);
					if(!myStatus.getText().equals(tweet)){
						alreadyTweeted = true;
					}
				}
				System.out.println(alreadyTweeted);
				if(!alreadyTweeted){
					twitter.setStatus(tweet);
				}
			}
		}
	}
}
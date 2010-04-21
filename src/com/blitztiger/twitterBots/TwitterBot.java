package com.blitztiger.twitterBots;

public interface TwitterBot {
	public void runBot(String userName, String password, boolean publicTimeline, String userToGetTimelineOf) throws Exception;
}
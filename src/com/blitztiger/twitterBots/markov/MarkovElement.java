package com.blitztiger.twitterBots.markov;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
  * @author Jeffrey Marvin
  * Copyright Jeffrey Marvin, released for non-commercial use with attribution.
**/

public class MarkovElement<T> {
	private T value;
	private ArrayList<MarkovElement<T>> branches;
	
	public MarkovElement(T value){
		this.value = value;
		branches = new ArrayList<MarkovElement<T>>();
	}
	
	public MarkovElement<T> insert(MarkovElement<T> newElement){
		branches.add(newElement);
		return newElement;
	}
	
	public T getValue(){
		return value;
	}
	
	public List<T> getChain(){
		if(branches.isEmpty()){
			List<T> list = new ArrayList<T>();
			list.add(value);
			return list;
		}
		List<T> list = getRandomBranch().getChain();
		list.add(0, value);
		return list;
	}
	
	public MarkovElement<T> getRandomBranch(){
		if(branches.isEmpty()){
			return null;
		}
		Random rand = new Random();
		return branches.get(rand.nextInt(branches.size()));
	}
}
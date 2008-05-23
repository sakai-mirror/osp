package org.theospi.portfolio.matrix.model;

public class ReviewTypeAndCount {

	private int type;
	private int count;
	
	public ReviewTypeAndCount(int type, int count){
		this.type = type;
		this.count = count;
	}
	
	public int getType() {
		return type;
	}

	public int getCount() {
		return count;
	}	
}

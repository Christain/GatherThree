package com.gather.android.database;

public class PublishTrendsInfo {
	
	public int id;
	public String content;
	public String imgPaths;
	public long time;
	public int userId;
	
	public PublishTrendsInfo(int id, String content, String imgPaths, long time, int userId) {
		super();
		this.id = id;
		this.content = content;
		this.imgPaths = imgPaths;
		this.time = time;
		this.userId = userId;
	}
	
	public PublishTrendsInfo(String content, String imgPaths, long time, int userId) {
		super();
		this.content = content;
		this.imgPaths = imgPaths;
		this.time = time;
		this.userId = userId;
	}

}

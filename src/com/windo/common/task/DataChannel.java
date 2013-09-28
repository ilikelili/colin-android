package com.windo.common.task;


public abstract class DataChannel {

	
	protected TaskEngine mTaskEngine;
	
	public DataChannel(TaskEngine engine)
	{
		mTaskEngine = engine;
	}
	
	
	public abstract void sendRequest(Object obj,Task t);
	
	
	public abstract void cancelRequest(Task t);
	
	
	public abstract void close();
}

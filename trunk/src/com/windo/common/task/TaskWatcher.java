package com.windo.common.task;

public interface TaskWatcher {

	
	public void onTaskComplete(int taskID);
	
	
	public void onTaskMessage(int taskID, int code, int arg2, Object arg3) ;

	
	public void onTaskError( int taskID, int errCode,int arg2, Object arg3);
	
}

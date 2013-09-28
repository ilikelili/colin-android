package com.windo.common.bluetooth;

public interface TaskWatcher {

	/**
	 * 任务完成回调
	 */
	public void onTaskComplete(int taskID);
	
	/**
	 * 任务消息回调
	 * @param code
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public void onTaskMessage(int taskID, int code, int arg2, Object arg3) ;

	/**
	 * 任务出错回调
	 * @param errCode
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public void onTaskError( int taskID, int errCode,int arg2, Object arg3);
	
}

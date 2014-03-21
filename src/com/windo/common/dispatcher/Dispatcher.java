package com.windo.common.dispatcher;

/**
 * 派发者
 * 
 * @author houmiao.xiong
 * 
 * @time 2013-10-21 下午5:19:06
 */
public interface Dispatcher {
	
	/**
	 * 派发正常消息
	 * @param msgCode
	 * @param agr1
	 * @param obj
	 * @time 2013-10-21 下午5:19:17
	 */
	public void onTaskMessage(int taskID, int code, int arg2, Object arg3) ;

	/**
	 * 派发错误消息
	 * @param errCode
	 * @param arg1
	 * @param obj
	 * @time 2013-10-21 下午5:19:30
	 */
	public void onTaskError( int taskID, int errCode,int arg2, Object arg3);
	
//	/**
//	 * 派发正常消息
//	 * @param msgCode
//	 * @param agr1
//	 * @param obj
//	 * @time 2013-10-21 下午5:19:17
//	 */
//	public void dispatcherMessage(int msgCode, int arg1, int arg2, Object obj);
//	
//	/**
//	 * 派发错误消息
//	 * @param errCode
//	 * @param arg1
//	 * @param obj
//	 * @time 2013-10-21 下午5:19:30
//	 */
//	public void dispatcherError(int errCode, int arg1, int arg2, Object obj);

}

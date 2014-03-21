package com.windo.common.dispatcher;

/**
 * 派发中心接口
 * 
 * @author houmiao.xiong
 * 
 * @time 2013-10-21 下午4:57:16
 */
public interface IDispatch {

	/**
	 * 添加派发者
	 * @param key
	 * @param dis
	 * @time 2013-10-21 下午5:01:55
	 */
	public void addDispatcher(Object key, Dispatcher dis);
	
	/**
	 * 移除派发者
	 * @param key
	 * @param dis
	 * @time 2013-10-21 下午5:02:38
	 */
	public void removeDispatcher(Object key, Dispatcher dis);
	
	/**
	 * 清空一类派发者
	 * @time 2013-10-21 下午5:03:00
	 */
	public void clearDispatcher(Object key);
	
	
	/**
	 * 清空所有
	 * @time 2013-10-21 下午5:27:13
	 */
	public void clearAll();
	
	/**
	 * 派发正常消息
	 * @param key
	 * @param msgCode
	 * @param agr1
	 * @param obj
	 * @time 2013-10-21 下午5:10:46
	 */
	public void dispatchMessage(Object key, int msgCode, int arg1, int arg2, Object obj);
	
	
	/**
	 * 派发错误消息
	 * @param key
	 * @param errCode
	 * @param arg1
	 * @param obj
	 * @time 2013-10-21 下午5:10:58
	 */
	public void dispatchError(Object key, int errCode, int arg1, int arg2, Object obj);
	
}

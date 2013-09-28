package com.windo.common.bluetooth;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 逻辑业务层消息处理者
 * 		1.将消息区分成错误与普通消息. 可以通过实现handlerError与handlerMessage处理
 * 		2.要求所有ErrCode,与 MsgCode都是大于零
 * 
 * @author rongqiang.wang
 * @qq  176617222
 * @version 1.0 2012-8-22 下午10:56:38
 *
 */
public abstract class TaskHandler extends Handler implements TaskWatcher {
	
	public TaskHandler(){
		
	}
	
	public TaskHandler(Looper looper){
		super(looper);
	}
	
	/**
	 * 失败消息处理
	 * @param errCode 错误码
	 * @param arg1	整型参数
	 * @param obj   Obj型参数
	 */
	public abstract void handleError(int errCode,int arg1,Object obj);
	
	/**
	 * 普通消息处理
	 * @param msgCode  消息码
	 * @param arg1		整型参数
	 * @param obj		Obj型参数
	 */
	public abstract void handleMessage(int msgCode,int arg1,Object obj);
	
	
	/**
	 * 系统Handler回调接口,
	 * 	将系统消息根据what区分成普通消息和错误系统.转给handlerError与handlerMessage方法
	 * @param  msg ： 消息对象
	 * 
	 */
	public void handleMessage(Message msg){
		if(msg.what >= 0){
			handleMessage(msg.what,msg.arg1,msg.obj);
		}else{
			handleError(-msg.what, msg.arg1,msg.obj);
		}
	}
	
	@Override
	public void onTaskComplete(int taskID) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 任务消息传递调用
	 * @param taskID	任务ID
	 * @param code		消息Code	(需大于0)
	 * @param arg1		整型参数
	 * @param arg2		Obj型参数
	 */
	@Override
	public  final void onTaskMessage(int taskID, int code, int arg1, Object arg2) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain(this, code, arg1, taskID, arg2);
		sendMessage(msg);
		
	}

	/**
	 * 任务出错消息传递调用
	 * @param taskID	任务ID
	 * @param code		错误消息Code	(需大于0)
	 * @param arg1		整型参数
	 * @param arg2		Obj型参数
	 */
	@Override
	public final void onTaskError(int taskID, int errCode, int arg1, Object arg2) {
		// TODO Auto-generated method stub
		errCode = -errCode;
		
		Message msg = Message.obtain(this, errCode, arg1, taskID, arg2);
		sendMessage(msg);
	}

}

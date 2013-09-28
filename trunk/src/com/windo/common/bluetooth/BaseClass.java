package com.windo.common.bluetooth;

import android.content.Context;

import com.windo.common.pal.internal.PalLog;

/**
 * 基类
 * @author houmiao.xiong
 * @email 925399262@qq.com
 * @version 1.0 2013-2-22 上午10:38:56
 */
public abstract class BaseClass{
	
	protected Context mContext;
	
	
	public BaseClass(){}
	
	
	public BaseClass(Context context){
		mContext = context;
	}
	
	
	protected void notifyError(TaskWatcher watcher, int errCode){
		if(watcher != null){
			watcher.onTaskError(-1, errCode, -1, null);
		}
	}
	
	protected void notifyError(TaskWatcher watcher, int errCode, int arg1){
		if(watcher != null){
			watcher.onTaskError(-1, errCode, arg1, null);
		}
	}
	
	protected void notifyError(TaskWatcher watcher, int errCode, Object obj){
		if(watcher != null){
			watcher.onTaskError(-1, errCode, -1, obj);
		}
	}
	
	
	protected void notifyError(TaskWatcher watcher, int errCode, int arg1, Object obj){
		if(watcher != null){
			watcher.onTaskError(-1, errCode, arg1, obj);
		}
	}
	
	
	
	protected void notifyMessage(TaskWatcher watcher, int msgCode){
		if(watcher != null){
			watcher.onTaskMessage(-1, msgCode, -1, null);
		}
	}
	
	protected void notifyMessage(TaskWatcher watcher, int msgCode, int arg1){
		if(watcher != null){
			watcher.onTaskMessage(-1, msgCode, arg1, null);
		}
	}
	
	protected void notifyMessage(TaskWatcher watcher, int msgCode, Object obj){
		if(watcher != null){
			watcher.onTaskMessage(-1, msgCode, -1, obj);
		}
	}
	
	protected void notifyMessage(TaskWatcher watcher, int msgCode, int arg1, Object obj){
		if(watcher != null){
			watcher.onTaskMessage(-1, msgCode, arg1, obj);
		}
	}
	
	
	
	protected void print(String msg){
		PalLog.d(getTag(), msg);
	}
	
	public abstract String getTag();

}

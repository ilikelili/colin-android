package com.windo.common.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.windo.common.pal.internal.PalLog;

/**
 * 自定义广播接收器
 *     可避免重复处理多个相同的广播
 *     
 * @author houmiao.xiong
 * @date 2013-2-18
 * @email 925399262@qq.com
 */
public class MyReceiver extends BroadcastReceiver{
	
	private static final String TAG = "MyReceiver";
	
	private Context mContext;
	
	private boolean isRegistered;
	
	
	/**
	 * 构造函数
	 * if you needn't register, use this construct
	 */
	public MyReceiver(){}
	
	
	/**
	 * 构造函数
	 * if you need register, context must not be null
 	 * @param context
	 */
	public MyReceiver(Context context){
		if(context == null){
			throw new NullPointerException("context can not be null");
		}
		
		mContext = context;
	}
	
	
	/**
	 * 注册广播接收器    重复注册需注销后再注册
	 * @param actions
	 */
	public void register(String...actions){
		final Context context = mContext;
		
		if(context != null){
			if(!isRegistered){
				if(actions != null && actions.length > 0){
					IntentFilter filter = new IntentFilter();
					for(String action : actions){
						filter.addAction(action);
					}
					context.registerReceiver(this, filter);
					isRegistered = true;
				}
			}
		}
		
	}
	
	
	/**
	 * 注销
	 */
	public void unRegister(){
		final Context context = mContext;
		
		if(context != null){
			if(isRegistered){
				context.unregisterReceiver(this);
				isRegistered = false;
			}
		}
		
	}
	
	
	/**
	 * 是否已注册
	 * @return
	 */
	public boolean isRegistered(){
		return isRegistered;
	}
	

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(isRegistered){
			onMyReceive(context, intent);
		}
	}
	
	
	public void onMyReceive(Context context, Intent intent){
	}
	
	
	private void print(String msg){
		PalLog.d(TAG, msg);
	}

}

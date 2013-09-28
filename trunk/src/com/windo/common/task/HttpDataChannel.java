package com.windo.common.task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import com.windo.common.http.HttpCallBack;
import com.windo.common.http.HttpEngine;
import com.windo.common.http.HttpRequest;
import com.windo.common.pal.IHttp;
import com.windo.common.pal.internal.PalLog;

public class HttpDataChannel extends DataChannel implements HttpCallBack
{
	
	private static final String TAG = "HttpDataChannel";
	
	Hashtable<Integer,Short> mTaskMap;
	
	
	Hashtable<Short,HttpRequest> mRequestMap;
	
	HttpEngine mHttpEngine;
	
	String JSESSIONID = null;
	
	public HttpDataChannel(TaskEngine engine,HttpEngine httpEngine){
		super(engine);
		mTaskMap = new Hashtable<Integer,Short>(); 
		mRequestMap = new Hashtable<Short,HttpRequest>();
		mHttpEngine = httpEngine; 
	}
	

	
	public void sendRequest(Object obj, Task t) {
		
		if(obj instanceof HttpRequest){
			sendRequest((HttpRequest)obj,t);
		}else{
			t.onTaskException(new IllegalArgumentException());
		}
	}
	
	
	
	public void sendRequest(HttpRequest request,Task task)
	{
		synchronized (mTaskMap)
		{
			addCookieSession(request,JSESSIONID);
			mTaskMap.put(request.getRequestID(), task.getId());
			mRequestMap.put(task.getId(), request);
			request.setHttpCallBack(this);
			mHttpEngine.addRequest(request);
		}
	}

	@Override
	public void onError(int requestId, int errCode, byte[] errStr,IHttp http)
	{	
		Short tid = null;
		
		synchronized (mTaskMap)
		{
			tid = (Short)mTaskMap.remove(new Integer(requestId));
			if(tid != null){
				mRequestMap.remove(tid);
				
				//modify by xiong at 2013.7.24 
				//错误处理时间如果太长，导致UI上掉cancelRequest无法进行下去  ，报anr
//				mTaskEngine.dispatchError(tid, errCode, errStr);
				
			}
		}
		
		if(tid != null){
			mTaskEngine.dispatchError(tid, errCode, errStr);
		}
	}

    @Override
	public void onReceived(int requestId, byte[] data, IHttp http)
	{
		
		synchronized (mTaskMap)
		{	
			Integer key = new Integer(requestId);
			if(mTaskMap.containsKey(key)){
				
				try {
					JSESSIONID = getCookieSession(http);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
				Short tid = (Short)mTaskMap.remove(key);
				mRequestMap.remove(tid);
				DispatchTask notify = new DispatchTask(mTaskEngine, tid, data);
				mTaskEngine.addTask(notify);
			}
		}
	}
	
	
	private void addCookieSession(HttpRequest request,String session){
		if(request!= null && 
				session != null && session.length() > 0){
			request.addHeaderField("Cookie", session);	
		}
		
	}
	
	
	private String getCookieSession(IHttp http) throws IOException{
		String cookis = http.getHeaderField("Set-Cookie");
		if(cookis != null && cookis.indexOf("JSESSIONID") >=0){
			String[] items = cookis.split("[;]");
			for (int i = 0; i < items.length; i++) {
				if(items[i].trim().startsWith("JSESSIONID")){
					return items[i].trim();
				}
			}
		}
		return null;
	}
	
	
	public void cancelRequest(Task t){
		PalLog.d(TAG, "cancelRequest wait");
		synchronized (mTaskMap){
			PalLog.d(TAG, "cancelRequest ing");
			if(mRequestMap.containsKey(t.getId())){
				HttpRequest request = (HttpRequest)mRequestMap.remove(t.getId());
				request.doCancel();
				mTaskMap.remove(request.getRequestID());
			}	
		}
	}
	
	public void close(){
		if(mHttpEngine != null){
			mHttpEngine.shutdown();
			mHttpEngine = null;
		}
	}

    @Override
	public void onReceived(int requestId, InputStream stream, long contentLength, IHttp http) {
		
		
	}





}

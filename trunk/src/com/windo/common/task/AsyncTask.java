package com.windo.common.task;

import com.windo.common.pal.internal.PalLog;




public abstract class AsyncTask extends Task
{
	
	
	DataChannel mDataChannel;
	

	
	protected AsyncTask(TaskEngine taskEngine, int type) {
		super(taskEngine, type);
		
	}

	
	public abstract boolean onResponseError(int errCode,byte[] err);


	
	public abstract boolean onResponseSuccess(byte[] response);
	
	
	
	protected void reassignTask()
	{
		getTaskEngine().reassignTaskId(getId());
	}
	
	
	
	
	public void setDataChannel(DataChannel dataChannel) 
	{
		mDataChannel = dataChannel;
	}
	
	public DataChannel getDataChannel(){
		return mDataChannel;
	}
	
	
	protected void sendRequest(Object obj){
		if(mDataChannel != null)
		{
			mDataChannel.sendRequest(obj,this);
		}
	}
	
	
	public final void run(){
	  	try {
	  		if(!isCancel()){
	  			onTask();	
	  		}
		} catch (Exception e) {
			
			e.printStackTrace();
			PalLog.d("AsyncTask", "onTask Exception:" + e.toString());
			onTaskException(e);
		}
	  	
	  	if (isCancel()) {
			endTransaction();
		}
	}
	
	
	protected void dispatchResponseError(int errCode, byte[] err){
		try {
			onResponseError(errCode,err);	
		} catch (Exception e) {
			
			e.printStackTrace();
			PalLog.d("AsyncTask", "dispatchResponseError Exception:" + e.toString());
			onTaskException(e);
		}
		endTransaction();
	}

	
	
	protected void dispatchResponseSuccess(byte[] response)
	{
		try {
			onResponseSuccess(response);	
		}catch (Exception e) {
			
			e.printStackTrace();
			PalLog.d("AsyncTask", "dispatchResponseSuccess Exception:" + e.toString());
			onTaskException(e);
		}
		endTransaction();
	}
	
	
	public void doCancel() {
		super.doCancel();
		if (mDataChannel != null) 
		{
			mDataChannel.cancelRequest(this);
		}
	}
	
	
	
}

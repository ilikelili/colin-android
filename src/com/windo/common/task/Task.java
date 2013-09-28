

package com.windo.common.task;

import com.windo.common.pal.internal.PalLog;


public abstract class Task
{
	
	private static short _TASK_ID = 0;
	
	
	protected int mTaskType;
	
	
	private short mTaskID;
	
	
	private boolean isCancel;
	
	
	private TaskEngine mTaskEngine;
	
	
	private TaskWatcher mWatcher;
	
	 
	
	protected Task(TaskEngine taskEngine, int type) {
		mTaskType = type;
    	mTaskID = getNextTaskId();
    	mTaskEngine = taskEngine;
	}

	
    public abstract void onTask();
    
    
    
    public void onDestroy(){
    	if(PalLog.DEBUG){
    		PalLog.d("Task", "@@@@@@@@@@  onDestroy:" + this);	
    	}
    }
    
    
    protected void onTaskException(Exception e){
    	
    }
    
	
	
	public TaskEngine getTaskEngine()
	{		
		return mTaskEngine;
	}
	
	
	public TaskWatcher getWatcher() {
		return mWatcher;
	}
	
	
	public void setWatcher(TaskWatcher watcher) {
		mWatcher = watcher;
	}


	
    public short getId() 
    {
        return mTaskID;
    }
    
    
    
    public int getType() {
    	return mTaskType;
    }
    
	
    
    public boolean isCancel() {
    	
    	return isCancel;
    }
    
    
    public void doCancel() {
    	isCancel = true;
    	if(PalLog.DEBUG){
    		PalLog.d("Task", "@@@@@@@@@@  doCancel");
    	}
   

    }
    
    
    protected final void endTransaction(){
    	try {
    		
    		if(PalLog.DEBUG){
        		PalLog.d("Task", "@@@@@@@@@@  endTransaction onDestroy:" + this);	
        	}
    		onDestroy();	
		} catch (Exception e) {
			
			e.printStackTrace();
			PalLog.e("Task", "onDestroy Exception:" + e.getMessage());
		}
    	mTaskEngine.endTask(this);
    	notifyComplete();
    }
    
    
    
    
    public void run(){
    	try {
    		if(!isCancel()){
    			onTask();	
    		}
		} catch (Exception e) {
			
			e.printStackTrace();
			PalLog.d("Task", "onTask Exception:" + e.toString());
			onTaskException(e);
		}
    	endTransaction();
    }
    
    
    
    protected void notifyMessage(int msgCode){
    	notifyMessage(msgCode,null);
    }
    
    
    protected void notifyMessage(int msgCode,int arg1){
    	notifyMessage(msgCode,arg1,null);
    }
    
    
    protected void notifyMessage(int msgCode,Object arg){
    	notifyMessage(msgCode,getType(),arg);
    }
    
    
    protected void notifyMessage(int msgCode,int arg1,Object arg) 
    {
    	if (mWatcher != null && !isCancel()) {
    		mWatcher.onTaskMessage(getId(),msgCode,arg1, arg);
    	}
    }
    
    
    
    protected void notifyError(int errCode){
    	notifyError(errCode,null);
    }
    
    
    protected void notifyError(int errCode,int arg1){
    	notifyError(errCode,arg1,null);
    }
    
    
    protected void notifyError(int errCode,String errMsg){
    	notifyError(errCode,getType(),errMsg);
    }
    
    
    
    
    public void notifyError(int errCode,int arg1,Object arg) {
    	if (mWatcher != null && !isCancel()) {
    		mWatcher.onTaskError(getId(),errCode,arg1, arg);
    	}
    }
    
    
    public void notifyComplete(){
    	if (mWatcher != null) {
    		mWatcher.onTaskComplete(getId());
    	}
    }

    
    
    private synchronized static short getNextTaskId(){
    	if (_TASK_ID >= 999)
		{
    		_TASK_ID = 0;
		}
		return _TASK_ID++;
    }
}

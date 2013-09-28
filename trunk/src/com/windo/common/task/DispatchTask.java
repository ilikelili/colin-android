package com.windo.common.task;



public class DispatchTask extends Task
{
	
	
	short mDispatchID;
	
	byte[] mData;
	
	public DispatchTask(TaskEngine transMgr,
			short tid,  byte[] data)
	{
		super(transMgr, -1);
		mDispatchID = tid;
		mData = data;
		
	}

	public void onTask()
	{
		
		TaskEngine engine = getTaskEngine();
		engine.dispatchIncomingPrimitive(mDispatchID, mData);
	}

	public void onTaskException(Exception e) {
		
		TaskEngine engine = getTaskEngine();
		engine.dispatchException(mDispatchID, e);
	}

	@Override
	public void onDestroy() {
		
		
	}

}

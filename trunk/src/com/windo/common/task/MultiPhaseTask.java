package com.windo.common.task;

import com.windo.common.pal.internal.PalLog;


public abstract class MultiPhaseTask extends AsyncTask
{
	
	public static final boolean TASK_COMPLETED = true;
	
	public static final boolean TASK_CONTINUE = false;
	
	protected int phase;
	
	protected MultiPhaseTask(TaskEngine transMgr, int type)
	{
		super(transMgr, type);
	}
	
	
	public void setPhase(int phase){
		this.phase = phase;
	}
	
	
	public void nextphase(int phase){
		this.phase = phase;
		reassignTask();
	}
	
	
	public int getPhase(){
		return phase;
	}
	
	
	protected void dispatchResponseError(int errCode,byte[] err){
		boolean completed = TASK_COMPLETED;
		try {
			completed = onResponseError(errCode,err);	
		} catch (Exception e) {
			
			e.printStackTrace();
			PalLog.d("MultiPhaseTask", "dispatchResponseError Exception:" + e.toString());
			onTaskException(e);
		}
		
		if(completed || isCancel()){
			endTransaction();
		}
	}
	
	
	protected void dispatchResponseSuccess(byte[] response)
	{
		
		boolean completed = TASK_COMPLETED;
		try {
			completed = onResponseSuccess(response);	
		} catch (Exception e) {
			
			e.printStackTrace();
			PalLog.d("MultiPhaseTask", "dispatchResponseSuccess Exception:" + e.toString());
			onTaskException(e);
		}
		
		if(completed || isCancel()){
			endTransaction();
		}
	}
	
	
	
	
}

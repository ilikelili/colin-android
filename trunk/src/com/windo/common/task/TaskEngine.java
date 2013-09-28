package com.windo.common.task;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import com.windo.common.pal.internal.PalLog;
import com.windo.common.util.LinkedBlockingQueue;


public class TaskEngine 
{
	
	static final String TAG = "WD-TASKEngine";


	
	
	
	HashMap<Short,Task> mTaskMap;

	
	LinkedBlockingQueue mTaskQueue;
	
	
	List<Task>	mRunningTaskList;
	
	List<Task> mAlreadyRunTaskList;
	


	
	
	boolean mStop = false;	
	
	
	int mThreadNum = 1;

	List<Thread> threadPool;


	
	PalLog mLog;

	public TaskEngine(int threadNum)
	{
		
		mTaskQueue = new LinkedBlockingQueue();
		mTaskMap = new HashMap<Short,Task>();
		mRunningTaskList = new Vector<Task>();
		mAlreadyRunTaskList = new Vector<Task>();
		if(threadNum > 0 ){
			mThreadNum = threadNum;	
		}
	}
	
	
	public void addTask(Task tx)
	{
		if (tx != null)
		{	
			synchronized (mTaskMap)
			{
				mTaskQueue.put(tx);
				mTaskMap.put(tx.getId(), tx);
				if (threadPool == null)
				{
					start();
				}
			}
		}
	}
	
	
	
	protected void endTask(Task tx){
		synchronized (mTaskMap) {
			if (mTaskMap.containsKey(tx.getId())){
				Task task = (Task) mTaskMap.remove(tx.getId());
				if(mAlreadyRunTaskList.contains(task)){
					mAlreadyRunTaskList.remove(task);
				}
			}
		}
	}
	
	
	public void cancelTask(short tId)
	{
		
		synchronized (mTaskMap)
		{
			if (mTaskMap.containsKey(tId))
			{	
				Task task = mTaskMap.get(tId);
				task.doCancel();
				
				
				if(mAlreadyRunTaskList.contains(task)){
					
					
					if(!mRunningTaskList.contains(task)){
						mAlreadyRunTaskList.remove(task);
						mTaskMap.remove(tId);
						try {
							if(PalLog.DEBUG){
					    		PalLog.d("TaskEngine cancel", "@@@@@@@@@@  onDestroy");	
					    	}
							task.onDestroy();	
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}else{
					
					mTaskQueue.remove(task);
					mTaskMap.remove(tId);
				}
			}
		}
	}
	
	
	public void shutdown()
	{
		if (threadPool != null)
		{
			mStop = true;
			mTaskQueue.interrupt();
			mTaskQueue.clear();
			mTaskMap.clear();
			
			
			for (int i = 0; i < threadPool.size(); i++) {
				try {
					threadPool.get(i).interrupt();
				} catch (Exception e) {
				
				}
			}
			threadPool.clear();
			threadPool = null;
			
			
			for (int i = 0; i < mAlreadyRunTaskList.size(); i++) {
				try {
					mAlreadyRunTaskList.get(i).doCancel();
					mAlreadyRunTaskList.get(i).onDestroy();	
				} catch (Exception e) {
					
				}
			}
			
			mAlreadyRunTaskList.clear();
			mRunningTaskList.clear();
			
		}
	}
	
	
	private void start()
	{
		
		if(threadPool == null){
			threadPool = new Vector<Thread>();
			for (int i = 0; i < mThreadNum; i++) {
				Thread thread = new Thread(new TaskProcessor());
				thread.setName("Task Processor " + i);
				thread.start();
				threadPool.add(thread);
			}
		}

	}
	
	
	public void dispatchIncomingPrimitive(short tId,byte[] response)
	{
		AsyncTask tx  = null;
		synchronized (mTaskMap) {
			if(mTaskMap.containsKey(tId)){
				tx = (AsyncTask)mTaskMap.get(tId);
			}
		}
		if(tx != null){
			synchronized (mTaskMap) {
				mRunningTaskList.add(tx);
			}
			tx.dispatchResponseSuccess(response);	
			synchronized (mTaskMap) {
				mRunningTaskList.remove(tx);
			}
		}
	}
	
	
	public void dispatchError(short tId,int errCode, byte[] errStr)
	{
		AsyncTask tx  = null;
		synchronized (mTaskMap) {
			if(mTaskMap.containsKey(tId)){
				tx = (AsyncTask)mTaskMap.get(tId);
			}	
		}
		if(tx != null){
			tx.dispatchResponseError(errCode, errStr);
		}
	}
	
	public void dispatchDestroy(short tId){
		AsyncTask tx  = null;
		synchronized (mTaskMap) {
			if(mTaskMap.containsKey(tId)){
				tx = (AsyncTask)mTaskMap.get(tId);
			}	
		}
		if(tx != null){
			tx.onDestroy();
		}
	}
	
	
	
	public void dispatchException(short tId,Exception e)
	{
		AsyncTask tx  = null;
		synchronized (mTaskMap) {
			if(mTaskMap.containsKey(tId)){
				tx = (AsyncTask)mTaskMap.get(tId);
			}
		}
		if(tx != null){
			tx.onTaskException(e);
		}
	}

	
	
	
	public void reassignTaskId(short taskID)
	{
		synchronized (mTaskMap)
		{	
			if (mTaskMap.containsKey(taskID))
			{
				Task tx = (Task) mTaskMap.get(taskID);
				if(!mTaskQueue.contains(tx)){
					mTaskQueue.put(tx);	
				}
			}
		}
	}
	
	
	
	public class TaskProcessor implements Runnable {

		@Override
		public void run() {
			
			while (!mStop)
			{
				Object obj = mTaskQueue.take();
				if (obj != null)
				{
					Task runTask = (Task) obj;
					
					synchronized (mTaskMap) {
						mRunningTaskList.add(runTask);
						
						if(!mAlreadyRunTaskList.contains(runTask)){
							mAlreadyRunTaskList.add(runTask);	
						}
					}
					try {
						runTask.run();	
					} catch (Exception e) {
						
						e.printStackTrace();
						endTask(runTask);
					}finally{
						synchronized (mTaskMap) {
							mRunningTaskList.remove(runTask);
						}
					}
				}
			}
		}
	} 
	


	
	
}

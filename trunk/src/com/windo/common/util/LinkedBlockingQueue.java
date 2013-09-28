package com.windo.common.util;

import java.util.Vector;

public class LinkedBlockingQueue {

	private Vector mQueue;
	
	public LinkedBlockingQueue(){
		mQueue = new Vector();
	}
	
	
	public Object take()  {
		synchronized (mQueue) {
			if(mQueue.size() > 0){
				return poll();
			}else{
				try {
					mQueue.wait();	
				} catch (Exception e) {
					
				}
				return poll();	
			}
		}
	}
	
	
	public Object poll(){
		synchronized (mQueue) {
			if(mQueue.size() > 0){
				Object obj = mQueue.elementAt(0);
				mQueue.removeElementAt(0);
				return obj;
			}else{
				return null;
			}
		}
	}
	
	
	public Object peek(){
		synchronized (mQueue) {
			if(mQueue.size() > 0){
				return mQueue.elementAt(0);
			}else{
				return null;
			}
		}
	}
	
	
	public void put(Object o) {
		if(o == null) return ;
		synchronized (mQueue) {
			mQueue.addElement(o);
			mQueue.notifyAll();
		}
	}
	
	public boolean remove(Object o){
		synchronized (mQueue) {
			return mQueue.removeElement(o);
		}
	}
	
	
	public int getSize(){
		return mQueue.size();
	}
	
	public void interrupt(){
		synchronized (mQueue) {
			mQueue.notifyAll();	
		}
	}
	
	public boolean contains(Object elem)
	{
		synchronized (mQueue) {
			return mQueue.contains(elem);	
		}
	}
	
	public void clear(){
		synchronized (mQueue) {
			mQueue.removeAllElements();
			mQueue.notifyAll();
		}
	}
	
	public void block(){
		synchronized (mQueue) {
			try {
				mQueue.wait();	
			} catch (Exception e) {
				
			}
		}
	}
	
}

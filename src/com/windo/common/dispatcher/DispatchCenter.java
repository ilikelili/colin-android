package com.windo.common.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.windo.common.pal.internal.PalLog;

/**
 * 派发中心
 * 
 * @author houmiao.xiong
 * 
 * @time 2013-10-21 下午5:15:38
 */
public class DispatchCenter implements IDispatch{
	
	private static final String TAG = "DispatchCenter";
	
	Dispatcher[] tmpDispatchArray = new Dispatcher[10];
	
	Map<Object, List<Dispatcher>> mDispatcherMap = new HashMap<Object, List<Dispatcher>>();
	
	
	public DispatchCenter() {
		
	}

	@Override
	public void addDispatcher(Object key, Dispatcher dis) {
		// TODO Auto-generated method stub
		if (dis == null) {
			return;
		}

		final Map<Object, List<Dispatcher>> map = mDispatcherMap;

		synchronized (map) {
			List<Dispatcher> dispatcherList = null;

			if (map.containsKey(key)) {
				dispatcherList = map.get(key);
				if (!dispatcherList.contains(dis)) {
					dispatcherList.add(dis);
				}
			} else {
				dispatcherList = new ArrayList<Dispatcher>();
				dispatcherList.add(dis);
				map.put(key, dispatcherList);
			}
		}
	}

	@Override
	public void removeDispatcher(Object key, Dispatcher dis) {
		// TODO Auto-generated method stub
		final Map<Object, List<Dispatcher>> map = mDispatcherMap;
		if(map == null){
			return;
		}
		
		synchronized (map) {
			if (map.containsKey(key)) {
				List<Dispatcher> dispatcherList = map.get(key);
				if (dispatcherList.contains(dis)) {
					dispatcherList.remove(dis);
				}
			} 
		}
	}

	@Override
	public void clearDispatcher(Object key) {
		// TODO Auto-generated method stub
		final Map<Object, List<Dispatcher>> map = mDispatcherMap;
		if(map != null){
			synchronized (map) {
				if (map.containsKey(key)) {
					map.remove(key);
				}
			}
		}
	}
	
	
	@Override
	public void clearAll(){
		if(mDispatcherMap != null){
			mDispatcherMap.clear();
			mDispatcherMap = null;
		}
	}

	@Override
	public void dispatchMessage(Object key, int msgCode, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		final Map<Object, List<Dispatcher>> map = mDispatcherMap;

		if (map.containsKey(key)) {
			List<Dispatcher> dispatcherList = map.get(key);
			Dispatcher[] dispatcher = dispatcherList.toArray(tmpDispatchArray);

			int size = dispatcherList.size();
			
			PalLog.d(TAG, "dispatchers size " + size);
			
			for (int i = 0; i < size; i++) {
				if (i >= dispatcher.length) {
					break;
				} else {
					if (dispatcher[i] != null) {
//						dispatcher[i].dispatcherMessage(msgCode, agr1, arg2, obj);
						dispatcher[i].onTaskMessage(arg1, msgCode, arg2, obj);
					}
				}
			}
			tmpDispatchArray = dispatcher;
		}
	}

	@Override
	public void dispatchError(Object key, int errCode, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		final Map<Object, List<Dispatcher>> map = mDispatcherMap;

		if (map.containsKey(key)) {
			List<Dispatcher> dispatcherList = map.get(key);
			Dispatcher[] dispatcher = dispatcherList.toArray(tmpDispatchArray);

			int size = dispatcherList.size();
			
			PalLog.d(TAG, "dispatchers size " + size);
			
			for (int i = 0; i < size; i++) {
				if (i >= dispatcher.length) {
					break;
				} else {
					if (dispatcher[i] != null) {
//						dispatcher[i].dispatcherError(errCode, arg1, arg2, obj);
						dispatcher[i].onTaskError(arg1, errCode, arg2, obj);
					}
				}
			}
			tmpDispatchArray = dispatcher;
		}
	}

}

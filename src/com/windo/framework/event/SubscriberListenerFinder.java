package com.windo.framework.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.windo.framework.event.common.EventListener;




/**
 * @package de.greenrobot.event
 * @file SubscriberListenerFinder.java
 * @version V1.0
 * @author Hawkes hawkty@gmail.com
 * @createdata 2013-4-18-下午5:08:08
 * @description 
 */
public class SubscriberListenerFinder {
	private static final Map<Object, SubscriberListener> subscriberListenerMap = new ConcurrentHashMap<Object,SubscriberListener>();

	SubscriberListener findSubscriberListener(Object subscriber,  EventListener listener, Class<?> eventType){
		SubscriberListener slistener = subscriberListenerMap.get(subscriber);
		if(slistener == null){
			slistener = new  SubscriberListener(listener, subscriber, eventType);
			subscriberListenerMap.put(subscriber, slistener);
		}
		
		return slistener;
	}
	
	 static void clearCaches() {
		 subscriberListenerMap.clear();
	 }
}

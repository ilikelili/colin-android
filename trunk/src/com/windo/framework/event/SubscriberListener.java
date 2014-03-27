package com.windo.framework.event;

import com.windo.framework.event.common.EventListener;




/**
 * @package de.greenrobot.event
 * @file SubscriberListener.java
 * @version V1.0
 * @author Hawkes hawkty@gmail.com
 * @createdata 2013-4-18-下午3:58:52
 * @description 
 */
public class SubscriberListener {
		
		final EventListener listener;
	    final Object subscriber;
	    final Class<?> eventType;
	    public ThreadMode mode = ThreadMode.Async;
	    

		SubscriberListener(EventListener listener, Object subscriber, Class<?> eventType) {
	        this.listener = listener;
	        this.subscriber = subscriber;
	        this.eventType = eventType;
	    }

	    @Override
	    public boolean equals(Object other) {
	        if (other instanceof SubscriberListener) {
	            // Don't use method.equals because of http://code.google.com/p/android/issues/detail?id=7811#c6
	            return subscriber.equals(((SubscriberListener) other).subscriber)
	            		&&listener.equals(((SubscriberListener) other).listener);
	        } else {
	            return false;
	        }
	    }

	 
	    @Override
	    public int hashCode() {
	        return subscriber.hashCode() + listener.hashCode();
	    }
}

package com.windo.framework.event.common;

import java.io.Serializable;

/**
 * @package com.pitaya.framework.event.common
 * @file Event.java
 * @version V1.0
 * @author Hawkes hawkty@gmail.com
 * @createdata 2013-4-23-上午9:59:31
 * @description 
 */
public class Event implements Serializable{
	
	/**
	 * serialVersionUID:TODO
	 */
	private static final long serialVersionUID = 1L;
	
	public String evenType;
	public Object object;
}

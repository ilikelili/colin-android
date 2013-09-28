package com.windo.common.pal;


public interface ILog {

	
	public static final int VERBOSE = 2;

	
	public static final int DEBUG = 3;

	
	public static final int INFO = 4;

	
	public static final int WARN = 5;

	
	public static final int ERROR = 6;

	
	public static final int ASSERT = 7;
	
	
	public  void d(String tag, String msg) ;

	
	public  void v(String tag, String msg) ;

	
	public  void e(String tag, String msg) ;

	
	public void i(String tag, String msg) ;

	
	public  void w(String tag, String msg) ;
	
	
}

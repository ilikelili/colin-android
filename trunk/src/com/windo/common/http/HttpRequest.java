package com.windo.common.http;

import java.util.Hashtable;

public class HttpRequest
{
	
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";

	
	int mRequestID;
	
	boolean mIsCancel = false;
	
	String mUrl;
	
	String mMethod;
	
	Hashtable mHeader;
	
	
	
	byte[] mPostByteArray;
	
	String mMultiPartFile;
	
	HttpCallBack mCallback;

	
	boolean mStreamCallBack;
	
	public HttpRequest(String url){
		this(url,METHOD_GET);
	}
	
	public HttpRequest(String url,byte[] postData){
		
		this(url,METHOD_POST);
		mPostByteArray = postData;
	}
	
	
	public HttpRequest(String url,String method){
		this.mUrl = url;
		mMethod = method;
		mRequestID = HttpEngine.getNextRequestID();
		mIsCancel = false;
	}


	
	public int getRequestID()
	{
		return mRequestID;
	}
	
	
	
	public void addHeaderField(String key,String value)
	{
		if(mHeader == null){
			mHeader = new Hashtable();
		}
		mHeader.put(key, value);
	}
	
	
	public void setHeaderField(Hashtable table)
	{
		if(table != null){
			mHeader = table;
		}
	}
	
	
	public Hashtable getHeaderField()
	{
		return mHeader;
	}

	
	public String getMethod()
	{
		return mMethod;
	}
	
	public String getUrl()
	{
        if(mUrl!=null)
            mUrl.trim();
		return mUrl;	
	}
	
	
	public void postData(byte[] postdata)
	{
		mPostByteArray = postdata;
		if(postdata != null){
			mMethod = METHOD_POST;
		}
	}
	
	public void postMultiPartFile(String file)
	{
		mMultiPartFile = file; 
		if(file != null){
			mMethod = METHOD_POST;
		}
	}
	
	public void postMultiPartFile(String filePath, byte[] data)
	{
		mMultiPartFile = filePath;
		mPostByteArray = data;
		if(filePath != null){
			mMethod = METHOD_POST;
		}
	}

	public void setHttpCallBack(HttpCallBack callback)
	{
		mCallback = callback;
	}
	
	public HttpCallBack getHttpCallBack()
	{
		return mCallback;
	}
	
	public String getMultiPartFile()
	{
		return mMultiPartFile;
	}
	
	public byte[] getMultiPartData() {
		return mPostByteArray;
	}
	
	public void setRequestMethod(String method)
	{
		mMethod = method;
	} 

	
	public byte[] getPostData()
	{
		return mPostByteArray;
	}

	
	public void doCancel(){
		mIsCancel = true;	
	}
	
	public boolean isCancel()
	{
		return mIsCancel;
	}

	public void setStreamCallBack(boolean streamCallBack) {
		mStreamCallBack = streamCallBack;
	}
	
	public boolean isStreamCallBack() {
		return mStreamCallBack;
	}
	
	
	
	
	private Hashtable mMultiPartParams;
	public void setMultiPartParams(String key, String value){
		if(mMultiPartParams == null)
			mMultiPartParams = new Hashtable();
		
		mMultiPartParams.put(key, value);
	}
	public Hashtable getMultiPartParams(){
		return mMultiPartParams;
	}
	
}

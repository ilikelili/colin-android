package com.windo.common.http;

import java.io.InputStream;

import com.windo.common.pal.IHttp;

public interface HttpCallBack
{
	
	public void onError(int requestId, int errCode, byte[] errStr,IHttp http);
	
	public void onReceived(int requestId, byte[] data, IHttp http);
	
	public void onReceived(int requestId, InputStream stream, long contentLength, IHttp http);	
}

package com.windo.common.pal.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.windo.common.http.HttpRequest;
import com.windo.common.pal.IHttp;



public class PalHttp implements IHttp
{
	
	
	private final String TAG = "PAL_HTTP";
	
	static Random mBoundaryRandom = new Random();
	
	boolean isWap;
	
	int   mTimeOut;
	
	String mUrl;
	
	String mMethod;
	
	String mMultiPartFile;
	
	Hashtable<String,String> mMultiParams;
	
	Hashtable<String,String> mRequestProperty;

	HttpClient mHttpClient;
	
	HttpResponse mHttpResponse;

	byte[] mPostData;
	
	public static IHttp createHttp(String url, String method,
			boolean isWap,HttpClient httpClient) {
		
		if( httpClient == null){
			return null;
		}
		PalHttp http = new PalHttp();
		http.mHttpClient = httpClient;
		http.isWap = isWap;
		http.open(url);
		http.setRequestMethod(method);
		return http;
	}
	
	private void buildHttpClient() {
		





		
	}

	
	private HttpResponse httpGet() throws IOException{
		PalLog.d(TAG,  "[GET URL:]"+mUrl);
		buildHttpClient();
		HttpGet httpGet = new HttpGet(mUrl);
		HttpResponse response = null;
		if(mRequestProperty != null && mRequestProperty.size() > 0 ){
			Enumeration e = mRequestProperty.keys();
			while (e.hasMoreElements())
			{
				String key = (String) e.nextElement();
				String value = (String) mRequestProperty.get(key);
				httpGet.setHeader(key, value);
			}
		}
		response = mHttpClient.execute(httpGet);	
		return response;

	}
	
	private HttpResponse httpPost() throws IOException{
		PalLog.d(TAG,  "[POST URL:]"+mUrl);
		
		buildHttpClient();
		HttpEntity entry = null;
		HttpResponse response = null;
		HttpPost httpPost = new HttpPost(mUrl);
		if(mRequestProperty != null && mRequestProperty.size() > 0 ){
			Enumeration e = mRequestProperty.keys();
			while (e.hasMoreElements())
			{
				String key = (String) e.nextElement();
				String value = (String) mRequestProperty.get(key);
				httpPost.setHeader(key, value);
			}
		}
		if(mPostData != null){
			entry = new ByteArrayEntity(mPostData);
		}
		else if (mMultiPartFile != null) {
			String boundary = getBoundary();
			
			PalFile file = new PalFile(mMultiPartFile, true);

			StringBuffer header = new StringBuffer();
			header.append("--");
			header.append(boundary);
			header.append("\r\n");
			header.append("Content-Disposition: form-data; ");
			if(mMultiParams != null && mMultiParams.size() > 0){
				Enumeration e = mMultiParams.keys();
				while (e.hasMoreElements())
				{
					String key = (String) e.nextElement();
					String value = (String) mMultiParams.get(key);
					header.append(key + "=" + value + "; ");
				}
			}
			else{
				header.append("name=\"pic\"; ");
			}
			header.append("filename=\"" + file.getName() + "\"\r\n");
			header.append("Content-Type: application/octet-stream;\r\n");
			header.append("Content-Transfer-Encoding: binary\r\n");
			header.append("\r\n");

			try{
    			byte[] head_data = header.toString().getBytes();
    			byte[] end_data = ("\r\n--" + boundary + "--\r\n").getBytes();
    			
    			httpPost.setHeader("Content-Type", 
    					"multipart/form-data; boundary=" + boundary);
    			InputStream in = file.openInputStream();
    			sequenceMutiTypeEntity sequenceentry = new sequenceMutiTypeEntity();
    			sequenceentry.addByteArray(head_data, -1);
    			sequenceentry.addInputStream(in, (int)file.getSize());
    			sequenceentry.addByteArray(end_data, -1);
    			entry = sequenceentry;
			}
			catch (IOException e){
				e.printStackTrace();
				throw e;
			}
		} 
		
		httpPost.setEntity(entry);
		response = mHttpClient.execute(httpPost);
		return response;
	}
	
	
	public int execute() throws IOException
	{
		if (mMethod.equals(HttpRequest.METHOD_POST)) {
			mHttpResponse = httpPost();
		} else {
			mHttpResponse = httpGet();
		}
	
		if(mHttpResponse != null && mHttpResponse.getStatusLine() != null){		
			PalLog.d(TAG, "ResponseCode:"+mHttpResponse.getStatusLine().getStatusCode());
			return mHttpResponse.getStatusLine().getStatusCode();
		}
		else{
			return -1;
		}		
	}

	private String getBoundary() {
		return String.valueOf(System.currentTimeMillis()) 
				+ String.valueOf(Math.abs(mBoundaryRandom.nextLong()));
	}
	
	public String getHeaderField(int n) throws IOException
	{
		if(mHttpResponse != null){
			Header head[] = mHttpResponse.getAllHeaders();
			if(head != null && head.length >= n)
				return head[n].getValue();
		}
		
		return null;
	}

	public String getHeaderField(String name) throws IOException
	{
		
		if(mHttpResponse != null){
			Header head = mHttpResponse.getFirstHeader(name);	
			if(head != null)
				return head.getValue();
		}
		
		return null;
	}

	public String getHeaderFieldKey(int n) throws IOException
	{
		if(mHttpResponse != null){
			Header head[] = mHttpResponse.getAllHeaders();
			if(head != null && head.length >= n)
				return head[n].getName();
		}
		
		return null;
	}

	public int getResponseCode()
	{
		if(mHttpResponse != null && mHttpResponse.getStatusLine() != null )
			return mHttpResponse.getStatusLine().getStatusCode();
		
		return -1;
	}

	public String getResponseMessage() throws IOException
	{
		if(mHttpResponse != null)
			return EntityUtils.toString(mHttpResponse.getEntity());
		
		return null;
	}

	public void open(String url)
	{
		
		mUrl = url;
	}
	
	
	public String getURL(){
		return mUrl;
	}

	public DataInputStream openDataInputStream() throws IOException
	{
		return null;
	}

	public DataOutputStream openDataOutputStream() throws IOException
	{
		return null;
	}

	public InputStream openInputStream() throws IOException
	{
		
		if(mHttpResponse != null && mHttpResponse.getEntity() != null){
			return mHttpResponse.getEntity().getContent();
		}
		else{
			return null;
		}
	}

	public OutputStream openOuputStream() throws IOException
	{
		return null;
	}

	public void postMultiPart(String file)
	{
		postMultiPart(file, null);
	}
	
	public void postMultiPart(String file, Hashtable param) {
		mMultiPartFile = file;
		mMultiParams = param;
	}
	
	public void postByteArray(byte[] byteArray)
	{
		mPostData = byteArray;
	}

	public void setRequestMethod(String method)
	{
		
		mMethod = method;
	}

	public void setRequestProperty(String key, String value)
	{
		
		if(mRequestProperty == null){
			mRequestProperty = new Hashtable();
		}
		mRequestProperty.put(key, value);
	}

	public long getContentLength()
	{
		if(mHttpResponse != null){
			HttpEntity entity = mHttpResponse.getEntity();
			if(entity != null)
				return entity.getContentLength();
		}
		
		return 0;
	}

	
	public void setTimeout(int timeout) {
		mTimeOut = timeout;
	}

	static public boolean isWap(Context context)
	{
		ConnectivityManager cm =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		if(nInfo == null || nInfo.getType() != ConnectivityManager.TYPE_MOBILE)
			return false;
		String extraInfo = nInfo.getExtraInfo();
		if(extraInfo == null || extraInfo.length() < 3)
			return false;
		if(extraInfo.toLowerCase().contains("wap"))
			return true;
		return false;
	}

	@Override
	public void close() throws IOException {
		
		if(mHttpResponse != null && mHttpResponse.getEntity() != null){
		
			try {
				InputStream in = mHttpResponse.getEntity().getContent();
				if(in != null){
					in.close();
				}				
			} catch (Exception e) {
				
			}
		}
	}


}

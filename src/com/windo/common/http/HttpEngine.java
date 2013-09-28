
package com.windo.common.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;

import com.windo.common.pal.IHttp;
import com.windo.common.pal.internal.PalHttp;
import com.windo.common.pal.internal.PalLog;
import com.windo.common.util.LinkedBlockingQueue;

public class HttpEngine implements Runnable {

	public static final String TAG = "HttpEngine";

	public static final int ERR_NETWORK_SECURITY = 0x1;
	
	public static final int ERR_NETWORK_DONT_CONNECT = 0x2;
	
	public static final int ERR_NETWORK_OTHER = 0x3;
	
	public static final int ERR_NETWORK_CANCEL = 0x4;

	private static final int MAX_RETRY_COUNT = 3; 
	private static final int NET_BUFFER = 1 * 1024; 
	private static final int NET_TIMEOUT = 10 * 1000; 

	LinkedBlockingQueue mReqeustQueue; 
	Thread mThread; 
	boolean mStop = false; 
	static HttpEngine mEngine;
	static int mReqeustNum = 0;

	

	Context mContext;

	HttpClient mHttpClient;


	public HttpEngine(Context context) {
		mReqeustQueue = new LinkedBlockingQueue();
		mContext = context;
	}

	public void addRequest(HttpRequest request) {
		mReqeustQueue.put(request);
		if (mThread == null) {
			mThread = new Thread(this);
			mThread.start();
		}
	}

	
	public void shutdown() {
		if (mThread != null) {
			mStop = true;
			
			if(mHttpClient != null){
				mHttpClient.getConnectionManager().shutdown();
				mHttpClient = null;
			}
			mReqeustQueue.interrupt();
			mThread.interrupt();
			mThread = null;
	
		}
	}


	
	public void run() {

		PalLog.d(TAG, "Http Engine start");

		while (!mStop) {
			Object obj = mReqeustQueue.take();

			if (obj != null) {
				HttpRequest request = (HttpRequest) obj;
				long start = System.currentTimeMillis();
				int interval = 0;
				int times = 0;
				while (!mStop && !request.isCancel()) {
					
					try {
						long t = System.currentTimeMillis();
						int code = doHttpReqeust(request);
						if(PalLog.DEBUG){
							PalLog.d(TAG, "Http Time:"  + (System.currentTimeMillis() - t));
						}
						if (code != -1) {	
							break;
						}
					} catch (Exception e) {
						
						interval = (int)(System.currentTimeMillis() - start);
						if(times >= MAX_RETRY_COUNT  || interval >= NET_TIMEOUT){	
							handleHttpException(request,e);
							break;
						}else{
							times ++;
						}
					}
				}
				
				if (isCancel(request)) 
				{
					notifyError(request, ERR_NETWORK_CANCEL, null, null);
				} 
			}
			obj = null;
		}
		PalLog.d(TAG, "Http Engine End");
	}
	
	
	private void handleHttpException(HttpRequest request, Exception e){
		if(PalLog.DEBUG){
			e.printStackTrace();	
		}
		if(e instanceof IOException){
			notifyError(request, ERR_NETWORK_DONT_CONNECT, null,null);
		}else if(e instanceof SecurityException){
			notifyError(request, ERR_NETWORK_SECURITY, null,null);
		}else{
			notifyError(request, ERR_NETWORK_OTHER, null,null);
		}
	}

	
	private int doHttpReqeust(HttpRequest request) throws Exception {
		IHttp http = null;
		try {
			http = trySend(request);
			int reponseCode = http.getResponseCode();
			if (isCancel(request)) {
				return reponseCode;
				
			} else {
				String type = http.getHeaderField("Content-Type");
				if (type != null && type.indexOf("vnd.wap.wml") >= 0) {
					PalLog.d(TAG, "Content-Type:" + type);
					return -1;
				} else {
					if (request.isStreamCallBack() && reponseCode == 200) {
						InputStream in = null;
						try {
							in = http.openInputStream();
							notifyReceived(request, in,
									http.getContentLength(),
									http);
						} finally {
							if (in != null) {
								in.close();
								in = null;
							}
						}
					} else {

						if (!isCancel(request)) {

							if (reponseCode == 200) {
								byte[] data = readData(http, request);
								notifyReceived(request, data, http);
							} else {
								notifyError(request, reponseCode, null,
										http);
							}
						}
					}
				}
			}

			return reponseCode;
		} catch (Exception e) {
			
			throw e;
		} finally {
			if(http != null){
				http.close();
			}
			http = null;
		}
	}
	

	
	private IHttp trySend(HttpRequest request) throws IOException {

		if(mHttpClient == null){
			mHttpClient = createHttpClient();
		}
		
		String url = request.getUrl();
        url = url.trim();
		if(!url.startsWith("https://") && !url.startsWith("http://")){
			url = "http://" + url;
		}
		boolean isWap = PalHttp.isWap(mContext);
		IHttp http = PalHttp.createHttp(url, request.getMethod(),isWap , mHttpClient);
		http.setTimeout(NET_TIMEOUT / MAX_RETRY_COUNT);
		
		try {
			
			
			Hashtable<String,String> header = request.getHeaderField();
			if (header != null && header.size() > 0) {
				Enumeration<String> enu = header.keys();
				while (enu.hasMoreElements()) {
					String key = (String) enu.nextElement();
					String value = (String) header.get(key);
					http.setRequestProperty(key, value);
				}
			}

			if (request.getMethod().equals(HttpRequest.METHOD_POST)) {

				if (request.getPostData() != null) {
					http.postByteArray(request.getPostData());
				}
				if (request.getMultiPartFile() != null) {
					String file = request.getMultiPartFile();
					Hashtable t = request.getMultiPartParams();
					http.postMultiPart(file, t);
				}
			}

			http.execute();

		} catch (SecurityException e) {
			http.close();
			http = null;
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			http.close();
			http = null;

			PalLog.e(TAG, "HttpEngine trySend IOException");
			throw e;
		}

		return http;
	}



	private HttpClient createHttpClient() {

		HttpParams params = new BasicHttpParams(); 

		
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        
		ConnManagerParams.setTimeout(params, 1000);	
		
		HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
		
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 100*1024);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpProtocolParams.setContentCharset(params, "UTF8");
		SchemeRegistry schReg = new SchemeRegistry();
		
		schReg.register(new Scheme("http",
								   PlainSocketFactory.getSocketFactory(),
								   80));
		
		schReg.register(new Scheme("https",
						getSSLSocketFactory(),
								   443));
	
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params,
				 schReg);
		
		HttpClient client = new DefaultHttpClient(conMgr, params);
		client.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		
		return client;
	}
	
	
	
	private  SocketFactory getSSLSocketFactory(){
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory.setHostnameVerifier(new X509HostnameVerifier(){

			@Override
			public boolean verify(String host, SSLSession session) {
				
				return true;
			}

			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {
				
				
			}

			@Override
			public void verify(String host, X509Certificate cert)
					throws SSLException {
				
				
			}

			@Override
			public void verify(String host, String[] cns, String[] subjectAlts)
					throws SSLException {
				
				
			}
			
		});
		return socketFactory;
	}

	
	
	private byte[] readData(IHttp http, HttpRequest request) throws IOException {
		InputStream inStream = null;
		try {
			inStream = http.openInputStream();
			
			String encode = http.getHeaderField("Content-Encoding");
			String contentLen = http.getHeaderField("Content-Length");
			
			PalLog.d(TAG, "content encode " + encode);
			PalLog.d(TAG, "content length " + contentLen);
			
			ByteArrayOutputStream buff = new ByteArrayOutputStream();

			if(encode != null && encode.equalsIgnoreCase("gzip")){
				
				inStream = new GZIPInputStream(inStream);
				readAllData(request, inStream, buff);
				
			}else if (http.getHeaderField("Content-Length") != null) {

				int len = (int) http.getContentLength();
				readData(request, inStream, buff, len);
				
			} else {
				
				readAllData(request, inStream, buff);
				
			}
			
			return buff.toByteArray();

		} finally {
			if (inStream != null) {
				inStream.close();
				inStream = null;
			}
		}
	}
	
	private void readAllData(HttpRequest request, InputStream inStream, OutputStream buff) 
			throws IOException{
		int numread = 0;
		long timeout = System.currentTimeMillis();

		byte[] tmp = new byte[NET_BUFFER];

		while (!isCancel(request)) {
			numread = inStream.read(tmp, 0, tmp.length);
			long t = System.currentTimeMillis();
			if (numread < 0) {
				break;
			} else if (numread == 0) {
				if (t - timeout > 5000) {
					System.out.println("time out  > 5000");
					break;
				}
				sleep(50);
			} else {
				buff.write(tmp, 0, numread);
				timeout = t;
			}
		}
	}

	private void readData(HttpRequest request, InputStream in,
			OutputStream out, int preferLength) throws IOException {
		int numread = 0;
		int count = 0;
		byte[] data = new byte[1024];
		while (!isCancel(request) && count < preferLength) {
			numread = in.read(data, 0,
					Math.min(data.length, preferLength - count));

			if (numread == -1) { 
				throw new IOException();
			} else {
				out.write(data, 0, numread);
				count += numread;
			}
			sleep(10);
		}
	}

	
	private boolean isCancel(HttpRequest request) {
		return mStop || request.isCancel();
	}

	
	private void notifyReceived(HttpRequest request, byte[] data, IHttp http) {
		if (request.getHttpCallBack() != null) {
			request.getHttpCallBack().onReceived(request.getRequestID(), data,
					http);
		}
	}

	private void notifyReceived(HttpRequest request, InputStream stream,
			long contentLength, IHttp http) {
		if (request.getHttpCallBack() != null) {
			request.getHttpCallBack().onReceived(request.getRequestID(),
					stream, contentLength, http);
		}
	}

	
	private void notifyError(HttpRequest request, int errCode, byte[] msg,
			IHttp http) {
		if(http == null){
			http = PalHttp.createHttp(request.getUrl(), request.getMethod(),false , mHttpClient);
		} 
		if (request.getHttpCallBack() != null) {
			request.getHttpCallBack().onError(request.getRequestID(), errCode,
					msg, http);
		}
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			
		}
	}

	public static synchronized int getNextRequestID() {
		if(mReqeustNum >= Integer.MAX_VALUE){
			mReqeustNum = 0;
			return mReqeustNum;
		}else{
			return mReqeustNum++;	
		}
		
	}

}

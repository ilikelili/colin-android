package com.windo.common.download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 多线程文件下器
 * 
 * @author houmiao.xiong
 * 
 * @time 2013-11-4 下午11:21:28
 */
public class DownloadFile {

//	 public static final String URI = "http://file1.updrv.com/soft/2012/drivethelife5_setup.exe";
	 
	 static final String URI = "http://gdown.baidu.com/data/wisegame/d988aebb65d8a9db/tengxunweibo_51.apk";
	 
//	public static final String URI = "http://www.java2s.com/Code/JarDownload/apache/apache-commons-logging.jar.zip";
//	 public static final String URI = "http://www.chongdingxiang.com";

	/** 线程大小 */
	public static final int THREAD_SIZE = 5;
	
	static long startDownTime = 0;

	public static void main(String[] args) {

		try {
			startDownTime = System.currentTimeMillis();

			DownloadFile df = new DownloadFile();
			df.execute(URI);

//			df.log("load time: " + (System.currentTimeMillis() - startTime));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void execute(final String url) throws Exception {
		HttpClient client = createClient();
		HttpUriRequest req = createGetRequest(url, 0, 0);
		HttpResponse rsp = client.execute(req);

		int statesCode = rsp.getStatusLine().getStatusCode();
		if (statesCode == 200 || statesCode == 206) {
			
			File tmpFile = new File(getFileName(url) + ".tmp");
			File file = new File(getFileName(url));
			if(file.exists()){
				file.delete();
			}
			if(tmpFile.exists()){
				tmpFile.delete();
			}
			
			long conLen = rsp.getEntity().getContentLength();
			long average = conLen / THREAD_SIZE;
			log("average " + average);
			long startIndex = 0;
			long endIndex = 0;
			for (int i = 0; i < THREAD_SIZE; i++) {
				startIndex = i * average;
				endIndex = (i + 1) * average -1;
				
				if (i == 0){
//					s
				}else if(i == THREAD_SIZE -1){
					endIndex = conLen - 1;
				}
				
//				Ex
				new Thread(new DownloadRun(startIndex, endIndex, mComplete), "ThreadName "
						+ i).start();
			}
		}

	}
	
	int time;
	Lock lock = new ReentrantLock();
	
	private LoadComplete mComplete = new LoadComplete() {
		
		@Override
		public void onComplete() {
			// TODO Auto-generated method stub
			lock.lock();
			time++;
			lock.unlock();
			
			if(time == THREAD_SIZE){
				File tmpfile = new File(getFileName(URI) + ".tmp");
				File file = new File(getFileName(URI));
				boolean rename = tmpfile.renameTo(file);
				log("rename " + rename + " download complete");
				
				log("load over " + (System.currentTimeMillis() - startDownTime));
			}
		}
	};
	
	interface LoadComplete{
		
		public void onComplete();
		
	}

	class DownloadRun implements Runnable {

		long mStartIndex;
		long mEndIndex;
		
		LoadComplete mComplete;

		DownloadRun(long startIndex, long endIndex, LoadComplete complete) {
			mStartIndex = startIndex;
			mEndIndex = endIndex;
			mComplete = complete;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			log("startIndex " + mStartIndex + " endIndex " + mEndIndex + Thread.currentThread().getName());
			
			HttpClient client = createClient();
			HttpUriRequest req = createGetRequest(URI, mStartIndex, mEndIndex);
			try {
				HttpResponse rsp = client.execute(req);
				parseResponse(rsp, mStartIndex);
				
				if(mComplete != null){
					mComplete.onComplete();
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public HttpUriRequest createPostRequest(String uri, byte[] postData) {
		HttpPost postReq = new HttpPost(uri);
		postReq.setEntity(new ByteArrayEntity(postData));
		return postReq;
	}

	private HttpUriRequest createGetRequest(String url, long startIndex,
			long endIndex) {
		HttpUriRequest getReq = new HttpGet(url);
		addRangeHeader(getReq, startIndex, endIndex);
		return getReq;
	}
	
	private void addRangeHeader(HttpUriRequest req, long startIndex, long endIndex){
		String rangeValue = null;
		if (startIndex >= 0) {
			rangeValue = "bytes=" + startIndex + "-";
			if (endIndex > startIndex) {
				rangeValue += endIndex;
			}
		} else if (endIndex > 0) {
			rangeValue = "bytes=-" + endIndex;
		}

		if (rangeValue != null) {
			req.addHeader("Range", rangeValue);
		}
	}

	private HttpClient createClient() {
		HttpClient client = new DefaultHttpClient();
		return client;
	}

	private void parseResponse(HttpResponse rsp, long startIndex) throws Exception {
		int statesCode = rsp.getStatusLine().getStatusCode();
		log(Thread.currentThread().getName() + " statesCode " + statesCode);

		if (statesCode == HttpStatus.SC_OK || statesCode == 206) {
			HttpEntity entity = rsp.getEntity();

			long contentLen = entity.getContentLength();
			log(Thread.currentThread().getName() + " contentLen " + contentLen);

			InputStream is = entity.getContent();
			output2File(is, contentLen, startIndex);
		}
	}

	private void output2File(InputStream is, long contentLen, long startIndex) throws Exception {
		String tmpFileName = getFileName(URI) + ".tmp";
		log("tmpFileName " + tmpFileName);

		RandomAccessFile raf = new RandomAccessFile(tmpFileName, "rw");
		raf.seek(startIndex);

		String currThreadName = Thread.currentThread().getName();

		byte[] buf = new byte[1024 * 2];
		int len = -1;
		long sum = 0;
		while ((len = is.read(buf)) != -1 && sum <= contentLen) {
			raf.write(buf, 0, len);
			
			sum += len;
			log(currThreadName + " download len " + len);
		}

		log(currThreadName + " download over");

		raf.close();
		is.close();
	}

	private String getFileName(String url) {
		String fileName = null;
		int index = url.lastIndexOf("/");
		if (index >= 0) {
			fileName = url.substring(index + 1);
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss");
			fileName = sdf.format(new Date(System.currentTimeMillis()))
					+ "_loadfile";
		}
		return fileName;
	}

	private void log(String msg) {
		System.out.println(msg);
	}

}

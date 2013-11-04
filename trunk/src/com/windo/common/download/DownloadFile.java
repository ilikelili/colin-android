package com.xhm.fhlt.download;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

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
 * 多线程文件下载
 * 
 * @author houmiao.xiong
 * 
 * @time 2013-11-4 下午11:21:28
 */
public class DownloadFile {

	// public static final String url =
	// "http://file1.updrv.com/soft/2012/drivethelife5_setup.exe";
	public static final String URI = "http://www.java2s.com/Code/JarDownload/apache/apache-commons-logging.jar.zip";
	// public static final String url = "http://www.chongdingxiang.com";

	/** 线程大小 */
	public static final int THREAD_SIZE = 3;

	public static void main(String[] args) {

		try {
			long startTime = System.currentTimeMillis();

			DownloadFile df = new DownloadFile();
			df.execute(URI);

			df.log("load time: " + (System.currentTimeMillis() - startTime));

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
			long conLen = rsp.getEntity().getContentLength();
			int average = (int) (conLen / THREAD_SIZE);
			log("average " + average);
			for (int i = 0; i < THREAD_SIZE; i++) {
				int startIndex = i * average;
				int endIndex = (i + 1) * average;
				new Thread(new DownloadRun(startIndex, endIndex), "ThreadName "
						+ i).start();
			}
		}

	}

	class DownloadRun implements Runnable {

		int mStartIndex;
		int mEndIndex;

		DownloadRun(int startIndex, int endIndex) {
			mStartIndex = startIndex;
			mEndIndex = endIndex;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpClient client = createClient();
			HttpUriRequest req = createGetRequest(URI, mStartIndex, mEndIndex);
			try {
				HttpResponse rsp = client.execute(req);
				parseResponse(rsp);
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

	private HttpUriRequest createGetRequest(String url, int startIndex,
			int endIndex) {
		HttpUriRequest getReq = new HttpGet(url);
		String rangeValue = null;
		if (startIndex > 0) {
			rangeValue = "bytes=" + startIndex + "-";
			if (endIndex > startIndex) {
				rangeValue += endIndex;
			}
		} else if (endIndex > 0) {
			rangeValue = "bytes=-" + endIndex;
		}

		if (rangeValue != null) {
			getReq.addHeader("Range", rangeValue);
		}
		return getReq;
	}

	private HttpClient createClient() {
		HttpClient client = new DefaultHttpClient();
		return client;
	}

	private void parseResponse(HttpResponse rsp) throws Exception {
		int statesCode = rsp.getStatusLine().getStatusCode();
		log("statesCode " + statesCode);

		if (statesCode == HttpStatus.SC_OK || statesCode == 206) {
			HttpEntity entity = rsp.getEntity();

			long contentLen = entity.getContentLength();
			log("contentLen " + contentLen);

			InputStream is = entity.getContent();
			output2File(is, contentLen);
		}
	}

	private void output2File(InputStream is, long contentLen) throws Exception {
		String tmpFileName = getFileName(URI) + ".tmp";
		log("tmpFileName " + tmpFileName);

		RandomAccessFile raf = new RandomAccessFile(tmpFileName, "rw");

		String currThreadName = Thread.currentThread().getName();

		byte[] buf = new byte[1024 * 2];
		int len = -1;
		long sum = 0;
		while ((len = is.read(buf)) != -1 && sum <= contentLen) {
			raf.write(buf, 0, len);
			sum += len;
			log("download len " + currThreadName + len);
		}

		log(currThreadName + " download over");

		raf.close();
		is.close();

		// File file = new File(fileName);
		// if(file.exists()){
		// file.delete();
		// }
		// boolean rename = tmpfile.renameTo(file);
		// log("rename " + rename);
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

	// private String dis(String arg){
	// MD5Digest md5 = new MD5Digest();
	// String digName = md5.getAlgorithmName();
	// try {
	// md5.doFinal(arg.getBytes("utf-8"), 0);
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}

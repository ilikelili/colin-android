package com.windo.common.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * �ļ�������
 * @author houmiao.xiong
 * @email 925399262@qq.com
 * @version 1.0 2013-11-4 ����11:21:10
 */
public class DownloadFile {

//	public static final String url = "http://file1.updrv.com/soft/2012/drivethelife5_setup.exe";
	public static final String url = "http://www.java2s.com/Code/JarDownload/apache/apache-commons-logging.jar.zip";
//	public static final String url = "http://www.chongdingxiang.com";
	
	
	public static void main(String[] args){
		
		try {
			long startTime = System.currentTimeMillis();
			
			DownloadFile df = new DownloadFile();
			HttpResponse rsp = df.get(url);
			df.parseResponse(rsp, url);
			
			df.log("load time: " + (System.currentTimeMillis() - startTime));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private HttpResponse get(String url) throws Exception{
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		String tmpName = getFileName(url) + ".tmp";
		File tmpFile = new File(tmpName);
		if(tmpFile.exists()){
			log("tmpFile len " + tmpFile.length());
			get.addHeader("Range", "bytes=" + tmpFile.length() + "-");
		}
		return client.execute(get);
	}
	
	
	private void parseResponse(HttpResponse rsp, String url) throws Exception{
		int  statesCode = rsp.getStatusLine().getStatusCode();
		log("statesCode " + statesCode);
		
		if(statesCode == HttpStatus.SC_OK || statesCode == 206){
			HttpEntity entity = rsp.getEntity();
			
			long contentLen = entity.getContentLength();
			log("contentLen " + contentLen);
			
			Header rangeHeader = rsp.getFirstHeader("Content-Range");
			if(rangeHeader != null){
				String contentRange = rangeHeader.getValue();
				log("contentRange " + contentRange);
				int index = contentRange.lastIndexOf("/");
				String len = contentRange.substring(index + 1);
				contentLen = Long.valueOf(len);
			}
			
			InputStream is = entity.getContent();
			
			String fileName = getFileName(url);
			log("fileName " + fileName);
			
			File tmpfile = new File(fileName + ".tmp");
			FileOutputStream fos = new FileOutputStream(tmpfile,true);
			
			byte[] buf = new byte[1024 * 4];
 			int len = -1;
 			long sum = tmpfile.length();
			while((len = is.read(buf)) != -1 && sum <= contentLen){
				fos.write(buf, 0, len);
				fos.flush();
				sum += len;
				log("download len " + len);
			}
			
			log("download over");
			
			fos.close();
			is.close();
		
			File file = new File(fileName);
			if(file.exists()){
				file.delete();
			}
			boolean rename = tmpfile.renameTo(file);
			log("rename " + rename);
		}
	}
	
	private String getFileName(String url){
		String fileName = null;
		int index = url.lastIndexOf("/");
		if(index >= 0){
			fileName = url.substring(index + 1);
		}else{
			SimpleDateFormat sdf  = new SimpleDateFormat("yyMMddhhmmss");
			fileName = sdf.format(new Date(System.currentTimeMillis())) + "_loadfile";
		}
		return fileName;
	}
	
	private void log(String msg){
		System.out.println(msg);
	}
	
//	private String dis(String arg){
//		MD5Digest md5 = new MD5Digest();
//		String digName = md5.getAlgorithmName();
//		try {
//			md5.doFinal(arg.getBytes("utf-8"), 0);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}

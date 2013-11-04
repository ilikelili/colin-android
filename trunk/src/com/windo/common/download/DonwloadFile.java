package com.windo.common.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 文件下载器
 * @author houmiao.xiong
 * @email 925399262@qq.com
 * @version 1.0 2013-11-4 上午11:21:10
 */
public class DonwloadFile {

	public static final String url = "http://file1.updrv.com/soft/2012/drivethelife5_setup.exe";
	
	
//	public static void main(String[] args){
		
//		try {
//			DownloadFile df = new DownloadFile();
//			HttpResponse rsp = df.get(url);
//			df.parseResponse(rsp, url);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	
	public HttpResponse get(String url) throws Exception{
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		return client.execute(get);
	}
	
	
	public void parseResponse(HttpResponse rsp, String url) throws Exception{
		int  statesCode = rsp.getStatusLine().getStatusCode();
		log("statesCode " + statesCode);
		
		if(statesCode == HttpStatus.SC_OK){
			HttpEntity entity = rsp.getEntity();
			
			long contentLen = entity.getContentLength();
			log("contentLen " + contentLen);
			
			InputStream is = entity.getContent();
			
			String fileName = null;
			int index = url.lastIndexOf("/");
			if(index >= 0){
				fileName = url.substring(index + 1);
			}else{
				SimpleDateFormat sdf  = new SimpleDateFormat("yyMMddhhmmss");
				fileName = sdf.format(new Date(System.currentTimeMillis())) + ".exe";
			}
			
			log("fileName " + fileName);
			
			File file = getFile(fileName);
			FileOutputStream fos = new FileOutputStream(file);
			
			byte[] buf = new byte[1024 * 4];
 			int len = -1;
 			int sum = 0;
			while((len = is.read(buf)) != -1 && sum <= contentLen){
				fos.write(buf, 0, len);
				fos.flush();
				sum += len;
				log("download len " + len);
			}
			
			log("download over");
			
			fos.close();
			is.close();
			
		}
	}
	
	private File getFile(String fileName) throws Exception{
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}else{
			file.createNewFile();
		}
		return file;
	}
	
	
	private void log(String msg){
		System.out.println(msg);
	}
	
}

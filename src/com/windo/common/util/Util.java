package com.windo.common.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Vector;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.StatFs;

public class Util {
	
	/**
	 * 容量转换
	 * @param bit
	 * @return
	 * 2013-9-24 下午5:04:15
	 * @author houmiao.xiong
	 */
	public static String translateUnit(long bit){
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#0.00");

		String unit = null;

		if(bit >= 1024*1024){
			float mb = (float) bit / (1024*1024);
			unit = df.format(mb) + "MB";
		}else if(bit > 0){
			float kb = (float) bit / 1024;
			unit = df.format(kb) + "KB";
		}else if(bit == 0){
			unit = bit+"KB";
		}else{
			unit = bit + "B";
		}

		return unit; 
	}
	
	/**
	 * 获取内存信息
	 * @return
	 * 2013-9-24 下午5:04:00
	 * @author houmiao.xiong
	 */
	public static String getSystemMemoryInfo(){
		
		long totalMemory = Runtime.getRuntime().totalMemory();  
        
        long freeMemory = Runtime.getRuntime().freeMemory();  
        
        long maxMemory = Runtime.getRuntime().maxMemory(); 

        StringBuilder sb = new StringBuilder();
        sb.append("TotalMemory: ");
        sb.append(translateUnit(totalMemory));
        sb.append("; FreeMemory: ");
        sb.append(translateUnit(freeMemory));
        sb.append("; MaxMemory: ");
        sb.append(translateUnit(maxMemory));
        return sb.toString();
	}

	/**
	 * 
	 * @param path
	 * @return
	 * 2013-9-24 下午5:03:52
	 * @author houmiao.xiong
	 */
	public static float getFreeDiskSize(String path){
		if(path != null && path.length() > 0){
			StatFs statFs = new StatFs(path);
			long blockSize = statFs.getBlockSize();
			long availSize = statFs.getAvailableBlocks();
			float availableSpare = (float)(availSize*blockSize)/(1024);
			return availableSpare;
		}
		return 0;
	}

	
	public  static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); 

		} catch (Exception e) {
			
			e.printStackTrace();

		}
	}

	public static void  calculateCacheSize(String path,Vector<Long> v){


		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				v.add(temp.length());

			}
			if (temp.isDirectory()) {
				calculateCacheSize(path + "/" + tempList[i],v);
			}
		}

	}
	
	public  static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);
				delFolder(path + "/" + tempList[i]);
			}
		}
	}

	

	
	public static  String getMocamVersion(Context context){
		PackageInfo info;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			String appName = info.versionName;
			int appCode = info.versionCode;
			return appName+"("+appCode+")";
		} catch (NameNotFoundException e) {
			
			e.printStackTrace();
		}
		return null;
	}


	
	public static String convertToLegalXmlStr(String oldStr){
		String newStr = null;
		try {
			newStr = new String(oldStr.replace("&", "&amp;").replace("<", "&lt;")
					.replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;")
					.replace("\n", "&#x0A;").replace(" ", "&#x20;"));

		} catch (Exception e) {
			e.printStackTrace(); 
		}

		return newStr;

	}
	
	
	public static String convertToNomalStr(String oldStr){
		String newStr = null;
		try {
			newStr = new String(oldStr.replace("&amp;","&").replace( "&lt;","<")
					.replace("&gt;",">").replace("&apos;","'").replace("&quot;","\"")
					.replace("&#x0A;","\n").replace("&#x20;", " "));

		} catch (Exception e) {
			e.printStackTrace(); 
		}

		return newStr;

	}

    public static boolean isPackageExists(Context context,String targetPackage){
        PackageManager pm=context.getPackageManager();
        try {
            pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            return false;
        }
        return true;
    }
    
    
}

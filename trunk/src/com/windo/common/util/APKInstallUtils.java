package com.windo.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import com.windo.common.pal.internal.PalLog;

public class APKInstallUtils {

	public static final String TAG = "APKInstallUtils";

	public static final String SYS_INSTALL_PKG = "com.android.packageinstaller";
	
	public static boolean installApk(Context ctx, String apkfilePath,
			String pkgName, int apkVerCode) {

		if (!checkAPKInstalled(ctx, pkgName, apkVerCode)) {

			PalLog.d(TAG, "silent install failed. try PackageInstaller");
			File apkfile = new File(apkfilePath);
			setAPKInstallPower(apkfile);
			Uri uri = Uri.fromFile(apkfile);
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.setDataAndType(uri,"application/vnd.android.package-archive");
			
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctx.startActivity(intent);
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			long now = System.currentTimeMillis();
			long WAIT_INSTALL = 24 * 60 * 60 * 1000;
			boolean installed = false;
			
			String expected = "com.android.packageinstaller";
			String installProgressClass = "com.android.packageinstaller.InstallAppProgress";
			
			while ((System.currentTimeMillis() - now < WAIT_INSTALL)
					&& (!installed)) {

				if (!isPkgInstallerRunning(ctx, expected)) {
					
					if (checkAPKInstalled(ctx, pkgName, apkVerCode)) {
						PalLog.d(TAG, "apk installed:");
						installed = true;
					}else{
						PalLog.d(TAG, "apk installation cancelled by user");
						installed = false;	
					}
					break;
				}else{
					if(!isPkgTopRunning(ctx, expected)){
						ComponentName compName = getTaskComp(ctx, expected);
                    	if (checkAPKInstalled(ctx, pkgName, apkVerCode)) {
    						PalLog.d(TAG, "apk installed:");
    						installed = true;
    						
    						break;
    					}else if(!compName.getClassName().equals(installProgressClass)){
    						PalLog.d(TAG, "apk installation cancelled by user");
    						installed = false;
    						
    						break;
    					}
                    }
					
                    PalLog.d(TAG, "PkgInstaller is Running");
                }
				
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!installed) {
				PalLog.d(TAG, "wait install timeout");
			}
			
			return installed;
		} else {
			PalLog.d(TAG, "apk installed!");
			return true;
		}

	}

	
	public static boolean installAsynApk(Context ctx, String apkfilePath,
			String pkgName, int apkVerCode) {

		if (!checkAPKInstalled(ctx, pkgName, apkVerCode)) {
			PalLog.d(TAG, "silent install failed. try PackageInstaller");
			File apkfile = new File(apkfilePath);
			setAPKInstallPower(apkfile);
			Uri uri = Uri.fromFile(apkfile);
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.setDataAndType(uri,
					"application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctx.startActivity(intent);
			return true;
		}else{
			PalLog.d(TAG, "apk installed!");
			return false;
		}
	}

	public static boolean isPkgInstallerRunning(Context context, String expected) {
		boolean ret = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService("activity");
		List<RunningTaskInfo> taskInfos = activityManager
				.getRunningTasks(2147483647);
		for (ActivityManager.RunningTaskInfo info : taskInfos) {
			ComponentName topActivity = info.topActivity;
			String topActivityPkgName = topActivity.getPackageName();

			if (expected.equals(topActivityPkgName)) {
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	
	
	public static boolean isPkgTopRunning(Context context, String packagename){
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService("activity");
		List<RunningTaskInfo> taskInfos = activityManager
				.getRunningTasks(2147483647);
		if(taskInfos.size() > 0){
			RunningTaskInfo info = taskInfos.get(0);
			ComponentName topActivity = info.topActivity;
			
			PalLog.d(TAG, "topActivity: " + topActivity);
			
			String topActivityPkgName = topActivity.getPackageName();
//			if(PalLog.DEBUG){
//				PalLog.i(TAG, "topClassName: " + topActivity.getClassName());
//			}
			if(packagename.equals(topActivityPkgName)){
				return true;
			}
		}
		return false;
	}
	
	public static ComponentName getTaskComp(Context context, String apk){
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService("activity");
		List<RunningTaskInfo> taskInfos = activityManager.getRunningTasks(2147483647);
		
		if(taskInfos.size() > 0){
			for(RunningTaskInfo info : taskInfos){
				
				ComponentName compName = info.topActivity;
				
				if(compName.getPackageName().equals(apk)){
					
					PalLog.d(TAG, "compName: " + compName);
					
					return compName;
				}
			}
		}
		return null;
	}
	
	
	public static String getTopRunningPkg(Context context){
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService("activity");
		List<RunningTaskInfo> taskInfos = activityManager
				.getRunningTasks(2147483647);
		if(taskInfos.size() > 0){
			RunningTaskInfo info = taskInfos.get(0);
			
			ComponentName topActivity = info.topActivity;
			
			PalLog.d(TAG, "topActivity: " + topActivity);
			
			String topActivityPkgName = topActivity.getPackageName();
			if(PalLog.DEBUG){
				PalLog.i(TAG, "topActivityPakname: " + topActivityPkgName);
			}
			return topActivityPkgName;
		}
		return null;
	}
	

	
	private static void setAPKInstallPower(File apkfile) {
		String folder = apkfile.getParent();
		String apk = apkfile.getPath();
		String[] args1 = { "chmod", "705", folder };
		exec(args1);
		args1 = new String[] { "chmod", "604", apk };
		exec(args1);
	}

	
	private static String exec(String[] args) {
		String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			baos.write('\n');
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}
		return result;
	}

	
	public static boolean checkAPKInstalled(Context ctx, String packagename,
			int apkVerCode) {
		PackageManager pm = ctx.getPackageManager();

		PackageInfo apkinfo;
		try {
			apkinfo = pm.getPackageInfo(packagename, 0);
			if (apkinfo != null
					&& (apkVerCode < 0 || (apkinfo.versionCode >= apkVerCode))) {
				return true;
			} else {
				return false;
			}
		} catch (NameNotFoundException e) {
			
		
			return false;
		}

	}
}

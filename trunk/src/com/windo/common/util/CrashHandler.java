package com.windo.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.colin.android.mail.Mail;

public class CrashHandler implements UncaughtExceptionHandler {

	private static final String TAG = "CrashHandler";

	private static final int ERR_LOG_SIZE = 2 * 1024 * 1024;

	private static final String ERR_LOG_PATH = "/logs/";
	
	private static final String ERR_LOG_NAME = "crashlog.txt";

	private Context mContext;

	public CrashHandler(Context context) {
		mContext = context;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

		toastErr(ex);
		writeToFile(thread, ex);
//		sendToMail(errMsg);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.exit(0);
	}
	
	private void toastErr(Throwable ex){
		String msg = ex.getMessage();
		Log.e(TAG, "msg: " + msg);
		final String errMsg = String.format("很抱歉，手机钱包意外停止，" +
				"程序将在5秒后自动退出。\r\n异常报告： %1$s。", msg);
		
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast toast = Toast.makeText(mContext, errMsg, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.show();
				Looper.loop();
			}
		}.start();
	}

	private String writeToFile(Thread td, Throwable ex) {
		String errInfo = null;

		if(ex != null){
			File errLog = getErrDir();
			errLog = new File(errLog, ERR_LOG_NAME);

			if (!errLog.exists()) {
				try {
					errLog.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (errLog.length() > ERR_LOG_SIZE) {
					errLog.delete();
				}
			}

			FileOutputStream fos = null;
			ByteArrayOutputStream baos = null;
			try {
				fos = new FileOutputStream(errLog, true);
				baos = new ByteArrayOutputStream();

				// write space
				if (errLog.length() != 0) {
					baos.write("\r\n\r\n".getBytes("utf-8"));
				}

				// write currtime
				baos.write(getCurrTime().getBytes("utf-8"));

				// write device
				baos.write(getUEPROF().getBytes("utf-8"));
				
				//write clientInfo
				baos.write(getClientInfo().getBytes("utf-8"));

				// wirte thread
				if (td != null) {
					baos.write(td.toString().getBytes("utf-8"));
				}
				baos.write("\r\n".getBytes("utf-8"));

				PrintStream ps = new PrintStream(baos);

				int times = 0;
				while (ex != null) {
					Log.e(TAG, "times " + times);
					ex.printStackTrace(ps);
					ex = ex.getCause();
					times++;
				}
				baos.flush();

				byte[] bytes = baos.toByteArray();
				
				fos.write(bytes);
				fos.flush();
				fos.close();
				
				baos.close();
				ps.close();
				
				return new String(bytes, "utf-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		return errInfo;
	}
	
	private void writeToFile2(Thread td, Throwable ex) {
		if(ex != null){
			File errLog = getErrDir();
			errLog = new File(errLog, ERR_LOG_NAME);

			if (!errLog.exists()) {
				try {
					errLog.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (errLog.length() > ERR_LOG_SIZE) {
					errLog.delete();
				}
			}

			PrintWriter pw = null;
			try {
				FileOutputStream fos = new FileOutputStream(errLog, true);
				pw = new PrintWriter(fos);
				
				
				// write space
				if (errLog.length() != 0) {
					pw.write("\r\n\r\n");
				}

				// write currtime
				pw.write(getCurrTime());

				// write device
				pw.write(getUEPROF());
				
				//write clientInfo
				pw.write(getClientInfo());

				// wirte thread
				if (td != null) {
					pw.write(td.toString());
				}
				pw.write("\r\n");

				int times = 1;
				while (ex != null) {
					Log.e(TAG, "times " + times);
					ex.printStackTrace(pw);
					ex = ex.getCause();
					times++;
				}
				pw.flush();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if(pw != null){
					pw.close();
				}
			}
		}
		
	}

	private boolean sendToMail(String body) {
		Mail m = new Mail("", "");

		String[] toArr = { ""};
		m.setTo(toArr);
		m.setFrom("");
		m.setSubject("crash log");
		m.setBody(body);
		try {
			return m.send();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private String getUEPROF() {
		StringBuilder mBuffer = new StringBuilder();

		int appCode = 1;
		try {
			PackageInfo info = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
			appCode = info.versionCode;
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		mBuffer.setLength(0);
		mBuffer.append("ME-2-Android_");
		mBuffer.append(android.os.Build.VERSION.RELEASE.replace('-', '_'));
		mBuffer.append("-");
		mBuffer.append(appCode);
		mBuffer.append("-");
		mBuffer.append(android.os.Build.VERSION.SDK_INT);
		mBuffer.append("-");

		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		mBuffer.append(display.getHeight());
		mBuffer.append("*");
		mBuffer.append(display.getWidth());
		mBuffer.append("-");
		mBuffer.append(android.os.Build.MODEL.replace('-', '_'));
		mBuffer.append("\r\n");

		return mBuffer.toString();
	}
	
	private String getClientInfo(){
		StringBuffer sb = new StringBuffer();
		
		PackageManager pm = mContext.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), 0);
			String verName = info.versionName;
			int verCode = info.versionCode;
			
			sb.append("PackageName=");
			sb.append(mContext.getPackageName());
			sb.append("; VersionName=");
			sb.append(verName);
			sb.append("; VersionCode=");
			sb.append(verCode);
			sb.append("\r\n");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}

	private String getCurrTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(new Date()) + "\r\n";
	}

	private File getErrDir() {
		File file = Config.getCacheFile(mContext);
		file = new File(file, ERR_LOG_PATH);

		if(!file.exists()){
			file.mkdirs();
		}
		
		return file;
	}

}

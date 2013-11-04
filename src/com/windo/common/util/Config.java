package com.windo.common.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class Config {
	
	//sd卡缓存根目录
	public static final String CACHE_DIR = "/conlin";
	
	/**
	 * 获取cache目录
	 * @param context
	 * @return
	 * @time 2013-11-3 下午3:02:11
	 */
	public static File getCacheFile(Context context){
		File file = null;

		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			File dir = Environment.getExternalStorageDirectory();
			file = new File(dir, CACHE_DIR);
		} else {
			file = context.getFilesDir();
		}

		if (!file.exists()) {
			file.mkdirs();
		}

		return file;
	}

}

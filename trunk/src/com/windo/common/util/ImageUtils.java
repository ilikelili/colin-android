package com.windo.common.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

public class ImageUtils {

	
	public static Bitmap createRepeater(int width, Bitmap src){
		int count = (width + src.getWidth() - 1) / src.getWidth();
		 
		Bitmap bitmap = Bitmap.createBitmap(width, src.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		 
		for(int idx = 0; idx < count; ++ idx){
		 

		canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
		}
		 
		return bitmap;
		}


}

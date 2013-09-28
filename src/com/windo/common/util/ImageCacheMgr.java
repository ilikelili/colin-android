package com.windo.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.windo.common.crypto.SHA1;
import com.windo.common.http.HttpCallBack;
import com.windo.common.http.HttpEngine;
import com.windo.common.http.HttpRequest;
import com.windo.common.pal.IHttp;
import com.windo.common.pal.internal.PalLog;


public class ImageCacheMgr {

	
	public static final int MAX_CACHE_SIZE = 30;

	
	
	private static final String TAG = "ImageCacheMgr";

	
	public interface ImageCallBack {

		
		public void onGetImage(Bitmap bitmap, String url);

		
		public void onGetError(String url);
	} 
	
	
	
	protected class BitmapWeakReference extends WeakReference<Bitmap>{
		
		String key;
		String folder;

		public BitmapWeakReference(String key, String folder, Bitmap r, ReferenceQueue<Bitmap> q) {
			super(r, q);
			this.key = key;
			this.folder = folder;
			
		}
		
	}
	
	
	
	ReferenceQueue<Bitmap> refQueue;

	public ImageCacheMgr(Context context) {
		mContext = context;

		refQueue = new ReferenceQueue<Bitmap>();
		mMemoryCache = new Hashtable<String, BitmapWeakReference>();
		mMemoryCacheURLList = new Vector<String>();
		mDownloadCallBackMap = new Hashtable<String, List<ImageCallBack>>();
		mImgDownRequestMap = new Hashtable<String, ImageRequest>();
	}

	
	
	
	
	
	

	public void destroy() {

		mMemoryCache.clear();
		mMemoryCacheURLList.clear();
		if (mHttpEngine != null) {
			mHttpEngine.shutdown();
			mHttpEngine = null;

		}
	}


	
	
	

	
	public Bitmap getImage(String url,String folder,Size rect) {
		if (!url.startsWith("http://")) {
			url = "http://" + url;
		}
		List<String> cacheList = getCacheURLList(folder);

		Hashtable<String, BitmapWeakReference> mMemoryCache = getCacheTable(folder);
		synchronized (mMemoryCache) {
			if (mMemoryCache.containsKey(url)) {



				BitmapWeakReference bitmapReference = mMemoryCache.get(url);
				
				if(bitmapReference != null){
					Bitmap bitmap = bitmapReference.get();
					
					
					if(bitmap != null && !bitmap.isRecycled()){
						return bitmap;
					}else {
						cacheList.remove(url);
						mMemoryCache.remove(url);
					}
				}else{
					cacheList.remove(url);
					mMemoryCache.remove(url);
				}
				
			} 
			





		}
		return null;
	}
	
	
	
	protected Bitmap getImageSyn(String url,String folder,Size rect){
		Bitmap bitmap = getImage(url, folder, rect);
		if(bitmap == null){
			bitmap = getImageFormFile(url,folder,rect);
		}
		return bitmap;
	}


	
	
	
	
	
	
	
	
	

	
	public void doDownloadImage(String url, final String saveFolder, 
			final Size size,ImageCallBack callback) {
		
		if(url == null || url.trim().length() == 0 ){
			throw new IllegalArgumentException("download image url is null");
		}

		if (!url.startsWith("http://")) {
			url = "http://" + url;
		}

		if (mHttpEngine == null) {
			mHttpEngine = new HttpEngine(mContext);
		}
		
		final List<String> fileList = getLoadingFileList();
 		
		synchronized (mDownloadCallBackMap) {
			// 同一个地址在下载列表中.
			
			if(fileList.contains(url)){
				List<ImageCallBack> callbackList = mDownloadCallBackMap.get(url);
				if ( callbackList != null && callback != null && !callbackList.contains(callback)) {
					callbackList.add(callback);
				}
			} else {
				fileList.add(url);
				Vector<ImageCallBack> callbackList = new Vector<ImageCallBack>();
				mDownloadCallBackMap.put(url, callbackList);
				if(callback != null){
					callbackList.add(callback);	
				}
				
				//先从文件中读取
				AsyncLoadFileBitmap loadBitmap = new AsyncLoadFileBitmap(new ImageCallBack() {
					
					@Override
					public void onGetImage(Bitmap bitmap, String url) {
						// TODO Auto-generated method stub
						fileList.remove(url);
						
						List<ImageCallBack> callbackList = mDownloadCallBackMap.remove(url);
						
						if(callbackList != null){
							for(ImageCallBack callback : callbackList){
								if(callback != null){
									callback.onGetImage(bitmap, url);
								}
							}
						}else{
							PalLog.e(TAG, "callback == null " + getUrlId(url));
						}
						
					}
					
					@Override
					public void onGetError(String url) {
						// TODO Auto-generated method stub
						//文件中不存在
						
						ImageRequest request = new ImageRequest();//图片下载请求放入到请求列表
						request.url  = url;
						request.folder  = saveFolder;
						request.imgSize = size;
						mImgDownRequestMap.put(url, request);	

						HttpRequest download = new HttpRequest(url);	//生成http请求
						download.setHttpCallBack(mDownloadCallback);
						download.setStreamCallBack(true);
						mHttpEngine.addRequest(download);
					}
					
				}, saveFolder, size);
				
				loadBitmap.execute(url);

			}
		}

//		synchronized (mDownloadCallBackMap) {
//			
//			if (mDownloadCallBackMap.containsKey(url)) {
//				List<ImageCallBack> callbackList = mDownloadCallBackMap.get(url);
//				if ( callback != null && !callbackList.contains(callback)) {
//					callbackList.add(callback);
//				}
//			} else {
//				Vector<ImageCallBack> callbackList = new Vector<ImageCallBack>();
//				mDownloadCallBackMap.put(url, callbackList);
//				if(callback != null){
//					callbackList.add(callback);	
//				}
//
//				ImageRequest request = new ImageRequest();
//				request.url  = url;
//				request.folder  = saveFolder;
//				request.imgSize = size;
//				mImgDownRequestMap.put(url, request);	
//
//				HttpRequest download = new HttpRequest(url);	
//				download.setHttpCallBack(mDownloadCallback);
//				download.setStreamCallBack(true);
//				mHttpEngine.addRequest(download);
//
//			}
//		}
	}
	
	
	protected void removeImageCallback(String url){
		synchronized (mDownloadCallBackMap) {
			if (mDownloadCallBackMap.containsKey(url)) {
				mDownloadCallBackMap.remove(url);
			}
		}
	}

	
	
	
	

	
	protected Bitmap getImageFormFile(String url,String folder,Size size) {
		String filename = SHA1.digest(url) + ".png";
		File file = getCacheDir(folder);
		if (file != null) {
			
			file = new File(file, filename);
			Bitmap fileCache = null;
			
			if (file.exists()) {
				if(size != null){
					fileCache = createBitmap(file.getPath(),size);
				}else{
					fileCache = BitmapFactory.decodeFile(file.getPath());
				}
				
				if (fileCache != null) {
					fileCache = addImageToCache(url, folder, fileCache);
					return fileCache;
				}
				
			}
		}
		return null;
	}
	
	private List<String> loadingFileUrlList;
	
	private List<String> getLoadingFileList(){
		if(loadingFileUrlList == null){
			loadingFileUrlList = new ArrayList<String>();
		}
		return loadingFileUrlList;
	}
	
	
//	protected void doDownloadImage(String url, final String folder, final Size size, 
//			final ImageCallBack callback){
//		
//		AsyncLoadFileBitmap loadBitmap = new AsyncLoadFileBitmap(new ImageCallBack() {
//			
//			@Override
//			public void onGetImage(Bitmap bitmap, String url) {
//				
//				if(callback != null){
//					callback.onGetImage(bitmap, url);
//				}
//			}
//			
//			@Override
//			public void onGetError(String url) {
//				
//				downloadImage(url, folder, size, callback);
//			}
//			
//		}, folder, size);
//		
//		loadBitmap.execute(url);
//	}
	
	
	class AsyncLoadFileBitmap extends AsyncTask<String, Integer, Bitmap>{
		
		ImageCallBack callback;
		
		String url;
		
		String folder;
		
		Size size;
		
		public AsyncLoadFileBitmap(ImageCallBack callback, String folder, Size size){
			this.callback = callback;
			this.folder = folder;
			this.size = size;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			
			url = params[0];
			return getImageFormFile(url, folder, size);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			
			super.onPostExecute(result);
			
			if(callback != null){
				if(result == null){
					callback.onGetError(url);
				}else{
					callback.onGetImage(result, url);
				}
			}
			
		}
		
	}

	
	protected Bitmap addImageToCache(String url, String folder,Bitmap bitmap) {

		Hashtable<String, BitmapWeakReference> table = getCacheTable(folder);
		List<String> urlList = getCacheURLList(folder);
		int maxCacheSize = getCacheMaxSize(folder);
		if(urlList == null){
			throw new IllegalArgumentException("getCacheURLList return null"); 
		}
		if(maxCacheSize < 0){
			throw new IllegalArgumentException("maxCacheSize <0");
		}
		synchronized (table) {
			
			PalLog.d(TAG, "addBitmap: " + folder + " Id: " + getUrlId(url));
			
			clearRecycledBitmap();
			table.put(url, new BitmapWeakReference(url, folder, bitmap, refQueue));
			urlList.add(url);
			return bitmap;
		}
	}
	
	private String getUrlId(String url){
		if(url != null){
			int index = url.lastIndexOf("=");
			if(index >= 0){
				String id = url.substring(index + 1);
				return id;
			}
		}
		return null;
	}
	
	protected void clearRecycledBitmap(){
		BitmapWeakReference bitmapRefer = null;
		while((bitmapRefer = (BitmapWeakReference)refQueue.poll()) != null){
			PalLog.d(TAG, "clearBitmap: " + bitmapRefer.folder 
					+ " Id: " + getUrlId(bitmapRefer.key));
			mMemoryCache.remove(bitmapRefer.key);
			mMemoryCacheURLList.remove(bitmapRefer.key);
		}
	}

	



	protected Hashtable<String, BitmapWeakReference> getCacheTable(String folder) {
		return mMemoryCache;
	}
	

	
	protected List<String> getCacheURLList(String folder) {
		return mMemoryCacheURLList;
	}

	
	protected int getCacheMaxSize(String folder){
		return MAX_CACHE_SIZE;
	}

	
	protected File getCacheDir(String folderStr) {
		File folder =  mContext.getCacheDir();
		if(folderStr == null || folderStr.length() <= 0){
			return null;
		}else{
			folder = new File(folder,folderStr);
			if(!folder.exists()){
				folder.mkdirs();
			}
		}
		return folder;
	}


	private Handler getDownloadHandler(){
		if(mDownloadHandler == null){
			mDownloadHandler = new Handler(Looper.getMainLooper()){

				public void handleMessage(Message msg) {
					String url = (String) msg.obj;
					List<ImageCallBack> callbacks = null;
					synchronized (mDownloadCallBackMap) {
						callbacks = mDownloadCallBackMap.get(url);
						mDownloadCallBackMap.remove(url);
					}
					
					ImageRequest request = mImgDownRequestMap.remove(url);
					getLoadingFileList().remove(url);
					
					
					if(callbacks != null && callbacks.size() > 0){
						if(msg.what == DOWNLOAD_SUCCEED){
							
							BitmapWeakReference bitmapRef = getCacheTable(request.folder).get(url);
							Bitmap bitmap = bitmapRef == null ? null : bitmapRef.get();  
							
							if(bitmap != null){
								for(int i = 0; i < callbacks.size(); i++){
									ImageCallBack cb = callbacks.get(i);
									if(cb != null){
										cb.onGetImage(bitmap, url);
									}
								}
								
								return;
							}
						}
						
						for(int i = 0; i < callbacks.size(); i++){
							ImageCallBack cb = callbacks.get(i);
							if(cb != null){
								cb.onGetError(url);
							}
						}
						
					}
					
				}
							
			};
		}
		return mDownloadHandler;
	}
	
	
	private HttpCallBack mDownloadCallback = new HttpCallBack() {

		@Override
		public void onReceived(int requestId, InputStream stream,
				long contentLength, IHttp http) {
			

			ImageRequest request = mImgDownRequestMap.get(http.getURL());
			if (request != null) {
				
				
				


				String folder = request.folder;
				String filename = SHA1.digest(request.url);
				
				
				File dir = getCacheDir(folder);

				File tmpFile = new File(dir, filename + ".png.tmp");
				if (tmpFile.exists()) {
					tmpFile.delete();
				}
				
				try {
					FileOutputStream fos = new FileOutputStream(tmpFile);
					byte[] buf = new byte[1024];
					int num = -1;
					while ((num = stream.read(buf)) != -1) {
						fos.write(buf, 0, num);
						fos.flush();
					}
					fos.close();
					File file = new File(dir, filename + ".png");
					if (file.exists()) {
						file.delete();
					}
					tmpFile.renameTo(file);
					Bitmap bitmap = null;
					if(request.imgSize != null){
						bitmap = createBitmap(file.getPath(),request.imgSize);
					}else{
						bitmap =  BitmapFactory.decodeFile(file.getPath());
					}

					Handler handler = getDownloadHandler();
					Message msg = null;
					if(bitmap == null){
						msg = handler.obtainMessage(DOWNLOAD_FAIL,
								request.url);
					}else{
						addImageToCache(request.url,folder, bitmap);
						msg = handler.obtainMessage(DOWNLOAD_SUCCEED, request.url);
					}
					handler.sendMessage(msg);	
				} catch (IOException e) {
					
					Handler handler = getDownloadHandler();
					Message msg = handler.obtainMessage(DOWNLOAD_FAIL,
							request.url);
					handler.sendMessage(msg);
				}
			}
		}

		@Override
		public void onReceived(int requestId, byte[] data, IHttp http) {
			

		}

		@Override
		public void onError(int requestId, int errCode, byte[] errStr,
				IHttp http) {
			
			if(http == null){
				return ;
			}
			ImageRequest request = mImgDownRequestMap.get(http.getURL());
			if (request != null) {
				mImgDownRequestMap.remove(http.getURL());
				Handler handler = getDownloadHandler();
				Message msg = handler.obtainMessage(DOWNLOAD_FAIL, http.getURL());
				handler.sendMessage(msg);
			}
		}
	};


	
	private Bitmap createBitmap(String filepath,Size size){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, options);

		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;
		int inWidthSampleSize = bitmapWidth / size.width;
		int inHeightSampleSize = bitmapHeight / size.height;


		options.inJustDecodeBounds = false;
		options.outWidth = size.width;
		options.outHeight = size.height;
		options.inSampleSize = inWidthSampleSize > inHeightSampleSize ? inWidthSampleSize:inHeightSampleSize;

		Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
//		PalLog.d("pic", "create bitmap size="+bitmap.getWidth()+"*"+bitmap.getHeight());
		if(bitmap!=null){
		if(bitmap.getHeight()!=size.height||bitmap.getWidth()!=size.width){
			Matrix matrix = new Matrix();   
	        matrix.postScale((float)size.width/(float)bitmap.getWidth(), (float)size.height/(float)bitmap.getHeight());     
			Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix,
				      true);
			return newbm;
		}
		}
		return bitmap;
	}

	
	private Handler mDownloadHandler ;

	
	
	
	
	
	
	


	
	protected List<String> mMemoryCacheURLList;
	

	protected Hashtable<String, BitmapWeakReference> mMemoryCache;

	
	Hashtable<String, List<ImageCallBack>> mDownloadCallBackMap;

	
	Hashtable<String, ImageRequest> mImgDownRequestMap;


	
	HttpEngine mHttpEngine;

	protected Context mContext;

	
	static final int DOWNLOAD_SUCCEED = 0x0;
	
	static final int DOWNLOAD_FAIL = 0x1;


	public void shutdown(){
		if(mHttpEngine != null){
			mHttpEngine.shutdown();
			mHttpEngine = null;
		}
		if(mMemoryCache != null){
			mMemoryCache.clear();	
		}
		if(mDownloadCallBackMap != null){
			mDownloadCallBackMap.clear();	
			mDownloadCallBackMap = null;
		}
	}


	private class ImageRequest{

		String url;
		String folder;
		Size   imgSize;
	}


	public static class Size{


		public int width;

		public int height;


		public Size(int w,int h){
			width = w;
			height = h;
		}
	}

	
}

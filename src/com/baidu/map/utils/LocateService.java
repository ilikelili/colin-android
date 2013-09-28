package com.baidu.map.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * 定位服务
 * @author houmiao.xiong
 * @email 925399262@qq.com
 * @version 1.0 2013-8-1 下午12:14:52
 */
public class LocateService {
	
	private LocationClient locClient;
	
	private BDLocationListener locateListener;
	
	
	private LocateResultCallback locateCallback;
	private PoiResultCallbck poiCallback;
	
	public LocateService(Context context){
		locClient = new LocationClient(context.getApplicationContext());
		locClient.registerLocationListener(getListner());
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
//		option.setScanSpan(60 * 1000);//设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);//禁止启用缓存定位
		option.setPoiExtraInfo(true);
		
		locClient.setLocOption(option);
	}
	
	/**
	 * 启动服务
	 * 2013-8-2 下午2:13:36
	 * @author houmiao.xiong
	 */
	public void start(){
		locClient.start();
	}
	
	/**
	 * 停止服务
	 * 2013-8-2 下午2:13:50
	 * @author houmiao.xiong
	 */
	public void stop(){
		if(locateListener != null){
			locClient.unRegisterLocationListener(locateListener);
			locateListener = null;
		}
		
		locateCallback = null;
		poiCallback = null;
		
		locClient.stop();
	}
	
	/**
	 * 定位
	 * @param callback
	 * 2013-8-2 下午2:12:09
	 * @author houmiao.xiong
	 */
	public void doLocate(LocateResultCallback callback){
		locateCallback = callback;
		locClient.requestLocation();
	}
	
	/**
	 * Poi请求
	 * 2013-8-2 下午2:12:17
	 * @author houmiao.xiong
	 */
	public void doReqPoi(PoiResultCallbck callback){
		poiCallback = callback;
		locClient.requestPoi();
	}
	
	
	private BDLocationListener getListner(){
		if(locateListener == null){
			locateListener = new BDLocationListener() {
				
				@Override
				public void onReceivePoi(BDLocation arg0) {
					// TODO Auto-generated method stub
					if(poiCallback != null && arg0 != null){
						poiCallback.onPoiResult(arg0);
					}
				}
				
				@Override
				public void onReceiveLocation(BDLocation arg0) {
					// TODO Auto-generated method stub
					if(locateCallback != null && arg0 != null){
						locateCallback.onLocateResult(arg0);
					}
				}
			};
		}
		return locateListener;
	}
	
	
	/**
	 * poi请求结果回调接口
	 * @author houmiao.xiong
	 * @email 925399262@qq.com
	 * @version 1.0 2013-8-2 下午2:10:56
	 */
	public interface PoiResultCallbck{
		
		public void onPoiResult(BDLocation dbLoc);
		
	}
	
	/**
	 * 定位结果回调接口
	 * @author houmiao.xiong
	 * @email 925399262@qq.com
	 * @version 1.0 2013-8-1 下午12:22:16
	 */
	public interface LocateResultCallback{
		
		public void onLocateResult(BDLocation bdLoc);
		
	}

}

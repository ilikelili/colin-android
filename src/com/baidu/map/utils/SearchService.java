package com.baidu.map.utils;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * 搜索管理
 * @author houmiao.xiong
 * @email 925399262@qq.com
 * @version 1.0 2013-8-1 下午5:20:03
 */
public class SearchService {
	
	public static final int SUCCESS_CODE = 0;
	
	private MKSearch mkSearch;
	
	private MKSearchListener searchListener;
	
	private SearchPoiResultCallback poiResultCallbcak;
	
	public SearchService(BMapManager mapMgr){
		mkSearch = new MKSearch();
		mkSearch.init(mapMgr, getSearchListener());
	}
	
	public void searchPoiNearBy(String key, GeoPoint point, int radius, 
			SearchPoiResultCallback callback){
		poiResultCallbcak = callback;
		mkSearch.poiSearchNearBy(key, point, radius);
	}
	
	private MKSearchListener getSearchListener(){
		if(searchListener == null){
			searchListener = new MKSearchListener() {
				
				@Override
				public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
					// TODO Auto-generated method stub
					if(poiResultCallbcak != null){
						poiResultCallbcak.onGetPoiResult(arg0, arg1, arg2);
					}
				}
				
				@Override
				public void onGetPoiDetailSearchResult(int arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
			};
		}
		return searchListener;
	}
	
	public interface SearchPoiResultCallback{
		
		public void onGetPoiResult(MKPoiResult result, int type, int code);
		
	}

}

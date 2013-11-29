package com.baidu.map.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.map.utils.BMapUtil;
import com.baidu.map.utils.LocateService;
import com.baidu.map.utils.LocateService.LocateResultCallback;
import com.baidu.map.utils.LocateService.PoiResultCallbck;
import com.baidu.map.utils.SearchService;
import com.baidu.map.utils.SearchService.SearchPoiResultCallback;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapView.LayoutParams;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.colin.android.R;

/**
 * 百度地图Activity
 * @author houmiao.xiong
 * @email 925399262@qq.com
 * @version 1.0 2013-7-30 上午11:16:21
 */
public class BaiduMapActivity extends Activity implements OnClickListener, MKMapViewListener{
	
	private static final String TAG = "BaiduMapActivity";
	
	private BMapManager mapMgr;
	private MapView mapView;
	
	private LocateService locService;
	private SearchService searchService;
	
	private GeoPoint myLocGeo;
	
	private ImageView trafficView;
	private EditText searchEdit;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mapMgr = new BMapManager(getApplicationContext());
		mapMgr.init("DBD68081EF9A9816202E0DBEADB62ACE987362C0", null);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.baidumap);
		
		Log.e(TAG, "onCreate");
		
		initMapview();
		
		initLocClient();
		
		searchService = new SearchService(mapMgr);
		
		initView();
		
	}
	
	/**
	 * 初始化MapView
	 * 2013-8-2 上午10:22:48
	 * @author houmiao.xiong
	 */
	private void initMapview(){
		mapView = (MapView)findViewById(R.id.bmapsView);
		mapView.setBuiltInZoomControls(true);
		
		MapController mapController = mapView.getController();
		mapController.setZoom(16);
		mapController.enableClick(true);
		
		//搜索框，交通信号灯
		View searchLayout = LayoutInflater.from(this).inflate(R.layout.search_layout, null);
		
		searchLayout.findViewById(R.id.search_btn).setOnClickListener(this);
		trafficView = (ImageView)searchLayout.findViewById(R.id.tranffic_iv);
		trafficView.setOnClickListener(this);
		
		searchEdit = (EditText)searchLayout.findViewById(R.id.search_edit);
		
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 
				0, 0, LayoutParams.TOP_LEFT);
		
		mapView.addView(searchLayout, params);
		mapView.regMapViewListener(mapMgr, this);
	}
	
	
	/**
	 * 初始化定位服务
	 * 2013-8-2 上午10:20:23
	 * @author houmiao.xiong
	 */
	private void initLocClient(){
		locService = new LocateService(this);
		
		locService.start();
		locService.doLocate(new LocateResultCallback() {
			
			@Override
			public void onLocateResult(BDLocation arg0) {
				// TODO Auto-generated method stub
				if(arg0 != null){
					Log.d(TAG, "My Location " + arg0.getAddrStr());
					
					int longitude = (int) (arg0.getLongitude() * 1E6);
					int latitude = (int) (arg0.getLatitude() * 1E6);
					
					Log.d(TAG, "longitude " + longitude
							+ " latitude " + latitude);
					
					LocationData data = new LocationData();
					data.latitude = arg0.getLatitude();
					data.longitude = arg0.getLongitude();
					data.direction = 2.0f;
					
					MyLocationOverlay myLocLay = new MyLocationOverlay(mapView);
					myLocLay.setData(data);
					
					mapView.getOverlays().add(myLocLay);
					mapView.refresh();
					
					myLocGeo = new GeoPoint(latitude, longitude);
					mapView.getController().animateTo(myLocGeo);
					
				}
			}
		});
		
		locService.doReqPoi(new PoiResultCallbck() {
			
			@Override
			public void onPoiResult(BDLocation dbLoc) {
				// TODO Auto-generated method stub
				
				String poi = dbLoc.getPoi();
				Log.d(TAG, "onPoiResult " + poi);
				
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(mapView != null){
			mapView.onResume();
		}
		
		if(mapMgr != null){
			mapMgr.start();
		}
		
		super.onResume();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		if(mapView != null){
			mapView.onPause();
		}
		
		if(mapMgr != null){
			mapMgr.stop();
		}
		
		super.onPause();
	}

	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		Log.e(TAG, "onDestroy");
		
		if(locService != null){
			locService.stop();
			locService = null;
		}
		
		if(mapView != null){
			mapView.destroy();
			mapView = null;
		}
		
		if(mapMgr != null){
			mapMgr.destroy();
			mapMgr = null;
		}
		
		super.onDestroy();
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id == R.id.tranffic_iv){
			changeTranfficView();
		}else if(id == R.id.search_btn){
			doSearchNearby();
		}
	}
	
	
	private void doSearchNearby(){
		if(searchEdit.getText() == null){
			return;
		}
		
		String searchTxt = searchEdit.getText().toString();
		if(searchTxt == null 
				|| searchTxt.length() == 0 
				|| myLocGeo == null){
			return;
		}
		
		searchService.searchPoiNearBy(searchTxt, myLocGeo, 1000, 
				
			new SearchPoiResultCallback(){

				@Override
				public void onGetPoiResult(MKPoiResult result,
						int type, int code) {
					// TODO Auto-generated method stub
					if(code == SearchService.SUCCESS_CODE){
						List<MKPoiInfo> list = result.getAllPoi();
						for(MKPoiInfo info : list){
							Log.d(TAG, "poiInfo " + info.name);
						}
						PoiOverlay poiOverlay = new PoiOverlay(BaiduMapActivity.this, mapView);
						poiOverlay.setData(result.getAllPoi());
						mapView.getOverlays().add(poiOverlay);
						mapView.refresh();
					}else{
						Log.e(TAG, "errcode " + code);
					}
				}
			}
		
		);
	}
	
	private void changeTranfficView(){
		mapView.setTraffic(!mapView.isTraffic());
		
		if(mapView.isTraffic()){
			trafficView.setImageResource(R.drawable.tranficc_on);
		}else{
			trafficView.setImageResource(R.drawable.tranficc_off);
		}
	}
	
	private TextView  popupText = null;
	private View popupInfo = null;
	private View popupLeft = null;
	private View popupRight = null;
	
	private void initView(){
		View viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
        popupInfo = (View) viewCache.findViewById(R.id.popinfo);
        popupLeft = (View) viewCache.findViewById(R.id.popleft);
        popupRight = (View) viewCache.findViewById(R.id.popright);
        popupText =(TextView) viewCache.findViewById(R.id.textcache);
	}
	
	


	@Override
	public void onClickMapPoi(MapPoi arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "mapPoi " + arg0.strText);
		
		GeoPoint point = arg0.geoPt;
		String msg = arg0.strText;
		
		popupText.setText(msg);
		
		PopupOverlay popLay = new PopupOverlay(mapView, 
				new PopupClickListener(){

			@Override
			public void onClickedPopup(int arg0) {
				// TODO Auto-generated method stub
				
				if(arg0 == 1){
					Intent delIntent = new Intent(BaiduMapActivity.this, PoiDetailActivity.class);
					BaiduMapActivity.this.startActivity(delIntent);
				}
				
			}
			
		});
		
		Bitmap[] bitMaps={
			    BMapUtil.getBitmapFromView(popupLeft), 		
			    BMapUtil.getBitmapFromView(popupInfo), 		
			    BMapUtil.getBitmapFromView(popupRight) 		
		 	};
		
		
		popLay.showPopup(bitMaps, point, 0);
		
		mapView.getController().animateTo(point);
	}



	@Override
	public void onGetCurrentMap(Bitmap arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onMapAnimationFinish() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onMapMoveFinish() {
		// TODO Auto-generated method stub
		
	}

}

package com.windo.common.pal.internal;



import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;


public class PalPlatform {

    private static final String TAG=PalPlatform.class.getSimpleName();
	public static boolean isOpenWIFI(Context mContext) {

		WifiManager mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		if (mWifiManager.isWifiEnabled()) {
			if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
				
				System.out.println("-----打开");
				return true;
			} else
				return false;
		} else {
			return false;
		}
	}



	public static boolean isWap(Context context){
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNi = cm.getActiveNetworkInfo();

		if (activeNi != null && activeNi.isConnected()) {
			
			if (PalLog.DEBUG) {
				PalLog.d("PalPaltform", "Active Network Type: " + activeNi.getTypeName());
			}

			int type = activeNi.getType();
			if(type != ConnectivityManager.TYPE_WIFI
					&& type != ConnectivityManager.TYPE_WIMAX){
				return true;
			}
		}
		return false;
	}






	
	
	public static void setWifiEnable(Context context,boolean enable){
		
		WifiManager wifimanager = (WifiManager) context
		.getSystemService(Context.WIFI_SERVICE);
		wifimanager.setWifiEnabled(enable);
	}
	





	
	
	public static String getIMEI(Context mContext) {
		TelephonyManager telephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
//		String imei = "123232ad8232328bf2";
		PalLog.d(TAG, "getIMEI: " + imei);
		return imei;
		
	}

	
	public static String getIMSI(Context mContext) {
		
		TelephonyManager telephonyManager = (TelephonyManager) mContext
		.getSystemService(Context.TELEPHONY_SERVICE);
		
		
		String imsi = telephonyManager.getSubscriberId();
        PalLog.d(TAG,"getImsi:"+imsi);
		return imsi;	
		
	}
	
	

}

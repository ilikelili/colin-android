package com.windo.common.bluetooth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.windo.common.pal.internal.PalLog;


/**
 * 蓝牙管理器
 *     主要负责蓝牙的开\关、(取消)扫描、(移除)配对、可见性设置、数据传输等功能
 * @author houmiao.xiong
 * @email 925399262@qq.com
 * @version 1.0 2013-2-18 下午11:16:36
 */
public class BluetoothMgr extends BaseClass{
	
	private static final String TAG = "BluetoothMgr";
	
	private static final String UUID_STR = "00001101-0000-1000-8000-00805F9B34FB";
	
	public static final UUID UUID_PP = UUID.fromString(UUID_STR);
	
	
	
	/**-------------    ------------------------*/
	
	private static final int PAIRING_VARIANT_PIN = 0;
    
	private static final int PAIRING_VARIANT_PASSKEY = 1;
	    
	private static final int PAIRING_VARIANT_PASSKEY_CONFIRMATION = 2;
	    
	private static final int PAIRING_VARIANT_CONSENT = 3;
	
	private static final String ACTION_PAIRING = "android.bluetooth.device.action.PAIRING_REQUEST";
	
	private static final String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
	
	private static final String EXTRA_PASSKEY = "android.bluetooth.device.extra.PASSKEY";
	
	private static final String ACTION_RELEASE = "com.windo.common.bluetooth.release";
	

	
	
	
	/**-------------  操作类型  ------------------------*/
	//open or close
	private static final byte OPE_TYPE_SWITCH = 0x1;
	//set scan mode
	private static final byte OPE_TYPE_SET_SCANMODE = 0x2;
	//search
	private static final byte OPE_TYPE_SEARCH = 0x3;
	//cancel searching
	private static final byte OPE_TYPE_CANCEL_SEARCH = 0x4;
	//pair
	private static final byte OPE_TYPE_PAIR = 0x5;
	//connect
	private static final byte OPE_TYPE_CONNECT = 0x6;
	//accept
	private static final byte OPE_TYPE_ACCEPT = 0x7;
	//receive
	private static final byte OPE_TYPE_RECEIVE = 0x8;
	//send
	private static final byte OPE_TYPE_SEND = 0x9;
	
	
	
	/**-------------  消息值  ------------------------*/
	
	/** 参数错误  */
	public static final int ERR_ILL_PARAM = 0x1;
	
	/** 打开成功  */
	public static final int MSG_OPEN_SUCCESS = OPE_TYPE_SWITCH << 8 | 0x1;
	/** 已经打开  */
	public static final int ERR_HAS_OPENED = OPE_TYPE_SWITCH << 8 | 0x2;
	/** 正在打开  */
	public static final int MSG_OPEN_ING = OPE_TYPE_SWITCH << 8 | 0x3;
	/** 蓝牙未打开  */
	public static final int ERR_OPEN_FAILED = OPE_TYPE_SWITCH << 8 | 0x4;
	
	
	/** 关闭成功  */
	public static final int MSG_CLOSE_SUCCESS = OPE_TYPE_SWITCH << 8 | 0x11;
	/** 已经关闭  */
	public static final int ERR_HAS_CLOSED = OPE_TYPE_SWITCH << 8 | 0x12;
	/** 正在关闭  */
	public static final int MSG_CLOSE_ING = OPE_TYPE_SWITCH << 8 | 0x13;
	
	
	/** 设置变成可见  */
	public static final int MSG_MOVE_VISIBLE = OPE_TYPE_SET_SCANMODE << 8 | 0x1;
	/** 设置变成不可见  */
	public static final int MSG_MOVE_INVISIBLE = OPE_TYPE_SET_SCANMODE << 8 | 0x2;
	/** 已经设置可见  */
	public static final int ERR_HAS_VISIBLE = OPE_TYPE_SET_SCANMODE << 8 | 0x3;
	
	
	/** 扫描结束  */
	public static final int MSG_SEARCH_FINISH = OPE_TYPE_SEARCH << 8 | 0x1;
	/** 开始扫描  */
	public static final int MSG_SEARCH_START = OPE_TYPE_SEARCH << 8 | 0x2;
	/** 扫描到一设备  */
	public static final int MSG_SEARCH_ONE = OPE_TYPE_SEARCH << 8 | 0x3;
	/** 正在扫描 */
	public static final int ERR_SEARCH_ING = OPE_TYPE_SEARCH << 8 | 0x4;
	
	
	/** 取消搜索成功  */
	public static final int MSG_CANCEL_SEARCH_SUCCESS = OPE_TYPE_CANCEL_SEARCH << 8 | 0x1;
	/** 设备未在扫描  */
	public static final int ERR_CANCEL_SEARCH_FAIL = OPE_TYPE_CANCEL_SEARCH << 8 | 0x2;
	
	
	/** 已经配对  */
	public static final int ERR_PAIR_BONDED = OPE_TYPE_PAIR << 8 | 0x1;
	/** 正在配对(msg)  */
	public static final int MSG_PAIR_BONDING = OPE_TYPE_PAIR << 8 | 0x2;
	/** 正在配对(err)  */
	public static final int ERR_PAIR_BONDING = OPE_TYPE_PAIR << 8 | 0x3;
	/** 已经移除  */
	public static final int ERR_PAIR_UNBOND = OPE_TYPE_PAIR << 8 | 0x4;
	/** 配对成功  */
	public static final int MSG_PAIR_SUCCESS = OPE_TYPE_PAIR << 8 | 0x5;
	/** 配对失败  */
	public static final int ERR_PAIR_FAIL = OPE_TYPE_PAIR << 8 | 0x6;
	/** 移除配对成功  */
	public static final int MSG_REMOVE_PAIR_SUCCESS = OPE_TYPE_PAIR << 8 | 0x7;

	
	/** 连接成功  */
	public static final int MSG_CONNECT_SUCCESS = OPE_TYPE_CONNECT << 8 | 0x1;
	/** 连接失败  */
	public static final int ERR_CONNECT_FAIL = OPE_TYPE_CONNECT << 8 | 0x3;
	/** 连接中断  */
	public static final int ERR_CONNECT_SHUTDOWN = OPE_TYPE_CONNECT << 8 | 0x5;
	/** 中断连接  */
	public static final int MSG_SHUTDOWN_CONNECT = OPE_TYPE_CONNECT << 8 | 0x6;
	
	
	/** 接受成功  */
	public static final int MSG_ACCEPT_SUCCESS = OPE_TYPE_ACCEPT << 8 | 0x1;
	/** 接受失败  */
	public static final int ERR_ACCEPT_FAILED = OPE_TYPE_ACCEPT << 8 | 0x2;
	
	
	/** 接收到蓝牙数据  */
	public static final int MSG_RECEIVED_BT_DATA = OPE_TYPE_RECEIVE << 8 | 0x1;
	/** 接收到NFC数据  */
	public static final int MSG_RECEIVED_NFC_DATA = OPE_TYPE_RECEIVE << 8 | 0x2;
	/** 解析蓝牙数据  */
	public static final int MSG_PARSE_BT_DATA = OPE_TYPE_RECEIVE << 8 | 0x3;
	/** 接收NFC数据错误   */
	public static final int ERR_ON_RECEIVE_NFC = OPE_TYPE_RECEIVE << 8 | 0x4;
	/** 接收蓝牙数据错误   */
	public static final int ERR_ON_RECEIVE_BT = OPE_TYPE_RECEIVE << 8 | 0x5;
	
	
	/** 蓝牙发送数据  */
	public static final int MSG_SEND_DATA = OPE_TYPE_SEND << 8 | 0x1;
	
	
	
	
	/**------------  蓝牙类型值定义   --------------*/
	/** 手机蓝牙  */
	public static final int DEVICE_TYPE_PHONE = BluetoothClass.Device.Major.PHONE;
	/** PC蓝牙  */
	public static final int DEVICE_TYPE_COMPUTER = BluetoothClass.Device.Major.COMPUTER;
	/** 蓝牙耳机  */
	public static final int DEVICE_TYPE_EAR = BluetoothClass.Device.Major.AUDIO_VIDEO;
	/**蓝牙打印机  */
	public static final int DEVICE_TYPE_PRINT = BluetoothClass.Device.Major.IMAGING;
	
	
	private static BluetoothMgr mBluetoolInstance;

	private BluetoothAdapter mBluetoothAdapter;
	
	private TaskWatcher mBluetoothWatcher;
	
	
	
	MyReceiver mReceiver = new MyReceiver(mContext){

		@Override
		public void onMyReceive(final Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(ACTION_PAIRING)){
				print("action: " + ACTION_PAIRING);
				
				int variant = intent.getIntExtra(BluetoothMgr.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
				print("variant: " + variant);
				
				try{
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				
					if(variant == BluetoothMgr.PAIRING_VARIANT_CONSENT
							|| variant == BluetoothMgr.PAIRING_VARIANT_PASSKEY_CONFIRMATION){
						
						ClsUtils.setPairingConfirmation(device.getClass(), device);
						
					}else if(variant == BluetoothMgr.PAIRING_VARIANT_PASSKEY){
						
						int passkey = intent.getIntExtra(BluetoothMgr.EXTRA_PASSKEY, 
								BluetoothDevice.ERROR);
						if(passkey != BluetoothDevice.ERROR){
							ClsUtils.setPasskey(device.getClass(), device, passkey);
						}
						
					}else if(variant == BluetoothMgr.PAIRING_VARIANT_PIN){
						
						int passkey = intent.getIntExtra(BluetoothMgr.EXTRA_PASSKEY, 
								BluetoothDevice.ERROR);
						if(passkey != BluetoothDevice.ERROR){
							ClsUtils.setPin(device.getClass(), device, String.valueOf(passkey));
						}
						
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
				
		}
		
	};
	
	
	private BluetoothMgr(Context context){
		super(context);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(mBluetoothAdapter == null){
			throw new UnsupportedOperationException("The Device can not support BlueTooth!");
		}
		
		if(context == null){
			throw new NullPointerException("context can not be null");
		}
		
		mReceiver.register(ACTION_PAIRING);
	}
	
	
	/**
	 * 构造BluetoothMgr对象
	 * @param context
	 * @return
	 */
	public synchronized static BluetoothMgr getInstance(Context context){
		if(mBluetoolInstance == null){
			mBluetoolInstance = new BluetoothMgr(context);
		}
		return mBluetoolInstance;
	}
	
	
	/**
	 *  注册蓝牙事件监听器
	 * @param watcher
	 */
	public void registerBluetoothWatcher(TaskWatcher watcher){
		mBluetoothWatcher = watcher;
	}
	
	
	
	/**
	 * 获得蓝牙适配器
	 * 
	 * @return
	 */
	public BluetoothAdapter getAdapter(){
		return mBluetoothAdapter;
	}
	
	
	/**
	 * 蓝牙是否开启
	 * @return
	 */
	public boolean isEnable(){
		return mBluetoothAdapter.isEnabled();
	}
	
	
	/**
	 * 蓝牙是否可见
	 * @return
	 */
	public boolean isVisible(){
		return mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
	}
	
	
	/**
	 * 获取已配对蓝牙列表
	 * 
	 * @return  null表示蓝牙未打开，否则返回Set列表
	 */
	public Set<BluetoothDevice> getBondedDevices(){
		if(!mBluetoothAdapter.isEnabled()){
			return null;
		}
		return mBluetoothAdapter.getBondedDevices();
	}
	
	
	/**
	 * 获得本蓝牙设备地址
	 * @return
	 * 2013-2-26 下午3:47:47
	 * @author houmiao.xiong
	 */
	public String getAddress(){
		return mBluetoothAdapter.getAddress();
	}
	
	
	/**
	 * 获取蓝牙类型
	 * 
	 * @param device
	 * 
	 * @return  -1表示获取失败，其他见 DEVICE_TYPE_××× 常量定义
	 */
	public static int getDeviceType(BluetoothDevice device){
		BluetoothClass cls = device.getBluetoothClass();
		if(cls != null){
			return cls.getMajorDeviceClass();
		}else{
			return -1;
		}
	}
	
	

	/**
	 * 开启蓝牙
	 * 
	 * @param watcher 
	 * 		打开蓝牙消息监听器，有以下消息： ERR_HAS_OPENED, MSG_OPEN_ING, 
	 * 		MSG_OPEN_SUCCESS, 其含义见消息常量值定义
	 * 
	 */
	public void openBluetooth(){
		final BluetoothAdapter adapter = mBluetoothAdapter;
		
		int state = adapter.getState();
		
		if(state == BluetoothAdapter.STATE_ON){
			//opened
			notifyError(mBluetoothWatcher, ERR_HAS_OPENED);
			
		}else if(state == BluetoothAdapter.STATE_TURNING_ON){
			//opening
			notifyError(mBluetoothWatcher, MSG_OPEN_ING);
			
		}else{
			//open
			final Context context = mContext;
			
			new MyReceiver(context){

				@Override
				public void onMyReceive(Context context, Intent intent) {
					// TODO Auto-generated method stub
					if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){
						
						int state = adapter.getState();
						if(state == BluetoothAdapter.STATE_ON){
							
							notifyMessage(mBluetoothWatcher, MSG_OPEN_SUCCESS);
							
							unRegister();
						}else if(state == BluetoothAdapter.STATE_TURNING_ON){
							
							notifyMessage(mBluetoothWatcher, MSG_OPEN_ING);
						}
						
					}
				}
				
			}.register(BluetoothAdapter.ACTION_STATE_CHANGED);
			
			adapter.enable();
		}
	}
	
	
	/**
	 * 关闭蓝牙
	 * 
	 * @param watcher
	 * 		关闭蓝牙消息监听器，有以下消息： ERR_HAS_CLOSED, MSG_CLOSE_ING, 
	 * 		MSG_CLOSE_SUCCESS, 其含义见消息常量值定义
	 * 
	 */
	public void closeBluetooth(){
		
		final BluetoothAdapter adapter = mBluetoothAdapter;
		
		int state = adapter.getState();
		if(state == BluetoothAdapter.STATE_OFF){
			//closed
			notifyError(mBluetoothWatcher, ERR_HAS_CLOSED);
			
		}else if(state == BluetoothAdapter.STATE_TURNING_OFF){
			//closing
			notifyError(mBluetoothWatcher, MSG_CLOSE_ING);
			
		}else{
			final Context context = mContext;
			
			new MyReceiver(context){

				@Override
				public void onMyReceive(Context context, Intent intent) {
					// TODO Auto-generated method stub
					if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){
						int state = adapter.getState();
						if(state == BluetoothAdapter.STATE_OFF){
							
							notifyMessage(mBluetoothWatcher, MSG_CLOSE_SUCCESS);
							
							unRegister();
						}else if(state == BluetoothAdapter.STATE_TURNING_OFF){
							
							notifyMessage(mBluetoothWatcher, MSG_CLOSE_ING);
						}
						
					}
				}
				
			}.register(BluetoothAdapter.ACTION_STATE_CHANGED);
			
			adapter.disable();
		}
		
		
	}
	
	/**
	 * 设置蓝牙可见性
	 * 
	 * @param duration 可见的时间段 
	 * 
	 * @param watcher  
	 * 		设置可见性消息监听器，有以下消息： ERR_HAS_CLOSED, ERR_HAS_VISIBLE, 
	 * 		MSG_VISIBLE_SUCCESS, 其含义见消息常量值定义
	 * 
	 */
	public void setVisible(final int duration){
		
		final BluetoothAdapter adapter = mBluetoothAdapter;
		
		int mScanmode = adapter.getScanMode();
		if(!adapter.isEnabled()){
			//not open
			notifyError(mBluetoothWatcher, ERR_HAS_CLOSED);
		}else if(mScanmode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
			//is visiable
			notifyError(mBluetoothWatcher, ERR_HAS_VISIBLE);
		}else{
			final Context context = mContext;
				
			new MyReceiver(context){
				
				@Override
				public void onReceive(Context context, Intent intent) {
					// TODO Auto-generated method stub
					if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(intent.getAction())){
						int mode = adapter.getScanMode();
						
						if(mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
							notifyMessage(mBluetoothWatcher, MSG_MOVE_VISIBLE);
						}else{
							notifyMessage(mBluetoothWatcher, MSG_MOVE_INVISIBLE);
						}
						
					}else if(intent.getAction().equals(ACTION_RELEASE)){
						unRegister();
					}
				}
				
			}.register(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED,ACTION_RELEASE);
				
			
			Intent reqDiscover = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			reqDiscover.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
			
			context.startActivity(reqDiscover);
			
		}
		
		
	}
	
	
	
	/**
	 * 扫描附近的蓝牙
	 * 
	 * @param watcher  
	 *      扫描消息监听器，有以下消息： ERR_HAS_CLOSED, ERR_SEARCH_ING, 
	 * 		MSG_SEARCH_START, MSG_SEARCH_ONE, MSG_SEARCH_FINISH 其含义见消息常量值定义.
	 * 		当扫描到一设备，watcher的obj参数即为device对象
	 * 
	 */
	public void searchNearBluetooths(){
		final BluetoothAdapter adapter = mBluetoothAdapter;
		
		if(!adapter.isEnabled()){
			//not open
			notifyError(mBluetoothWatcher, ERR_HAS_CLOSED);
		}else if(adapter.isDiscovering()){
			//search ing
			notifyError(mBluetoothWatcher, ERR_SEARCH_ING);
		}else {
			final Context context = mContext;
			
			new MyReceiver(context){

				@Override
				public void onMyReceive(Context context, Intent intent) {
					// TODO Auto-generated method stub
					String action = intent.getAction();
					
					if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
						
						notifyMessage(mBluetoothWatcher, MSG_SEARCH_START);
					}else if(BluetoothDevice.ACTION_FOUND.equals(action)){
						
						BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						notifyMessage(mBluetoothWatcher, MSG_SEARCH_ONE, device);
					}else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
						
						notifyMessage(mBluetoothWatcher, MSG_SEARCH_FINISH);
						//search finsh; remove receiver
						unRegister();
					}
				}
				
			}.register(BluetoothAdapter.ACTION_DISCOVERY_STARTED, 
					BluetoothAdapter.ACTION_DISCOVERY_FINISHED, 
					BluetoothDevice.ACTION_FOUND);
				
			
			adapter.startDiscovery();
		}
		
	}
	
	
	/**
	 * 取消扫描
	 * 
	 * @param watcher
	 * 		扫描消息监听器，有以下消息： ERR_HAS_CLOSED, ERR_CANCEL_SEARCH_FAIL, 
	 * 		MSG_CANCEL_SEARCH_SUCCESS 其含义见消息常量值定义
	 *   
	 */
	public void cancelSearchBluetooth(){
		final BluetoothAdapter adapter = mBluetoothAdapter;
		
		if(!adapter.isEnabled()){
			notifyError(mBluetoothWatcher, ERR_HAS_CLOSED);
		}else if(!adapter.isDiscovering()){
			notifyError(mBluetoothWatcher, ERR_CANCEL_SEARCH_FAIL);
		}else{
			final Context context = mContext;
			
			new MyReceiver(context){

				@Override
				public void onReceive(Context context, Intent intent) {
					// TODO Auto-generated method stub
					if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
						notifyMessage(mBluetoothWatcher, MSG_CANCEL_SEARCH_SUCCESS);
						
						unRegister();
					}
				}
				
			}.register(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			
			adapter.cancelDiscovery();
		}
	}
	
	
	/**
	 * 蓝牙配对
	 * 
	 * @param device  配对的蓝牙设备
	 *         
	 * @param watcher  
	 * 		蓝牙配对消息监听器，有以下消息：ERR_ILL_PARAM, ERR_HAS_CLOSED,
	 * 		MSG_PAIR_BONDED, MSG_PAIR_BONDING, ERR_PAIR_BONDING, 
	 * 		MSG_PAIR_SUCCESS, ERR_PAIR_FAIL 其含义见消息常量值定义
	 * 
	 */
	public void pairDevice(final BluetoothDevice device){
		final BluetoothAdapter adpater = mBluetoothAdapter;
		
		if(device == null){
			notifyError(mBluetoothWatcher, ERR_ILL_PARAM);
		}else if(!adpater.isEnabled()){
			notifyError(mBluetoothWatcher, ERR_HAS_CLOSED);
		}else{
			final int state = device.getBondState();
			if(state == BluetoothDevice.BOND_BONDED){
				notifyError(mBluetoothWatcher, ERR_PAIR_BONDED);
			}else if(state == BluetoothDevice.BOND_BONDING){
				notifyError(mBluetoothWatcher, ERR_PAIR_BONDING);
			}else{
				if(adpater.isDiscovering()){
					adpater.cancelDiscovery();
				}
				
				final Context context = mContext;
				
				new MyReceiver(context){

					@Override
					public void onMyReceive(Context context, Intent intent) {
						// TODO Auto-generated method stub
						super.onMyReceive(context, intent);
						
						if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())){
							
							int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
							
							if(state == BluetoothDevice.BOND_BONDING){
								
								notifyMessage(mBluetoothWatcher, MSG_PAIR_BONDING);
								print("pairing");
								
							}else if(state == BluetoothDevice.BOND_BONDED){
								
								notifyMessage(mBluetoothWatcher, MSG_PAIR_SUCCESS);
								print("bonded");
								unRegister();
								
							}else if(state == BluetoothDevice.BOND_NONE){
								
								notifyError(mBluetoothWatcher, ERR_PAIR_FAIL);
								print("pair fail");
								unRegister();
							}
							
						}
						
					}
					
				}.register(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
					
				
				try {
					ClsUtils.createBond(device.getClass(), device);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		
	}
	
	
	/**
	 * 移除配对
	 * 
	 * @param device  移除的蓝牙设备
	 * 
	 * @param watcher  
	 * 		移除配对消息监听器，有以下消息：ERR_ILL_PARAM, ERR_HAS_CLOSED,
	 * 		ERR_PAIR_UNBOND, MSG_REMOVE_PAIR_SUCCESS 其含义见消息常量值定义
	 * 
	 */
	public void removePairDevice(final BluetoothDevice device){
		
		final BluetoothAdapter adapter = mBluetoothAdapter;
		
		if(device == null){
			notifyError(mBluetoothWatcher, ERR_ILL_PARAM);
		}else if(!adapter.isEnabled()){
			notifyError(mBluetoothWatcher, ERR_HAS_CLOSED);
		}else{
			int state = device.getBondState();
			if(state == BluetoothDevice.BOND_NONE){
				notifyError(mBluetoothWatcher, ERR_PAIR_UNBOND);
			}else{
				if(adapter.isDiscovering()){
					adapter.cancelDiscovery();
				}
				
				final Context context = mContext;

				new MyReceiver(context){

					@Override
					public void onReceive(Context context, Intent intent) {
						// TODO Auto-generated method stub
						if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())){
							
							int state = device.getBondState();
							
							if(state == BluetoothDevice.BOND_NONE){
								notifyMessage(mBluetoothWatcher, MSG_REMOVE_PAIR_SUCCESS);
								
								unRegister();
							}
						}
					}
					
				}.register(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

					
				try {
					ClsUtils.removeBond(device.getClass(), device);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
	}
	
	
	
	
	private SocketThread mSocketMgr;
	
	
	/**
	 * 接受蓝牙连接
	 */
	public void doWaitAccept(){
		final BluetoothAdapter adapter = mBluetoothAdapter;
		
		if(!adapter.isEnabled()){
			notifyError(mBluetoothWatcher, BluetoothMgr.ERR_OPEN_FAILED);
		}else{
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					BluetoothSocket tmpSocket = null;
					BluetoothServerSocket serverSocket = null;
					try {
						serverSocket = adapter.listenUsingRfcommWithServiceRecord("Server", 
							BluetoothMgr.UUID_PP);
					
						print("waiting accept");
						tmpSocket = serverSocket.accept();
						print("accepted: " + tmpSocket.getRemoteDevice().getAddress());
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						PalLog.e(TAG, "accept err: " + e.getMessage());
						
						if(tmpSocket != null){
							try {
								tmpSocket.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							tmpSocket = null;
						}
						
						notifyError(mBluetoothWatcher, BluetoothMgr.ERR_ACCEPT_FAILED, e.getMessage());
					} finally{
						
						if(serverSocket != null){
							try {
								serverSocket.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							serverSocket = null;
						}
						
					}
					
					
					if(tmpSocket != null){
						setupSocket(tmpSocket);
						
						notifyMessage(mBluetoothWatcher, BluetoothMgr.MSG_ACCEPT_SUCCESS, tmpSocket.getRemoteDevice());
					}
					
				}
			}).start();
		}
		
	}
	
	
	private void setupSocket(BluetoothSocket tmpSocket){
		SocketThread st = new SocketThread(tmpSocket, mBluetoothWatcher);
		st.start();
		mSocketMgr = st;
	}
	
	
	/**
	 * 连接蓝牙
	 * @param add
	 * @param watcher
	 */
	public void doConnectBt(String add){
		final BluetoothAdapter adapter = mBluetoothAdapter;
		final String address = add;
		
		if(TextUtils.isEmpty(address)
				|| !BluetoothAdapter.checkBluetoothAddress(address)){
			//param ill
			notifyError(mBluetoothWatcher, BluetoothMgr.ERR_ILL_PARAM);
			
		}else if(!adapter.isEnabled()){
			//open bt
			notifyError(mBluetoothWatcher, BluetoothMgr.ERR_OPEN_FAILED);
			
		}else {
			final BluetoothDevice device = adapter.getRemoteDevice(address);
			//connect
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(adapter.isDiscovering()){
						adapter.cancelDiscovery();
					}
					
					BluetoothSocket tmpSocket = null;
					
					try {
						tmpSocket = device.createRfcommSocketToServiceRecord(BluetoothMgr.UUID_PP);
						tmpSocket.connect();
					} catch (Exception ex) {
						ex.printStackTrace();
						PalLog.e(TAG, "connect fail: " + ex.getMessage());
						
						if(tmpSocket != null){
							try {
								tmpSocket.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							tmpSocket = null;
						}
						
						notifyError(mBluetoothWatcher, BluetoothMgr.ERR_CONNECT_FAIL, ex.getMessage());
					}
					
					if(tmpSocket != null){
						print("connect success");
						setupSocket(tmpSocket);
						
						notifyMessage(mBluetoothWatcher, BluetoothMgr.MSG_CONNECT_SUCCESS, device);
					}
					
				}
			}).start();
				
			
		}
		
	}
	
	
	/**
	 * 断开连接
	 */
	public void disConnect(){
		if(mSocketMgr != null){
			mSocketMgr.shutDown();
			mSocketMgr = null;
		}
	}
	
	
	/**
	 * 发送数据
	 * @param bytes
	 */
	public void sendData(byte[] bytes){
		if(mSocketMgr != null){
			try {
				mSocketMgr.writeByteData(bytes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 断开连接，释放资源
	 */
	public void release(){
		Intent release = new Intent(ACTION_RELEASE);
		mContext.sendBroadcast(release);
		
		mReceiver.unRegister();
	}


	@Override
	public String getTag() {
		// TODO Auto-generated method stub
		return TAG;
	}
	
	
	
	/**
	 * 
	 * @author houmiao.xiong
	 * @date 2013-3-1下午03:28:32
	 * @email 925399262@qq.com
	 */
	class SocketThread extends Thread{
		
		private boolean isCancel;
		
		private InputStream socketInputStream;
		private OutputStream socketOutputStream;
		
		private BluetoothSocket mSocket;
		
		private TaskWatcher receivedWatcher;

		SocketThread(BluetoothSocket socket, TaskWatcher watcher){
			try {
				socketInputStream = socket.getInputStream();
				socketOutputStream = socket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			receivedWatcher = watcher;
			mSocket = socket;
		}
		
		
		void doCancel(){
			isCancel = true;
		}
		

		void shutDown(){
			isCancel = true;
			
			if(mSocket != null){
				try {
					mSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					mSocket = null;
				}
			}
			
			if(socketInputStream != null){
				try {
					socketInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					socketInputStream = null;
				}
			}
			
			if(socketOutputStream != null){
				try {
					socketOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					socketOutputStream = null;
				}
			}
			
			notifyMessage(receivedWatcher, BluetoothMgr.MSG_SHUTDOWN_CONNECT);
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			while (!isCancel) {
				
				try {
					readByteData();
					Thread.sleep(1000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					print("e.error: " + e.getMessage());
					
					shutDown();
				}
				
			}
			
		}
		
		//读字节流
		private void readByteData() throws Exception{
			final InputStream ips = socketInputStream;
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[2*1024];
			int num = -1;
			if((num = ips.read(buf)) != -1){
				baos.write(buf, 0, num);
				baos.flush();
			}
			
			byte[] bytes = baos.toByteArray();
			if(bytes != null && bytes.length > 0){
				notifyMessage(receivedWatcher, BluetoothMgr.MSG_RECEIVED_BT_DATA, bytes);
			}
			
			baos.close();
			baos = null;
		}
		
		
		//写入字节流
		public void writeByteData(byte[] bytes) {
			print("writeByteData");
			final OutputStream ops = socketOutputStream;
			
//			byte[] bytes = data.getBytes();
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			
			int len = bais.available();
			
			print("len: " + len);
			
			byte[] buf = new byte[1024];
			int num = 0;
			int sum = 0;
			
			try{
				while((num = bais.read(buf)) != -1 && sum <= len){
					ops.write(buf, 0 , num);
					sum += num;
				}
				ops.flush();
				
				print("sum: " + sum);
				bais.close();
				bais = null;
			}catch(Exception ex){
				ex.printStackTrace();
	
				shutDown();
			}
			
			
		}
		
	}
	
}

package com.windo.common.bluetooth;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.windo.common.pal.internal.PalLog;

/**
 * 反射工具类
 * @author houmiao.xiong
 * @email 925399262@qq.com
 * @version 1.0 2013-2-19 上午10:15:45
 */
public class ClsUtils {

	private static final String TAG = "ClsUtils";
	
	
	
	/**----------------- bluetooth  ---------------*/
	

	/**
	 * 蓝牙配对
	 * @param btClass
	 * @param btDevice
	 * @return
	 * @throws Exception
	 */
	public static boolean createBond(Class<?> btClass, BluetoothDevice btDevice)
			throws Exception {
		Method createBondMethod = btClass.getMethod("createBond");
		Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
		print("createBond: " + returnValue);
		return returnValue.booleanValue();
	}


	/**
	 * 移除配对
	 * @param btClass
	 * @param btDevice
	 * @return
	 * @throws Exception
	 */
	public static boolean removeBond(Class<?> btClass, BluetoothDevice btDevice)
			throws Exception {
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		print("removeBond: " + returnValue);
		return returnValue.booleanValue();
	}

	
	/**
	 * 设置配对PIN
	 * @param btClass
	 * @param btDevice
	 * @param pin
	 * @return
	 * @throws Exception
	 */
	public static boolean setPin(Class<?> btClass, BluetoothDevice btDevice,
			String pin) throws Exception {
		try {
			Method setPinMethod = btClass.getDeclaredMethod("setPin",
					new Class[] { byte[].class });
			Boolean returnValue = (Boolean) setPinMethod.invoke(btDevice,
					new Object[] { pin.getBytes() });
			print("setPin: " + returnValue);
		} catch (SecurityException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}
	
	
	/**
	 * 设置配对passkey
	 * @param btClass
	 * @param btDevice
	 * @param passkey
	 * @return
	 * @throws Exception
	 */
	public static boolean setPasskey(Class<?> btClass, BluetoothDevice btDevice,
			int passkey) throws Exception {
		try {
			Method setPasskeyMethod = btClass.getDeclaredMethod("setPasskey",int.class);
			Boolean returnValue = (Boolean) setPasskeyMethod.invoke(btDevice,passkey);
			print("setPasskey: " + returnValue);
		} catch (SecurityException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}
	
//	/**
//	 * 
//	 * @param btClass
//	 * @param device
//	 * @return
//	 * @throws Exception
//	 */
//	public static boolean cancelPairingUserInput(Class<?> btClass,
//			BluetoothDevice device) throws Exception {
//		Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
//		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
//		print("cancelPairingUserInput: " + returnValue);
//		return returnValue.booleanValue();
//	}
	
	
	/**
	 * 设置配对确认
	 * @param btClass
	 * @param device
	 * @return
	 * @throws Exception
	 */
	public static boolean setPairingConfirmation(Class<?> btClass,
			BluetoothDevice device) throws Exception {
		Method createBondMethod = btClass.getMethod("setPairingConfirmation", boolean.class);
		Boolean returnValue = (Boolean) createBondMethod.invoke(device, true);
		print("setPairingConfirmation: " + returnValue);
		return returnValue.booleanValue();
	}

	
	/**
	 * 取消进行中的配对
	 * @param btClass
	 * @param device
	 * @return
	 * @throws Exception
	 */
	public static boolean cancelBondProcess(Class<?> btClass,
			BluetoothDevice device) throws Exception {
		Method createBondMethod = btClass.getMethod("cancelBondProcess");
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
		print("cancelBondProcess: " + returnValue);
		return returnValue.booleanValue();
	}

	
	/**
	 * 设置蓝牙模式
	 * @param adapter
	 * @param mode
	 * @param duration
	 * @return
	 * @throws Exception
	 */
	public static boolean setScanMode(BluetoothAdapter adapter, int mode,
			int duration) throws Exception {
		Method setScanMode = adapter.getClass().getDeclaredMethod(
				"setScanMode", new Class[] { int.class, int.class });
		setScanMode.setAccessible(true);
		Boolean returnValue = (Boolean) setScanMode.invoke(adapter,
				new Object[] { mode, duration });
		return returnValue.booleanValue();
	}
	
	
	
	
//	/**----------------- nfc  ---------------*/
//	
//	/**
//	 * 获取nfc状态
//	 * @param adapter
//	 * @return
//	 * @throws Exception
//	 */
//	public static int getNfcState(NfcAdapter adapter) throws Exception{
//		Method getNfcState = adapter.getClass().getDeclaredMethod("getAdapterState");
//		int state = (Integer)getNfcState.invoke(adapter);
//		print("getNfcState: " + state);
//		return state;
//	}
//	
//	
//	/**
//	 * 打开NFC
//	 * @param adapter
//	 * @return
//	 * @throws Exception
//	 */
//	public static boolean enableNfc(NfcAdapter adapter) throws Exception{
//		Method enable = adapter.getClass().getDeclaredMethod("enable");
//		boolean value = (Boolean)enable.invoke(adapter);
//		print("enableNfc: " + value);
//		return value;
//	}
//	
//	
//	/**
//	 * 关闭NFC
//	 * @param adapter
//	 * @return
//	 * @throws Exception
//	 */
//	public static boolean disableNfc(NfcAdapter adapter) throws Exception{
//		Method disable = adapter.getClass().getDeclaredMethod("disable");
//		boolean value = (Boolean)disable.invoke(adapter);
//		print("disableNfc: " + value);
//		return value;
//	}
	
	
	
	/**
	 * 获取全部方法名
	 * @param clsShow
	 */
	public static void getAllParam(Class<?> cls) {
		try {
			Method[] hideMethod = cls.getMethods();
			for (int i = 0; i < hideMethod.length; i++) {
				print("method name" + hideMethod[i].getName() + ";and the i is:"
						+ i);
			}
			
			Field[] allFields = cls.getFields();
			for (int i = 0; i < allFields.length; i++) {
				print("Field name" + allFields[i].getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void print(String msg){
		PalLog.d(TAG, msg);
	}
	
}

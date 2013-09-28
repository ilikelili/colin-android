package com.windo.common.util;

import java.util.List;

import android.app.PendingIntent;
import android.telephony.SmsManager;


public class SMSSender {

	
	public static void sendSms(String telNum, String message) {
        sendSms(telNum, message,null,null);
	}

    public static void sendSms(String telNum, String message,PendingIntent sentIntent, PendingIntent deliveryIntent) {
        if (message != null) {
            SmsManager smsMgr = SmsManager.getDefault();
            List<String> texts = smsMgr.divideMessage(message);
            for (String text : texts) {
                smsMgr.sendTextMessage(telNum, null, text, sentIntent, deliveryIntent);
            }
        }else{
            throw new IllegalArgumentException();
        }
    }
}

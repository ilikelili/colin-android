package com.windo.common.pal;

import java.io.IOException;


public interface IPlatform {

	
	public boolean isQwertyKeyboard();
	

	
	
	public boolean isSupportTouch();
	
	
	
	public String getPhoneIMEI();
	
	
	
	public String getPhoneOS();
	
	
	public String getPhonoeOSVersion();
	

	
	
	public String getPhoneModels();
	
	
	
	
	public String[] getCurrentLocation() ;
	
	
	
	public boolean isCurrentCDMA() ;
	
	
	public int getCellid() ;
	
	
	public int getMNC() ;
	
	
	public int getMCC() ;
	
	
	public int getLAC() ;
	
	
	public int getSID() ;
	
	
	public int getNID() ;
	
	
	public int getBID() ;
	
	
	
	
	
	
	public long timeString2Long(String timeStr);
	
	
	
	
	public byte[] gzipCompress(byte[] data) throws IOException ;
	
	
	public byte[] gzipDecompress(byte[] data) throws IOException ;
}

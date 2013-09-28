package com.windo.common.crypto;

public class BCDEncoder {

	
	public static byte[] encode(String s) {
		int i = 0, j = 0;
		int max = s.length() - (s.length() % 2);
		
		byte[] buf = new byte[(s.length() + (s.length() % 2)) / 2];
		while (i < max) {
			buf[j++] = (byte) ((((s.charAt(i++) - '0') << 4) | (s.charAt(i++) - '0')));
		}
		if ((s.length() % 2) == 1) { 
			buf[j] = (byte) ((s.charAt(i++) - '0') << 4 | 0x0A);
		}
		return buf;
	}
	
	
	
	public static byte[] encode(String s,byte[] buf,int offset){
		int i = 0, j = 0;
		int max = s.length() - (s.length() % 2);
		
		int size = (s.length() + (s.length() % 2)) / 2;
		if(size > buf.length - offset){
			buf =  new byte[size + offset]; 
		}
		j = offset;
		while (i < max) {
			buf[j++] = (byte) ((((s.charAt(i++) - '0') << 4) | (s.charAt(i++) - '0')));
		}
		if ((s.length() % 2) == 1) { 
			buf[j] = (byte) ((s.charAt(i++) - '0') << 4 | 0x0A);
		}
		return buf;
	}
	

	
	public static String decode(byte[] b) {
		StringBuffer buf = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; ++i) {
			buf.append((char) (((b[i] & 0xf0) >> 4) + '0'));
			if ((i != b.length) && ((b[i] & 0xf) != 0x0A)) 
				buf.append((char) ((b[i] & 0x0f) + '0'));
		}
		return buf.toString();
	}
	
	
	public static String decode(byte[] b,int offset,int len){
		if(offset < 0 || len <= 0 || offset + len > b.length ){
			throw new IllegalArgumentException();
		}
		StringBuffer buf = new StringBuffer(len * 2);
		for (int i = offset; i < offset + len; ++i) {
			buf.append((char) (((b[i] & 0xf0) >> 4) + '0'));
			if ((i != b.length) && ((b[i] & 0xf) != 0x0A)) 
				buf.append((char) ((b[i] & 0x0f) + '0'));
		}
		return buf.toString();
	}
	
}

package com.windo.common.crypto;

public class SHA1 {
	
	
	public static String digest(String string) {
		try {
			byte[] b = string.getBytes("utf-8");
			SHA1Digest digest = new SHA1Digest();
			byte[] out = new byte[digest.getDigestSize()];
			digest.update(b, 0, b.length);
			digest.doFinal(out,0);
			String shaHaxStr = new String(Hex.encode(out));
			return shaHaxStr;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
	}

}

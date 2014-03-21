package cn.easysw.binxml;

import java.io.DataInputStream;

/**
 * bixml 的工具类
 * 
 * @author thomescai@163.com
 * @version 2011-7-28
 */
public class BinxmlUtil {

	/**
	 * 获取节点名字
	 * 
	 * @param dis
	 * @return
	 * @throws Exception
	 */
	 static String readNodeName(DataInputStream dis) {
		StringBuffer strb = new StringBuffer();
		int b;
		try {
			while ((b = dis.read()) != -1) {
				if (b != 0) {
					strb.append((char) b);
				} else {
					return strb.toString();
				}
			}
		} catch (Exception e) {
			return "";
		}
		return strb.toString();
	}

}

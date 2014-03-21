package cn.easysw.binxml;


import java.io.DataInputStream;
import java.io.UnsupportedEncodingException;

import cn.easysw.binxml.BinxmlElement;

import cn.easysw.binxml.BinxmlAttr;
import cn.easysw.binxml.BinxmlUtil;


/**
 * bixml属性类
 * @author thomescai@163.com
 * @version 2011-7-28
 */
public class BinxmlAttr {

	public byte flag;  /*属性flag标志 */
	public int vlen;  
	
	public String name; 
	public byte[] value; 
	
	
	public BinxmlAttr(){
		
	}
	
	public BinxmlAttr(String name,String value){
		this.name = name;
		try {
			if (value != null){
				this.value = value.getBytes("utf-8");
			}else{
				this.value = "".getBytes();
			}
		} catch (UnsupportedEncodingException e) {
			this.value = "".getBytes();
		}
		this.flag = BinxmlElement.EMP_VALUE_STR;
	}
	
	
	
	public static BinxmlAttr parse(DataInputStream drs) throws Exception{
		if(drs == null)
			return null;
		BinxmlAttr attr = new BinxmlAttr();
		
		attr.name = BinxmlUtil.readNodeName(drs);
		attr.flag = drs.readByte();
		
//		if(attr.flag== BixmlElement.EMP_VALUE_INT){  //int
//			int num = drs.readShort();
//			attr.vlen = num;
//			drs.readByte();
//			attr.value = new byte[num];
//			drs.read(attr.value, 0, num);
//		}else if( ((attr.flag & BixmlElement.EMP_VALUE_BINARY )== BixmlElement.EMP_VALUE_BINARY ) ){// bin|string
//			attr.vlen = drs.readShort();
//			attr.value = new byte[attr.vlen];
//			drs.read(attr.value, 0, attr.vlen);
//		}
		
		int num = drs.readShort();
		attr.vlen = num;
		attr.value = new byte[num];
		drs.read(attr.value, 0, num);
		return attr;
	}
}

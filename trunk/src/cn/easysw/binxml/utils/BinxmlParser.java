/**
 * Project Name:binxml_j2se
 * File Name:BinxmlParser.java
 * Package Name:cn.easysw.binxml
 * Date:2013年9月17日下午3:26:30
 * Copyright (c) 2013, www.windo-soft.com All Rights Reserved.
 *
*/

package cn.easysw.binxml.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.easysw.binxml.BinxmlElement;
import cn.easysw.binxml.IBinxml;

/**
 * ClassName:BinxmlParser <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年9月17日 下午3:26:30 <br/>
 * @author   Roy.Wong
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class BinxmlParser {

	
	
	
	/**
	 * 
	 * parseIBinxml:将binxml解析成ibinxml数据. <br/>
	 *
	 * @author Roy.Wong
	 * @param ele
	 * @return
	 * @since JDK 1.6
	 */
	public static IBinxml parseIBinxmlElement(BinxmlElement ele){
		String cls = ele.getStringAttribute("n");
		return parseIBinxmlElement(ele,cls);
	}

	/**
	 * 
	 * parseIBinxml:将binxml解析成ibinxml数据. <br/>
	 * 
	 * @author Roy.Wong
	 * @param ele		binxml元素
	 * @param classname ibinxml类
	 * @return
	 * @since JDK 1.6
	 */
	public static IBinxml parseIBinxmlElement(BinxmlElement ele ,String classname ){
		if (classname != null && !classname.equals("")) {
			IBinxml obj;
			try {
				obj = (IBinxml) Class.forName(classname).newInstance();
				obj.parseBinxml(ele);
				return obj;
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	/**
	 * 
	 * getElementList:解析列表 <br/>
	 * 
	 * @author Roy.Wong
	 * @param ele
	 * @return
	 * @since JDK 1.6
	 */
	public static List<Object> parseListElement(BinxmlElement ele) {
		ArrayList<Object> list = new ArrayList<Object>();
		String t = ele.getStringAttribute("t");
	
		if (t != null && t.equals("xml")) {
			String n = ele.getStringAttribute("n");
			for (int i = 0; i < ele.getElementCount(); i++) {
				Object object = parseIBinxmlElement(ele.getElement(i),n );
				if ( object != null )
					list.add(object);
			}
		} else {
			for (int i = 0; i < ele.getElementCount(); i++) {
				BinxmlElement child = ele.getElement(i);
				Object obj = parseElement(child);
				list.add(obj);
			}
		}
		return list;
	}

	/**
	 * 
	 * parseElementMap:解析成Map. <br/>
	 *
	 * @author Roy.Wong
	 * @param ele
	 * @return
	 * @since JDK 1.6
	 */
	public static HashMap<String, Object> parseMapElement(BinxmlElement ele) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < ele.getElementCount(); i++) {
			BinxmlElement child = ele.getElement(i);
			String name = child.getName();
			Object obj = parseElement(child);
			if (name != null && obj != null)
				map.put(name, obj);
		}
		return map;
	}

	
	/**
	 * 把binxml对象转换成Bean
	 * 
	 * @param ele
	 *            binxml 对象
	 * @return 对象
	 */
	public static Object parseElement(BinxmlElement ele) {
		if (ele != null) {
			String type = ele.getStringAttribute("t");
			if (type.equals("str"))
				return ele.getText();
			else if (type.equals("int"))
				return new Integer(ele.getInt());
			else if (type.equals("bin"))
				return ele.getBin();
			else if (type.equals("lst"))
				return parseListElement(ele.getFirstElement());
			else if (type.equals("map"))
				return parseMapElement(ele);
			else if (type.equals("xml")) 
				return parseIBinxmlElement(ele);
		}
		return null;
	}
	

	
	
}


/**
 * Project Name:binxml_j2se
 * File Name:BinxmlBuilder.java
 * Package Name:cn.easysw.binxml
 * Date:2013年9月17日下午3:13:13
 * Copyright (c) 2013, www.windo-soft.com All Rights Reserved.
 *
 */

package cn.easysw.binxml.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.easysw.binxml.BinxmlAttr;
import cn.easysw.binxml.BinxmlElement;
import cn.easysw.binxml.BinxmlException;
import cn.easysw.binxml.IBinxml;

/**
 * ClassName:BinxmlBuilder <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2013年9月17日 下午3:13:13 <br/>
 * 
 * @author Roy.Wong
 * @version
 * @since JDK 1.6
 * @see
 */
public class BinxmlBuilder {

	/**
	 * 
	 * createBinxmlElement:创建binxmlElement结点. <br/>
	 * 
	 * @author Roy.Wong
	 * @param name
	 * @param obj
	 * @return
	 * @since JDK 1.6
	 */

	public static <T> BinxmlElement createBinxmlElement(String name, T obj) {
		if (obj instanceof String) {
			BinxmlElement ele = new BinxmlElement(name, (String) obj);
			BinxmlAttr attr = new BinxmlAttr("t", "str");
			ele.addAttribute(attr);
			return ele;

		} else if (obj instanceof Number) {
			
			if(obj instanceof Integer){
				BinxmlElement ele = new BinxmlElement(name,
						String.valueOf(((Integer) obj).intValue()));
				BinxmlAttr attr = new BinxmlAttr("t", "int");
				ele.addAttribute(attr);
				return ele;
			}else{
				BinxmlElement ele = new BinxmlElement(name,	obj.toString());
				BinxmlAttr attr = new BinxmlAttr("t", "str");
				ele.addAttribute(attr);
				return ele;
			}
		}else if(obj instanceof Boolean){
			
			BinxmlElement ele = new BinxmlElement(name,
					((Boolean)obj).booleanValue()?1:0);
			BinxmlAttr attr = new BinxmlAttr("t", "int");
			ele.addAttribute(attr);
			return ele;
		}else if (obj instanceof byte[]) {
			BinxmlElement ele = new BinxmlElement(name, (byte[]) obj);
			BinxmlAttr attr = new BinxmlAttr("t", "bin");
			ele.addAttribute(attr);
			return ele;
		} else if (obj instanceof List<?>) {
			BinxmlElement ele = createListBinxmlElement(name, (List<?>) obj);
			BinxmlAttr attr = new BinxmlAttr("t", "lst");
			ele.addAttribute(attr);
			return ele;
		} else if (obj instanceof Map<?, ?>) {
			@SuppressWarnings("unchecked")
			BinxmlElement ele = createMapBinxmlElement(name,
					(Map<String, Object>) obj);
			BinxmlAttr attr = new BinxmlAttr("t", "map");
			ele.addAttribute(attr);
			return ele;
		} else if (obj instanceof IBinxml) {
			BinxmlElement ele = ((IBinxml) obj).toBinxml(name);
			BinxmlAttr typeAttr = new BinxmlAttr("t", "xml");
			String clsname = obj.getClass().getName();

			BinxmlAttr clsAttr = new BinxmlAttr("n", clsname);
			ele.addAttribute(typeAttr);
			ele.addAttribute(clsAttr);
			return ele;
		}
		return null;
	}

	/**
	 * 
	 * getMapElement:创建一个Map型结点. <br/>
	 * 
	 * @author Roy.Wong
	 * @param name
	 * @param map
	 * @return
	 * @since JDK 1.6
	 */
	public static BinxmlElement createMapBinxmlElement(String name,
			Map<String, Object> map) {
		BinxmlElement root = new BinxmlElement(name);
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Object obj = map.get(key);
			BinxmlElement be = createBinxmlElement(key, obj);
			if (be != null)
				root.addElement(be);
		}
		return root;
	}

	/**
	 * 
	 * createListBinxmlElement:创建List型结点. <br/>
	 * 
	 * @author Roy.Wong
	 * @param name
	 * @param list
	 * @return
	 * @since JDK 1.6
	 */
	public static BinxmlElement createListBinxmlElement(String name,
			List<?> list) {
		BinxmlElement root = new BinxmlElement(name);
		if (list != null && list.size() > 0) {
			Iterator<?> it = list.iterator();
			if (list.get(0) instanceof IBinxml) {
				BinxmlAttr attr = new BinxmlAttr("t", "xml");
				String clsname = list.get(0).getClass().getName();
				BinxmlAttr attr2 = new BinxmlAttr("n", clsname);
				root.addAttribute(attr);
				root.addAttribute(attr2);
			}

			while (it.hasNext()) {
				Object obj = it.next();
				BinxmlElement ele = null;
				if (obj instanceof IBinxml) {
					ele = ((IBinxml) obj).toBinxml(name);
				} else {
					ele = createBinxmlElement(name, obj);
				}
				if (ele != null) {
					root.addElement(ele);
				}
			}
		}
		return root;
	}

	public static BinxmlElement build(byte[] data) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		return build(dis, (byte) 0);
	}

	public static BinxmlElement build(DataInputStream drs, byte limit)
			throws Exception {
		return BinxmlElement.parse(drs, limit);
	}

	public static BinxmlElement buildArrayToRoot(byte[] data) throws Exception {
		return buildArrayToRoot(data, new BinxmlElement("root"));
	}

	public static BinxmlElement buildArrayToRoot(byte[] data, BinxmlElement root)
			throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		return buildArrayToRoot(dis, (byte) 0, new BinxmlElement("root"));
	}

	public static BinxmlElement buildArrayToRoot(DataInputStream drs,
			byte limit, BinxmlElement root) throws Exception {
		if (drs == null)
			throw new BinxmlException("DataInputStream is null");
		if (root == null) {
			root = new BinxmlElement("root");
		}
		boolean flag = true;
		while (flag) {
			BinxmlElement childNode = BinxmlElement.parse(drs, limit);
			if (childNode != null && !"".equals(childNode.name)) {
				root.addElement(childNode);
			} else {
				flag = false;
			}
		}
		return root;
	}

}

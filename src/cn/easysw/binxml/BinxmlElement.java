package cn.easysw.binxml;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import cn.easysw.binxml.BinxmlAttr;
import cn.easysw.binxml.BinxmlElement;
import cn.easysw.binxml.BinxmlException;
import cn.easysw.binxml.BinxmlUtil;
import cn.easysw.common.crypto.encoders.Hex;

/**
 * bixml节点
 * 
 * @author thomescai@163.com
 * @version 2011-7-28
 */
public class BinxmlElement {
	public static final int EMP_HAVE_ATTR = 0x80; // 当前节点是否有属性的标志位
	public static final int EMP_HAVE_CHILD = 0x40; // 当前节点是否有子节点的标志位

	public static final int EMP_VALUE_INT = 0x20; // int数据
	public static final int EMP_VALUE_UINT_ONE = 0x21;
	public static final int EMP_VALUE_UINT_TWO = 0x22;
	public static final int EMP_VALUE_UINT_FOUR = 0x24; // 四字节无符号整形数据

	public static final int EMP_VALUE_BINARY = 0x10; // bin数据
	public static final int EMP_VALUE_STR = 0x11;

	public byte flag;
	public String name = "";
	public byte[] value;
	private int tempChild;

	
	
	 Vector<BinxmlElement> nodeList = new Vector<BinxmlElement>();

	 Hashtable<String, BinxmlAttr> attrList = new Hashtable<String, BinxmlAttr>();
	
	public BinxmlElement() {

	}



	public BinxmlElement(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param name
	 *            节点名称
	 * @param bin
	 *            二进制数据
	 */
	public BinxmlElement(String name, byte[] bin) {
		this.name = name;
		this.flag = EMP_VALUE_BINARY;
		this.value = bin;
	}

	public void removeAllChild() {
		nodeList.removeAllElements();
	}

	/**
	 * 
	 * @param name
	 *            节点名称
	 * @param str
	 *            字符串数据
	 */
	public BinxmlElement(String name, String str) {
		this.name = name;
		this.flag = EMP_VALUE_STR;
		try {
			if (str != null)
				this.value = str.getBytes("utf-8");
			else
				this.value = "".getBytes();
		} catch (UnsupportedEncodingException e) {
			this.value = "".getBytes();
		}
	}

	/**
	 * @param name
	 *            节点名称
	 * @param str
	 *            4位int数据
	 */
	public BinxmlElement(String name, int num) {
		this(name, EMP_VALUE_UINT_FOUR, num);
	}

	/**
	 * 
	 * @param name
	 *            节点名称
	 * @param value
	 *            boolean类型：ture=1;false=0;
	 */
	public BinxmlElement(String name, boolean value) {
		this(name, EMP_VALUE_UINT_ONE, value ? 1 : 0);
	}

	/**
	 * 
	 * @param name
	 *            节点名称
	 * @param flag
	 *            节点标示
	 * @param num
	 *            int数据
	 */
	public BinxmlElement(String name, int flag, int num) {
		this.name = name;
		this.flag = (byte) flag;
		if ((flag & EMP_VALUE_UINT_FOUR) == EMP_VALUE_UINT_FOUR) {
			this.value = new byte[4];
			this.value[0] = (byte) (num & 0xff);
			this.value[1] = (byte) ((num >> 8) & 0xff);
			this.value[2] = (byte) ((num >> 16) & 0xff);
			this.value[3] = (byte) ((num >> 24) & 0xff);
		} else if ((flag & EMP_VALUE_UINT_TWO) == EMP_VALUE_UINT_TWO) {
			this.value = new byte[2];
			this.value[0] = (byte) (num & 0xff);
			this.value[1] = (byte) ((num >> 8) & 0xff);
		} else if ((flag & EMP_VALUE_UINT_ONE) == EMP_VALUE_UINT_ONE) {
			this.value = new byte[] { (byte) (num & 0xff) };
		}
	}

	/**
	 * 根部解析
	 * 
	 * @param drs
	 * @param limit
	 *            长度
	 * @return
	 * @throws Exception
	 */
//	public static BinxmlElement rootparse(DataInputStream drs, byte limit)
//			throws Exception {
//		if (drs == null)
//			throw new BinxmlException("DataInputStream is null");
//		BinxmlElement node = new BinxmlElement("root");
//		boolean flag = true;
//		while (flag) {
//			BinxmlElement childNode = BinxmlElement.parse(drs, limit);
//			if (childNode != null && !"".equals(childNode.name)) {
//				node.nodeList.addElement(childNode);
//			} else {
//				flag = false;
//			}
//		}
//		return node;
//	}

	/**
	 * 解析节点
	 * 
	 * @param drs
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public static BinxmlElement parse(DataInputStream drs, byte limit)
			throws Exception {
		if (drs == null)
			throw new BinxmlException("parse error,The input stream is empty.");

		BinxmlElement node = new BinxmlElement();
		node.name = BinxmlUtil.readNodeName(drs);
		if ("".equals(node.name)) {
			return null;
		}
		node.flag = drs.readByte();
		if (((node.flag & EMP_VALUE_BINARY) == EMP_VALUE_BINARY)) { // bin
			int len = drs.readShort();
			if (len > 0) {
				node.value = new byte[len];
				drs.read(node.value, 0, len);
			}
		} else if ((node.flag & EMP_VALUE_INT) == EMP_VALUE_INT) { // int
			int keyvalue = drs.readShort();
			node.value = new byte[keyvalue];
			drs.read(node.value, 0, keyvalue);
		} else { // 为空，表示有子节点
			drs.readShort();
		}

		if ((node.flag & EMP_HAVE_ATTR) == EMP_HAVE_ATTR) { // 节点带有属性
			int keyAttr = drs.readShort();
			for (int i = 0; i < keyAttr; i++) {
				BinxmlAttr childNode = BinxmlAttr.parse(drs);
				if (childNode != null) {
					// node.attrList.addElement(childNode);
					node.attrList.put(childNode.name, childNode);
				}
			}
		}
		if ((node.flag & EMP_HAVE_CHILD) == EMP_HAVE_CHILD) { // 有子节点

			node.tempChild = drs.readShort();
			for (int i = 0; i < node.tempChild; i++) {
				BinxmlElement childNode = BinxmlElement.parse(drs, limit);
				if (childNode != null)
					node.nodeList.addElement(childNode);
			}
		}
		return node;
	}

	/**
	 * 返回指定的路径下的子结点，不存在返回null
	 * 
	 * @param path
	 *            example: a/b
	 * @return
	 */
	public BinxmlElement getNodeByPath(String pathStr) {
		if (pathStr == null)
			return null;
		String[] path = pathStr.split("[\\\\/]");
		BinxmlElement node = this;
		for (int i = 0; i < path.length; i++) {
			if(path[i].length() > 0){
				node = node.getElementByName(path[i]);
				if(node == null){
					return null;
				}	
			}
		}
		return node;
	}
	
	
	/**
	 * 返回指定的路径下的子结点列表，不存在返回null
	 * 
	 * @param path
	 *            example: a/b
	 * @return
	 */
	public BinxmlElement[] getNodeArrayByPath(String pathStr) {
		if (pathStr == null)
			return null;
		String[] path = pathStr.split("[\\\\/]");
		BinxmlElement node = this;
		for (int i = 0; i < path.length - 1; i++) {
			if(path[i].length() > 0){
				node = node.getElementByName(path[i]);
				if(node == null){
					return null;
				}	
			}
		}
		return node.getElementArrayByName(path[path.length - 1]);
	}
	
	

	/**
	 * 增加节点
	 * 
	 * @param element
	 */
	public void addElement(BinxmlElement element) {
		if (element != null) {
			nodeList.addElement(element);
			// 增加flag标示
			flag = (byte) (flag | BinxmlElement.EMP_HAVE_CHILD);
			
		}
	}


	/**
	 * 获取下一个路径
	 * 
	 * @param pathStr
	 * @return
	 */
	public String getNextPath(String pathStr) {
		int index = pathStr.indexOf("/");
		if (index > 0) {
			return pathStr.substring(index + 1, pathStr.length());
		} else {
			return null;
		}
	}

	/**
	 * 打印节点列表
	 */
	public void printNodeList() {
		for (int i = 0; i < nodeList.size(); i++) {
			BinxmlElement node = (BinxmlElement) nodeList.elementAt(i);
			System.out.println("name:" + node.name);
		}
	}

	/**
	 * 返回第一个子结点
	 * 
	 * @return
	 */
	public BinxmlElement getFirstElement() {
		if (nodeList.size() > 0)
			return (BinxmlElement) nodeList.elementAt(0);
		return null;
	}

	/**
	 * 根据名称，获取第一个节点。
	 * 
	 * @param name
	 * @return
	 */
	public BinxmlElement getElementByName(String name) {
		int size = nodeList.size();
		for (int i = 0; i < size; i++) {
			BinxmlElement node = (BinxmlElement) nodeList.elementAt(i);
			if (name.equals(node.name)) {
				return node;
			}
		}
		return null;
	}
	
	/**
	 *
	 * getElementArrayByName:根据节点名称，返回相同名称所有节点. <br/>
	 *
	 *
	 * @author Roy.Wong
	 * @param name
	 * @return
	 * @since JDK 1.6
	 */
	public BinxmlElement[] getElementArrayByName(String name){
		int size = nodeList.size();
		List<BinxmlElement> rlt = new ArrayList<BinxmlElement>();
		for (int i = 0; i < size; i++) {
			BinxmlElement node = (BinxmlElement) nodeList.elementAt(i);
			if (name.equals(node.name)) {
				rlt.add(node);
			}
		}
		return rlt.toArray(null);
	}

	
	/**
	 * 获取节点
	 * 
	 * @param index
	 * @return
	 */
	public BinxmlElement getElement(int index) {
		if (index > nodeList.size()) {
			return null;
		} else {
			return (BinxmlElement) nodeList.elementAt(index);
		}
	}



	/**
	 * 返回子结点个数.
	 * 
	 * @return
	 */
	public int getElementCount() {
		return nodeList.size();
	}

	/**
	 * 返回该结点名
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	public void addAttribute(BinxmlAttr attr) {
		this.flag = (byte) (flag | EMP_HAVE_ATTR);
		// attrList.addElement(attr);
		attrList.put(attr.name, attr);
	}

	/**
	 * 获取属性节点的int值，如果值不是int，返回-1
	 * 
	 * @return
	 */
	public int getIntAttribute(String attname) {
		BinxmlAttr attr = attrList.get(attname);
		if (attr != null) {
			if ((attr.flag & EMP_VALUE_UINT_FOUR) == EMP_VALUE_UINT_FOUR) {
				int val = value[0] & 0xFF;
				val |= ((value[1] << 8) & 0xFF00);
				val |= ((value[2] << 16) & 0xFF0000);
				val |= ((value[3] << 24) & 0xFF000000);
				return val;
			} else if ((attr.flag & EMP_VALUE_UINT_ONE) == EMP_VALUE_UINT_ONE) {
				int val = value[0] & 0xFF;
				val |= ((value[1] << 8) & 0xFF00);
				return val;
			} else if (attr.flag == EMP_VALUE_UINT_ONE) {
				return attr.value[0];
			} else {
				return -1;
			}
		}
		return -1;
	}

	/**
	 * 节点还是通过获取bixml节点后 获取具体数据。 该属性整型 类型，返�?1
	 * 
	 * @param path
	 * @return
	 */
	// public int getIntAttributeByPath(String path,String attname)
	// {
	// return -1;
	// }

	/**
	 * 获取属性节点的二进制数据。
	 * 
	 * @return
	 */
	public byte[] getAttribute(String attname) {
		BinxmlAttr attr = attrList.get(attname);
		if (attr != null) {
			return attr.value;
		} else {
			return "".getBytes();
		}
	}

	/**
	 * 获取该属性的值
	 * 
	 * @param path
	 * @return
	 */
	public String getStringAttribute(String attname) {
		BinxmlAttr attr = attrList.get(attname);
		if (attr != null && attr.value != null) {
			return new String(attr.value);
		} else {
			return "";
		}
	}

	/**
	 * 以字符串型号返回指点path路径的element，属性attname�? 该属性�?非String 类型，返回null
	 * 
	 * @param path
	 * @return
	 */
	// public String getStringAttributeByPath(String path,String attname)
	// {
	// return null;
	// }

	/**
	 * 返回该节点的int值。
	 * 
	 * @return 如果该结点值非int，返-1
	 */
	public int getInt() {
		if ((flag & EMP_VALUE_UINT_FOUR) == EMP_VALUE_UINT_FOUR) {
			int val = value[0] & 0xFF;
			val |= ((value[1] << 8) & 0xFF00);
			val |= ((value[2] << 16) & 0xFF0000);
			val |= ((value[3] << 24) & 0xFF000000);
			// int val = value[0] | (value[1] >> 8) | value[2] >> 16| value[3]
			// >> 24;
			return val;
		} else if ((flag & EMP_VALUE_UINT_TWO) == EMP_VALUE_UINT_TWO) {
			int val = value[0] & 0xFF;
			val |= ((value[1] << 8) & 0xFF00);
			return val;
		} else if ((flag & EMP_VALUE_UINT_ONE) == EMP_VALUE_UINT_ONE) {
			return value[0];
		} else {
			return -1;
		}
	}

	/**
	 * 返回该节点的int值。
	 * 
	 * @param path
	 *            节点路径
	 * @param isMust
	 *            该节点是否必须
	 * @return 如果该结点值非int，返-1
	 * @throws BixmlException
	 */
	public int getIntByPath(String path, int defaultValue) {
		BinxmlElement emp = getNodeByPath(path);
		if (emp != null && emp.value != null) {
			return emp.getInt();
		} else {
			return defaultValue;
		}
	}

	/**
	 * 返回该节点的String值。
	 * 
	 * @return 如果该结点值非String，返回null
	 */
	public String getText() {
		if ((flag & EMP_VALUE_STR) == EMP_VALUE_STR) {
			if (value != null)
				try {
					return new String(value, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					return new String(value);
				}
			else
				return "";
		} else {
			return "";
		}
	}

	/**
	 * 返回该节点的String值。
	 * 
	 * @param path
	 *            节点路径
	 * @param isMust
	 *            该节点是否必须
	 * @return
	 * @throws BixmlException
	 */
	public String getTextByPath(String path, String defaultValue){
		BinxmlElement emp = getNodeByPath(path);

		if (emp != null && emp.value != null) {
			if ((emp.flag & EMP_VALUE_STR) == EMP_VALUE_STR) {
				try {
					return new String(emp.value, "utf-8");
				} catch (Exception e) {
					return "";
				}
			}
		} else {
			return defaultValue;
		}
		return "";
	}

	/**
	 * 
	 * getBool:返回布尔值. <br/>
	 * 
	 * @author Roy.Wong
	 * @return
	 * @since JDK 1.6
	 */
	public boolean getBool() {
		return getInt() > 0 ? true : false;
	}

	/**
	 * 
	 * getBoolByPath:指定路径节点的布尔值数据. <br/>
	 * 
	 * @author Roy.Wong
	 * @param path
	 * @param defValue
	 * @return
	 * @since JDK 1.6
	 */
	public boolean getBoolByPath(String path, boolean defValue) {
		int value = getIntByPath(path, defValue ? 1 : 0);
		return (value == 1) ? true : false;
	}

	/**
	 * 返回该节点的bin值。
	 * 
	 * @return
	 */
	public byte[] getBin() {
		return this.value;
	}

	/**
	 * 返回该节点的bin值。
	 * 
	 * @param path
	 * @param isMust
	 * @return
	 * @throws BixmlException
	 */
	public byte[] getBinByPath(String path, byte[] defaultValue) {
		BinxmlElement emp = getNodeByPath(path);
		if (emp != null) {
			return emp.value;
		} else {
			return defaultValue;
		}
	}

	/**
	 * 
	 * getFloat:该节点的浮点型值. <br/>
	 * 
	 * @author Roy.Wong
	 * @return
	 * @since JDK 1.6
	 */
	public float getFloat(){
		String txt = getText();
		
		if(txt != null && txt.length() > 0){
			return Float.parseFloat(txt);
		}else{
			return 0.0f;
		}
	}
	
	/**
	 * 
	 * getFloatByPath:指定子结点的浮点型值. <br/>
	 * 
	 *
	 * @author Roy.Wong
	 * @param path
	 * @param defaultValue
	 * @return
	 * @since JDK 1.6
	 */
	public float getFloatByPath(String path,float defaultValue){
		BinxmlElement emp = getNodeByPath(path);
		if (emp != null) {
			return emp.getFloat();
		} else {
			return defaultValue;
		}
	}
	
	/**
	 * 
	 * getLong: 该节点的长整型值. <br/>
	 *
	 * @author Roy.Wong
	 * @return
	 * @since JDK 1.6
	 */
	public long getLong(){
		String txt = getText();
		if(txt != null && txt.length() > 0) {
			return Long.parseLong(txt);
		}else{
			return 0l;
		}
	}
	
	/**
	 * 
	 * getLongByPath: 指定子节点的长整型值. <br/>
	 * @author Roy.Wong
	 * @param path
	 * @param defaultValue
	 * @return
	 * @since JDK 1.6
	 */
	public long getLongByPath(String path,long defaultValue){
		BinxmlElement emp = getNodeByPath(path);
		if (emp != null) {
			return emp.getLong();
		} else {
			return defaultValue;
		}
	}
	
	/**
	 *
	 * getTimestamp:该节点的时间戳类型数据 <br/>
	 *
	 * @author Roy.Wong
	 * @return
	 * @since JDK 1.6
	 */
	public Timestamp getTimestamp(){
		long t = getLong();
		return new Timestamp(t);
	}
	
	/**
	 * 
	 * getTimestampByPath:指定节点下的时间戳数据. <br/>
	 * 
	 * @author Roy.Wong
	 * @param path
	 * @param defaultValue
	 * @return
	 * @since JDK 1.6
	 */
	public Timestamp getTimestampByPath(String path,Timestamp defaultValue){
		BinxmlElement emp = getNodeByPath(path);
		if (emp != null) {
			return emp.getTimestamp();
		} else {
			return defaultValue;
		}
	}
	
	
	
	
	/**
	 * 把节点转换成xml格式 格式：^?: 操作符含义说明 0 char数据 -128~127 1 uchar数据 0~255 2 ushort数据
	 * 0~65535 3 short数据 -32768~32767 4 uint数据 0~4294967295 5 int数据
	 * -2147483648~2147483647 8 ulong数据 0~18446744073709551617 9 long数据
	 * -9223372036854775808~9223372036854775807 b 经过标准base64后的数据 h 经过Hex转换后的数据
	 * 
	 * @throws
	 */
	public String toXml() {
		StringBuffer strb = new StringBuffer();
		for (int i = 0; i < nodeList.size(); i++) {
			BinxmlElement node = (BinxmlElement) nodeList.elementAt(i);
			if (node != null) {
				strb.append("<");
				strb.append(node.name);
				strb.append(getAttrStr(node));
				strb.append(">");
				if ((node.flag & BinxmlElement.EMP_HAVE_CHILD) == BinxmlElement.EMP_HAVE_CHILD) {
					strb.append("\n");
				}
				strb.append(getNodeStr(node));
				strb.append(node.toXml());
				strb.append("</");
				strb.append(node.name);
				strb.append(">\n");
			}
		}
		return strb.toString();
	}

	/**
	 * 获得节点的字符串
	 * 
	 * @param node
	 * @return
	 * @throws
	 */
	private String getNodeStr(BinxmlElement node) {
		StringBuffer strb = new StringBuffer();
		if (node.value != null) {
			if ((node.flag & EMP_VALUE_INT) == EMP_VALUE_INT) {
				strb.append("^4:");
				strb.append(node.getInt());
			} else if ((node.flag & BinxmlElement.EMP_VALUE_STR) == BinxmlElement.EMP_VALUE_STR) {
				try {
					strb.append(new String(node.value, "utf-8"));
				} catch (Exception e) {
					strb.append("");
				}
			} else if ((node.flag & BinxmlElement.EMP_VALUE_BINARY) == BinxmlElement.EMP_VALUE_BINARY) {
				strb.append("binary data");
			}
		}
		return strb.toString();
	}

	/**
	 * 获得节点属性的字符串
	 * 
	 * @param node
	 * @return
	 */
	private String getAttrStr(BinxmlElement node) {
		StringBuffer strb = new StringBuffer();
		Iterator<Entry<String, BinxmlAttr>> it = node.attrList.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, BinxmlAttr> entry = it.next();
			BinxmlAttr attr = entry.getValue();
			strb.append(" ");
			strb.append(attr.name);
			strb.append("=\"");
			strb.append(new String(attr.value));
			strb.append("\"");
		}
		return strb.toString();
	}

	
	public String valueToString(){
		if ((flag & BinxmlElement.EMP_VALUE_STR) == BinxmlElement.EMP_VALUE_STR) {
			return getText();
		} else if ((flag & BinxmlElement.EMP_VALUE_INT) == BinxmlElement.EMP_VALUE_INT) {
			return String.valueOf(getInt());
		} else if ((flag & BinxmlElement.EMP_VALUE_BINARY) == BinxmlElement.EMP_VALUE_BINARY) {
			return  Hex.encodeToString(getBin());
		}else{
			return null;
		}
	}
	
	/**
	 * 转成byte数组
	 * 
	 * @return
	 */
	public byte[] toBytes() {
		byte[] bytes = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dws = new DataOutputStream(baos);
		try {
			for (int i = 0; i < nodeList.size(); i++) {
				BinxmlElement element = (BinxmlElement) nodeList.elementAt(i);
				dws.write(element.name.getBytes());
				dws.write(0);
				dws.write(element.flag);

				if ((element.flag & EMP_VALUE_BINARY) == EMP_VALUE_BINARY) { // 字符和二进制
					if (element.value != null) {
						dws.writeShort((short) element.value.length);
						dws.write(element.value);
					} else {
						dws.writeShort(0);
					}
				} else if ((element.flag & EMP_VALUE_INT) == EMP_VALUE_INT) {// int

					dws.writeShort((short) element.value.length);
					dws.write(element.value);
				} else {
					dws.writeShort(0);

				}
				if ((element.flag & EMP_HAVE_ATTR) == EMP_HAVE_ATTR) { // 节点带有属性
					int count = element.attrList.size();
					dws.writeShort((short) count);

					Iterator<Entry<String, BinxmlAttr>> it = element.attrList
							.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, BinxmlAttr> entry = it.next();
						BinxmlAttr attr = entry.getValue();

						dws.write(attr.name.getBytes());
						dws.write(0);
						dws.write(attr.flag);
						if ((attr.flag & EMP_VALUE_INT) == EMP_VALUE_INT) {// int
							short num = (short) (attr.flag & 0x0f);
							dws.writeShort(num);
							dws.write(attr.value);
						} else if ((attr.flag & EMP_VALUE_BINARY) == EMP_VALUE_BINARY) { // bin
							dws.writeShort(attr.value.length);
							dws.write(attr.value);
						}

					}
				}
				if ((element.flag & EMP_HAVE_CHILD) == EMP_HAVE_CHILD) { // 有子节点

					dws.writeShort(element.nodeList.size());

					dws.write(element.toBytes());
				}
			}
			bytes = baos.toByteArray();

			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				baos.close();
				dws.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		}
	}

}
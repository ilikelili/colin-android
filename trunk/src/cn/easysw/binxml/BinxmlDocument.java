package cn.easysw.binxml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;

import cn.easysw.binxml.BinxmlElement;
import cn.easysw.binxml.utils.BinxmlBuilder;


/**
 * bixml根节点
 * 
 * @author thomescai@163.com
 * @version 2011-7-28
 */
public class BinxmlDocument {
	public static int EMP_DEFAULT_ENCODING = 1; /* UTF-8 */
	public static int EMP_DEFAULT_LIMIT_ONE = 1;
	public static int EMP_DEFAULT_LIMIT_TWO = 2;

	public byte[] empFixedHead;

	public byte version;
	public byte limit;
	public byte endian;
	public byte[] encoding;

	
	public static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>";

	
	public BinxmlElement rootElement = new BinxmlElement("root");
	
	
	/**
	 * bixml的固定头
	 */
	public static byte[] head = new byte[] { (byte) 0x9b, (byte) 0xba, (byte) 0xee,
		(byte) 0x50, (byte) 0xb4, (byte) 0xe9, (byte) 0x83,
		(byte) 0x7f, (byte) 0xa8, (byte) 0xb6, (byte) 0x6d,
		(byte) 0xaf, (byte) 0x10, (byte) 01, (byte) 00, (byte) 0x21 };

	/**
	 * 将字节数据以emp格式解析成Element树型结构.
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static BinxmlDocument parse(byte[] data) {
		DataInputStream drs;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			drs = new DataInputStream(bais);
			return parse(drs);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将DataReadStream中的数据,以emp格式解析成Element树型结构.
	 * 
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	public static BinxmlDocument parse(DataInputStream drs) throws Exception {		BinxmlDocument emp = null;
		if (drs == null)
			 throw new Exception("input stream is null.");
		
		emp = new BinxmlDocument();

		byte[] bytes = new byte[15];
		drs.read(bytes, 0, bytes.length);
		if (!checkBixml(bytes)) {
			throw new Exception("bad emp head .");
		}

		byte b = drs.readByte();
		emp.limit = (byte) (b >> 4);
		emp.endian = (byte) (b & 0x0F);
		emp.rootElement = BinxmlBuilder.buildArrayToRoot(drs,emp.limit,null); 
		return emp;
	}

	/**
	 * 验证bixml
	 * 测试阶段，所有数据都通过。
	 * @param bytes
	 * @return
	 */
	private static boolean checkBixml(byte[] bytes) {
		if (bytes[0] != 0) { 
			return true;
		}
		return true;
	}

	/**
	 * 返回解析出来的element树型结构的rootElement
	 * 
	 * @return
	 */
	public BinxmlElement getRootElement() {
		return this.rootElement;
	}

	/**
	 * 转换成xml模式
	 * @return
	 */
	public String toXML() {
		StringBuffer strb = new StringBuffer();
		strb.append("<?emp v=\"1.0\" e=\"utf-8\" x=\"ln\" ?>\n");
		strb.append(rootElement.toXml());
		return strb.toString();
	}

	/*转成标准XML模式*/
	
	public String toXMLStand(){
		StringBuffer strb = new StringBuffer();
		strb.append(XML_HEAD);
		strb.append(rootElement.toXml());
		return strb.toString();
	}
	
	/**
	 * 打印xml
	 */
	public void printXml() {
		System.out.println(toXML());
	}

	/**
	 * 2个字节表示长度
	 * 
	 * @return
	 */
	public byte[] toBytes() {
		byte[] bytes = null;
		try {
			ByteArrayOutputStream dws = new ByteArrayOutputStream();
			dws.write(head);
			dws.write(rootElement.toBytes());
			bytes = dws.toByteArray();
			dws.close();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			bytes = null;
		}
	}

	/**
	 * 根据路径增加节点
	 * 
	 * @param element
	 * @param path
	 */
	public void addElementByPath(BinxmlElement element, String path) {
		if (element == null || path == null)
			return;
		BinxmlElement elementAdded = rootElement.getNodeByPath(path);
		if (elementAdded != null) {
			elementAdded.nodeList.addElement(element);
		}
	}

	public void setRootElement( BinxmlElement element){
		this.rootElement = element;
	}
	/**
	 * 增加节点到根节点
	 * 
	 * @param element
	 */
	public void addElementToRoot(BinxmlElement element) {
		if (element == null)
			return;
		rootElement.nodeList.addElement(element);
	}
}

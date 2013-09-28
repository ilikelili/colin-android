package com.windo.common.util.xml;





public class DomXmlWriter {

	
	
//	public static String getSerialzerXml(XmlSerializer obj){
//		StringBuffer sbf = new StringBuffer();
//		obj.toXml(sbf);
//		return sbf.toString();
//	}
//	
//	
//	public static void toWriteXml(StringBuffer sbf,Hashtable<String,Object> args){
//		Enumeration<String> enu = args.keys();
//		String name = null;
//		Object value = null;
//		while (enu.hasMoreElements()) {
//			name  = enu.nextElement();
//			value = args.get(name);
//			if(value instanceof XmlSerializer){
//				((XmlSerializer)value).toXml(sbf);
//			}else{
//				appendChildNodeStart(sbf,name);
//				appendValueNode(sbf,value);
//				appendChildNodeEnd(sbf,name);	
//			}
//		}
//	}
//	
//	
//	private static void appendValueNode(StringBuffer sbf,Object value){
//		if(value instanceof Array){
//			Object[] array = (Object[])value;
//			for (int i = 0; i < array.length; i++) {
//				appendValueNode(sbf,array[i]);
//			}
//		}else if(value instanceof XmlSerializer){
//			((XmlSerializer)value).toXml(sbf);
//		}else{
//			String str = value.toString();
//            value = str.replace("&", "&amp;");
//			sbf.append(value.toString());
//		}
//	}
//	
//	
//	
//
//	
//	
//	
//	public static void appendChildNodeStart(StringBuffer sb,String name){
//		if(MocamProtocol.XML_DOMAIN != null){
//			name = MocamProtocol.XML_DOMAIN + name;
//		}
//		sb.append("<");
//		sb.append(name);
//		sb.append(">");
//	}
//	
//	
//	public static void appendChildNodeEnd(StringBuffer sb,String name){
//		if(MocamProtocol.XML_DOMAIN != null){
//			name = MocamProtocol.XML_DOMAIN  + name;
//		}
//		sb.append("</");
//		sb.append(name);
//		sb.append(">");
//	}
//	
//	
//	
//	
//	
//	public static void appendChildNode(StringBuffer sb,String name,String value){
//		
//		if(name != null && value != null){
//			if(MocamProtocol.XML_DOMAIN != null){
//				name = MocamProtocol.XML_DOMAIN  + name;
//			}
//			value = value.replaceAll("&", "&amp;");
//			sb.append("<");
//			sb.append(name);
//			sb.append(">");
//			sb.append(value);
//			sb.append("</");
//			sb.append(name);
//			sb.append(">");	
//		}
//	}
	
}

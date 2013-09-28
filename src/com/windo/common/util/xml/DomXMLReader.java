package com.windo.common.util.xml;

import java.util.List;
import java.util.Vector;

import org.xmlpull.v1.XmlSerializer;




public class DomXMLReader {

	
	
//	public static List<XmlSerializer> creatDocumentList(String xmlstring,String tagname,
//															Class obj) {
//		
//		Node root = TreeBuilder.parseTree(xmlstring);
//		return creatDocumentList(root,tagname,obj);
//	}
//	
//	
//	
//	public static XmlSerializer createData(String xml,Class obj){
//		Node root = TreeBuilder.parseTree(xml);
//		return createData(root,obj);
//	}
	
	
//	public static XmlSerializer createData(Node node,Class obj){
//		XmlSerializer mobj;
//		try {
//			mobj = (XmlSerializer)obj.newInstance();
//
//
//			mobj.initialize(node);
//			return mobj;
//		} catch (InstantiationException e) {
//			
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	
//	
//	public static List<XmlSerializer> creatDocumentList(Node node,String tagname
//			, Class obj){
//		
//		List<XmlSerializer> list = new Vector<XmlSerializer>();
//		Vector items = node.getChildrenByName(tagname);
//		int count = items.size();
//		try {
//			for (int i = 0; i < count; i++) {
//				Node n = (Node)items.elementAt(i);
//				XmlSerializer mobj = (XmlSerializer)obj.newInstance();
//				mobj.initialize(n);
//				list.add(mobj);
//			}
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//	
//		return list;
//	}
	
	
	public static String[] getNodeStringListByName(Node node,String parentname,String childname){
			String[] imageUrl = null;
			Node nodeparent =node.getFirstChildrenByName(parentname);
			if(nodeparent != null){
				Vector nodelist = nodeparent.getChildrenByName(childname);
				int count =nodelist.size();
                if(count>0){
                    imageUrl = new String[count];
                    for(int i=0;i<count;i++){
                        Node childnode = (Node) nodelist.elementAt(i);
                        imageUrl[i] = new String();
                        imageUrl[i] = childnode.getText();
                    }
                }
			}
		return imageUrl;
	}
	
	
	
	public static String getNodeStringByName(Node args,String nodename){
		
		Node node = args.getFirstChildrenByName(nodename);
		if(node != null){
			return node.getText().trim();
		}
		return null;
	}
	
	
	
	public static int getNodeIntByName(Node args,String nodename){
		
		Node node = args.getFirstChildrenByName(nodename);
		if(node != null && node.getText().length() > 0 ){
			try {
				return Integer.parseInt(node.getText().trim());	
			} catch (NumberFormatException e) {
				
				return Integer.parseInt(node.getText(), 16);
			}
		}
		return -1;
	}
	
	
	public static long getNodeLongByName(Node args,String nodename){
		
		Node node = args.getFirstChildrenByName(nodename);
		if(node != null && node.getText().length() > 0 ){
			try {
				return Long.parseLong(node.getText().trim());
			} catch (NumberFormatException e) {
				
				return Long.parseLong(node.getText(), 16);
			}
		}
		return -1;
	}
	
	
	public static byte getNodeByteByName(Node args,String nodename){
		
		String value = getNodeStringByName(args,nodename);
		if(value != null && value.length() > 0){
			try {
				return Byte.parseByte(value);
			} catch (NumberFormatException e) {
				
				return Byte.parseByte(value,16);
			}
		}
		return -1;
	}
	

}
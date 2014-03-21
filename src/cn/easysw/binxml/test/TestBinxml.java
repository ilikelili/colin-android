package cn.easysw.binxml.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import cn.easysw.binxml.BinxmlDocument;
import cn.easysw.binxml.BinxmlElement;
import cn.easysw.binxml.BinxmlException;
import cn.easysw.binxml.protocol.BinProtocal;

public class TestBinxml {
	public static void main(String[] args){
		String t = "ttt/asdfasdf/asdf\\asdf\\/asdf";
		String[] s = t.split("[\\\\/]");
		for (int i = 0; i < s.length; i++) {
			System.out.println(s[i]);
		}
	}
//		public static void main(String[] args){
//			  BinProtocal bin = new BinProtocal();
//			  bin.setServiceid(10000);
//			  BinxmlElement xml = new BinxmlElement("request");
//			  xml.addElement(new BinxmlElement("a","text1"));
//			  xml.addElement(new BinxmlElement("b","text2"));
//			  
//			  bin.setBody(xml);
//			  byte[] data;
//			try {
//				data = bin.toBytes(null);
//				  bin.setHead(data);
//				  bin.setBody(data, 32);
//				  xml = bin.getBody();
//				  BinxmlElement a = xml.getElementByName("a");
//				  BinxmlElement b = xml.getElementByName("b");
//				  System.out.println(a.getText());
//				  System.out.println(b.getText());
//			} catch (BinxmlException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			  
//			  
//		}
	
		
	
}

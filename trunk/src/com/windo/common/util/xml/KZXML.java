package com.windo.common.util.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import android.util.Log;

public class KZXML {
	
	
	Document m_doc = null;
	
	private KZXML(Document document)
	{
		m_doc = document;
	}
	
	
	public static KZXML parse(String node)
	{
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(node);
			return  new KZXML(doc);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.d("KZXML Parse:", e.toString());
			return null;
		}
	
	}
	
	public static KZXML parse(byte[] b){
		try {
			if(b == null || b.length == 0 ){
				Log.d("[ERROR]KZXML", "parse data is null");
				return null;
			}
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(b);
			InputSource is = new InputSource( inputStream );
			is.setEncoding("UTF-8");
			Document doc = builder.parse(is);
			return  new KZXML(doc);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.d("[ERROR]KZXML Parse(Byte[]):", e.toString());
			return null;
		}
	}
	
	public static KZXML parse(ByteArrayOutputStream dws){
		if(dws != null){
			try {
				return KZXML.parse(dws.toByteArray());	
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.d("[ERROR]KZXML parse(DataWriteStream)", e.toString());
				return null;
			}
		}else{
			return null;
		}
	}
	
	public XMLElement getRootElement()
	{
		if(m_doc != null){
			return  new XMLElement(m_doc.getDocumentElement());	
		}else{
			return null;
		}
	}
	
	public void dispose()
	{
		m_doc = null;	
	}
	
	
}

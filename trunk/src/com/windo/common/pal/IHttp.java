package com.windo.common.pal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

public interface IHttp
{
	
	public final static String MULTIPART_FORM_DATA = "multipart/form-data";
	public final static String POST_FORM_DATA = "application/x-www-form-urlencoded"; 
	public static final String TRANSFER_ENCODING = "Transfer-Encoding";
	public static final String TRANSFER_ENCODING_CHUNKED = "chunked";
	
	
	public void open(String url);
	
	
	public String getURL();
	
	
	public void close()  throws IOException;
	
	public void setRequestProperty(String key, String value);
	
	
	public void setRequestMethod(String method);
	
	






	
	
	
	public void postMultiPart(String fileUrl); 
	public void postMultiPart(String fileUrl, Hashtable param); 
	
	public void postByteArray(byte[] byteArray);
	
	public int execute()  throws IOException;
	
	
	public int getResponseCode();
	
	
	public String getResponseMessage() throws IOException;
	
	
	public String getHeaderField(String name)  throws IOException;
	
	
	public String getHeaderField(int n)  throws IOException; 
	
	
	public String getHeaderFieldKey(int n)  throws IOException;
	
	
	public InputStream openInputStream()  throws IOException;
	
	
	public DataInputStream openDataInputStream()  throws IOException;
	
	
	public OutputStream openOuputStream()  throws IOException;
	
	
	public DataOutputStream openDataOutputStream()  throws IOException;
	
	
	public long getContentLength()  throws IOException;
	
	
	public void setTimeout(int timeout) ;
    
}

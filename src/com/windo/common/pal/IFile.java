package com.windo.common.pal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IFile
{
	
	
	public boolean mkdir();
	
	
	public boolean mkdirs() ;

	
	
	public boolean renameTo(String newName); 

	
	public String getName();
	
	
	public boolean exists(); 

	
	public void close() throws IOException;
	
	
	public long getSize() throws IOException;
	
	
	public OutputStream openOutputStream() throws IOException;
	
	
	public InputStream openInputStream() throws IOException;

	
	
}

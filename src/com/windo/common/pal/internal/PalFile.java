package com.windo.common.pal.internal;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.windo.common.pal.IFile;

public class PalFile implements IFile {

	private java.io.File mFile;
	private boolean m_readOnly;
	
	public PalFile(String path) {
		m_readOnly = false;
		mFile = new java.io.File(path);
	}
	
	public PalFile(String path, boolean readOnly) {
		m_readOnly = readOnly;
		mFile = new java.io.File(path);
	}
	
	
	public void close() throws IOException {
		
	}

	
	public boolean exists() {
		return mFile.exists();
	}

	
	public String getName() {
		return mFile.getName();
	}

	
	public long getSize() throws IOException {
		if(mFile.isFile())
			return mFile.length();
		
		return 0;
	}

	
	public boolean mkdir() {
		
		return false;
	}

	
	public boolean mkdirs() {
		
		return false;
	}

	public InputStream openInputStream() throws IOException
	{
		return new FileInputStream(mFile);
	}

	public OutputStream openOutputStream() throws IOException
	{
		if (m_readOnly) {
			throw new IOException();
		}
		
		return new FileOutputStream(mFile);
	}

	
	public boolean renameTo(String newName) {
		java.io.File newFile = new java.io.File(newName);
		return mFile.renameTo(newFile);
	}

}

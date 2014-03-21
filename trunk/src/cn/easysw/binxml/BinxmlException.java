package cn.easysw.binxml;

/**
 * bixml的异常
 * @author thomescai@163.com
 * @version 2011-7-28
 */
public class BinxmlException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5552520504067828774L;

	public BinxmlException(String path) {
		super(path);
	}
}

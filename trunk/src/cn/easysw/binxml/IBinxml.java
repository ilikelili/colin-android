package cn.easysw.binxml;

public  interface IBinxml {
	public BinxmlElement toBinxml(String name);
	public void parseBinxml(BinxmlElement ele);
}

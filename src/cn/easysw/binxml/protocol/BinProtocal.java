package cn.easysw.binxml.protocol;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import cn.easysw.binxml.BinxmlElement;
import cn.easysw.binxml.BinxmlException;
import cn.easysw.binxml.IBinxml;
import cn.easysw.binxml.utils.BinxmlBuilder;

/**
 * 
 * 通讯协议包头32位+数据
 * 
 * @author sanmulong
 * 
 */
public class BinProtocal {

	private final static byte[] IDENTITY = { (byte) 0xf9, 0x17, (byte) 0xE6,
			(byte) 0x99 };

	// 压缩标志
	private boolean compress;

	//是否多包，分块模式
	private boolean chunkMode;

	// 服务ID
	private int serviceid;

	//包的序列号
	private short packageNo;
	// 协议类型
	private byte protocoltype;
	// 版本
	private byte version = 1;
	
	// 加密方式
	private byte encryption = 0;

	private  int dataLen = 0;

	private byte[] sessionId = new byte[16];

	private byte[] bodyData=null;
	
	private BinxmlElement body = new BinxmlElement("root");

	public int getServiceid() {
		return serviceid;
	}

	public void setServiceid(int serviceid) {
		this.serviceid = serviceid;
	}
	

	public short getPackageNo() {
		return packageNo;
	}

	public void setPackageNo(short packageNo) {
		this.packageNo = packageNo;
	}

	public BinProtocal() {
		
		//  System.arraycopy("0000000000000000".getBytes(), 0, sessionId, 0, 16);
	}

	public byte getEncryption() {
		return encryption;
	}

	public void setEncryption(byte encryption) {
		this.encryption = encryption;
	}

	public byte[] getSesssionId() {
		return sessionId;
	}

	public void setSesssionId(byte[] sessionId) throws BinxmlException {
		if (sessionId == null || sessionId.length != 16)
			throw new BinxmlException(" The session id length must be 16. ");
		this.sessionId = sessionId;
	}

	public void parse(byte[] data) throws BinxmlException {
		setHead(data);
		setBody(data, 32);
	}

	public void doParse() throws BinxmlException{
		setBody(bodyData,0);
	}
	
	
	public void setHead(byte[] data) throws BinxmlException {
		if (data.length < 32)
			new BinxmlException(
					"Array length is too short,must be greater than 32 ");
		if (data[0] != IDENTITY[0] || data[1] != IDENTITY[1]
				|| data[2] != IDENTITY[2] || data[3] != IDENTITY[3])
			throw new BinxmlException("Illegal binary xml format");
		version = data[4];
		compress = (data[5] >> 7 & 0x1) == 1 ? true : false;
		chunkMode = (data[5] >> 6 & 0x1) == 1 ? true : false;
		protocoltype =(byte) ( data[5] >> 3 & 0x7 );
		encryption =(byte) (data[5] & 0x7 );
	    packageNo =(short)( (data[6] << 8 ) | data[7] );
		serviceid = data[8] << 24 | (data[9]&0xff) << 16 | (data[10]&0xff) << 8 |(data[11]&0xff);

		dataLen =  (int)( data[12] << 24 |  (data[13]&0xff) << 16 | (data[14] &0xff)<< 8 | data[15]&0xff );
		System.arraycopy(data, 16, sessionId, 0, 16);
	}

	public void setBody(byte[] data, int offset) throws BinxmlException {
		if ( (dataLen+offset) == data.length) {
			ByteArrayInputStream bais = new ByteArrayInputStream(data, offset,
					dataLen);
			DataInputStream dis = new DataInputStream(bais);
			try {
				body = BinxmlBuilder.buildArrayToRoot(dis, (byte) 0, null);
			} catch (Exception e) {
					e.printStackTrace();
			    	throw new BinxmlException(e.getMessage());
			}
		} else{
			
			throw new BinxmlException(
					"Packet length and the size of the data is inconsistent. ");
		}
	}

	public byte[] toBytes(byte[] data) throws BinxmlException {
		byte[] bodydata = body.toBytes();
		dataLen = bodydata != null ? bodydata.length : 0;

		if (data == null) {
			data = new byte[dataLen + 32];
		} else {
			if (data.length < (dataLen + 32))
				throw new BinxmlException(
						"Array length is too short,must be greater than 32 ");
		}

		System.arraycopy(IDENTITY, 0, data, 0, 4);

		data[4] = version;

		if (compress)
			data[5] |= (1 << 7);
		if (chunkMode)
			data[5] |= (1 << 6);

		data[5] |= protocoltype<<3;
		data[5] |= encryption;
     
		data[6] =  (byte) ((packageNo >> 8)&0xff);
		data[7] =  (byte) (packageNo&0xff);
		data[8] = (byte) ((serviceid >> 24)&0xff);
		data[9] = (byte) ((serviceid >> 16) & 0xff);
		data[10] = (byte) ((serviceid >> 8) & 0xff);
		data[11] = (byte) (serviceid & 0xff);

		data[12] = (byte) ((dataLen >> 24) & 0xff);
		data[13] = (byte) ((dataLen >> 16) & 0xff);
		data[14] = (byte) ((dataLen >> 8) & 0xff);
		data[15] = (byte) (dataLen & 0xff);

		System.arraycopy(sessionId, 0, data, 16, 16);
		if ( dataLen > 0 )
		     System.arraycopy(bodydata, 0, data, 32, dataLen );
		return data;
	}

	
	public byte[] getHead(){
		
		byte[] head = new byte[32];
		System.arraycopy(IDENTITY, 0, head, 0, 4);
		head[4] = version;
		
		byte[] bodybytes = bodyData;
		if(bodybytes == null){
			bodybytes = body.toBytes();
			bodyData = bodybytes; 
		}
		int dataLen =  bodybytes != null ? bodybytes.length:0; 
		
		if (compress)
			head[5] |= (1 << 7);
		if (chunkMode)
			head[5] |= (1 << 6);

		head[5] |= protocoltype<<3;
		head[5] |= encryption;
     
		head[6] =  (byte) ((packageNo >> 8)&0xff);
		head[7] =  (byte) (packageNo&0xff);
		head[8] = (byte) ((serviceid >> 24)&0xff);
		head[9] = (byte) ((serviceid >> 16) & 0xff);
		head[10] = (byte) ((serviceid >> 8) & 0xff);
		head[11] = (byte) (serviceid & 0xff);

		head[12] = (byte) ((dataLen >> 24) & 0xff);
		head[13] = (byte) ((dataLen >> 16) & 0xff);
		head[14] = (byte) ((dataLen >> 8) & 0xff);
		head[15] = (byte) (dataLen & 0xff);

		
		return head;
		
	}
	
	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public boolean isChunkMode() {
		return chunkMode;
	}

	public void setChunkMode(boolean chunkMode) {
		this.chunkMode = chunkMode;
	}

	public byte getProtocoltype() {
		return protocoltype;
	}

	public void setProtocoltype(byte protocoltype) {
		this.protocoltype = protocoltype;
	}

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public int getDataLen() {
		return dataLen;
	}

	public BinxmlElement getBody() {
		if (body.getElementCount() == 1)
			return body.getElement(0);
		else
			return null;
	}

	public void setBody(IBinxml bean) {
		this.body.removeAllChild();
		body.addElement(bean.toBinxml(null));

	}

   public void setBody(BinxmlElement ele ){
		this.body.removeAllChild();
		body.addElement(ele);
   }
	public int getSize() {
		return dataLen + 32;
	}

	public byte[] getBodyData() {
		return bodyData;
	}

	public void setBodyData(byte[] bodyData) {
		this.bodyData = bodyData;
	}

}

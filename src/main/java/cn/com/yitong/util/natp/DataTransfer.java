/*
 * 创建日期 2004-5-18 11:37:07
 *
 */
package cn.com.yitong.util.natp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**新natp数据传输协议传输器，用于接收和发送nnatp数据传输的数据
 * @author 陈育生
 *
 * 北京赞同科技发展有限公司
 * 
 * 通讯前置
 */
public class DataTransfer {

    private OutputStream _outputStream;

    private InputStream _inputStream;

    private Socket _sock;

    private byte[] recvDatas = new byte[40960];

    private int dataLen = 0;

    private int _natpVersion = 0x01; // NATP协议版本号

    private String _tradeCode;//交易代码

    private String _businessType;

    private String _agentNo;

    private String _areaNo;

    private String _bankNo;

    private String _tellerNo;

    private int _protoclVersion;

    private String _fileServerName;

    private String _templateCode; // 模板代码，商业银行使用

    private String _reserve; // 保留，商业银行使用

    /**构造器
     * @param sock
     */
    public DataTransfer(Socket sock) {
        _sock = sock;
        try {
            setInputStream(_sock.getInputStream());
            setOutputStream(_sock.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**收取nnatp的数据，数据包收取时设置部分全局值，并返回收到数据包
     * @return 数据包
     * @throws java.io.IOException
     */
    public byte[] natpRecv() throws IOException {
        DataInputStream din = new DataInputStream(getInputStream());
        int totalLength = din.readInt();
        byte[] allDatas = new byte[totalLength];
        readFully(din, allDatas);
        if (Crc32_Agree.crc32_2(allDatas, 0, totalLength - 4) != getCrc(allDatas))
            throw new IOException("crc校验不符");
        byte protoclVersion = allDatas[0];
        setProtoclVersion(protoclVersion);
        // 传输数据流
        if (protoclVersion == 0x01) {
            byte[] datas = natpRecvData(allDatas, din);
            return handleDatas(datas);
        }
        // 传输文件	
        else if (protoclVersion == 0x02)
            return natpRecvFile(allDatas, din);
        else
            throw new IOException("无此协议版本号" + protoclVersion);

    }

    public void natpSendDatas(byte[] datas) throws IOException {
        int offset;
        if (getNatpVersion() == 0x10)
            offset = 71;
        else
            offset = 43;
        DataOutputStream dout = null;
        DataInputStream din = null;
        
        try {
        	dout = new DataOutputStream(getOutputStream());
            din = new DataInputStream(getInputStream());
            byte continueFlag = 0;
            short packNum = 1;
            int sendLength = datas.length + offset;
            int index = datas.length;
            if (sendLength > 4092) {
                sendLength = 4092;
                continueFlag = 1;
                index = sendLength - offset; // 实际要发送的数据的结束点
            }
            dout.writeInt(sendLength);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(bout);

            dataOut.write(0x01);
            dataOut.write(0x00);
            dataOut.write(continueFlag);
            dataOut.write(0x00);
            dataOut.writeShort(packNum);
            if (getNatpVersion() == 0x10) {
                // TODO 商业银行中间业务包
                dataOut.write(0x10);
                dataOut.write(StringTool.fill(getTradeCode(), ' ', 20, false).getBytes("GBK"));
                dataOut.write(StringTool.fill(getTemplateCode(), ' ', 20, false).getBytes("GBK"));
                dataOut.write(StringTool.fill(getReserve(), ' ', 20, false).getBytes("GBK"));
            }
            else {
                dataOut.write(0x01);
                dataOut.write(StringTool.fill(getTradeCode(), ' ', 5, false).getBytes());
                dataOut.write(StringTool.fill(getBusinessType(), ' ', 3, false).getBytes());
                dataOut.write(StringTool.fill(getAgentNo(), ' ', 9, false).getBytes());
                dataOut.write(StringTool.fill(getAreaNo(), ' ', 5, false).getBytes());
                dataOut.write(StringTool.fill(getBankNo(), ' ', 5, false).getBytes());
                dataOut.write(StringTool.fill(getTellerNo(), ' ', 5, false).getBytes());
            }
            dataOut.write(datas, 0, index);
            byte[] pureDatas = bout.toByteArray();
            dout.write(pureDatas);
            dout.writeInt(Crc32_Agree.crc32_2(pureDatas, 0, pureDatas.length));
            dout.flush();
            while (continueFlag == 1) {
                recvProtocolPack(din);
                if (datas.length <= index + 4096 - 14) {
                    continueFlag = 0;
                    sendLength = datas.length - index;
                }
                else {
                    continueFlag = 1;
                    sendLength = 4092 - 10;
                }
                bout.reset();
                dataOut.write(0x01);
                dataOut.write(0x00);
                dataOut.write(continueFlag);
                dataOut.write(0x00);
                dataOut.writeShort(++packNum);
                dataOut.write(datas, index, sendLength);
                pureDatas = bout.toByteArray();
                dout.writeInt(sendLength + 10);
                dout.write(pureDatas);
                dout.writeInt(Crc32_Agree.crc32_2(pureDatas, 0, pureDatas.length));
                dout.flush();
                index += sendLength;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				dout.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			try {
				din.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
        

    }

    /**
     * @param din
     */
    private void recvProtocolPack(DataInputStream din) throws IOException {
        int len = din.readInt();
        byte[] protocol = new byte[len];
        din.read(protocol);
    }

    /**
     * @param datas
     * @return
     */
    private byte[] handleDatas(byte[] datas) throws IOException {
        int natpVersion = datas[0];
        if (natpVersion == 0x01 || natpVersion == 0x02) {
            //交易代码
            byte[] tradeCode = { datas[1], datas[2], datas[3], datas[4], datas[5] };
            //业务类型
            byte[] businessType = { datas[6], datas[7], datas[8] };
            //代理业务编号
            byte[] agentNo = { datas[9], datas[10], datas[11], datas[12], datas[13], datas[14], datas[15], datas[16],
                    datas[17] };
            //地区号
            byte[] areaNo = { datas[18], datas[19], datas[20], datas[21], datas[22] };
            //网点号
            byte[] bankNo = { datas[23], datas[24], datas[25], datas[26], datas[27] };
            //柜员号
            byte[] tellerNo = { datas[28], datas[29], datas[30], datas[31], datas[32] };
            setTradeCode(new String(tradeCode).trim());
            setAgentNo(new String(agentNo).trim());
            setBusinessType(new String(businessType).trim());
            setAreaNo(new String(areaNo).trim());
            setBankNo(new String(bankNo).trim());
            setTellerNo(new String(tellerNo).trim());
            byte[] realDatas = new byte[datas.length - 33];
            System.arraycopy(datas, 33, realDatas, 0, realDatas.length);
            return realDatas;
        }
        else if (natpVersion == 0x03) {
            byte[] realDatas = new byte[datas.length - 1];
            System.arraycopy(datas, 1, realDatas, 0, realDatas.length);
            return realDatas;
        }
        else if (natpVersion == 0x10) {
            // 商业银行中间业务报文定义
            // 交易代码
            byte[] tradeCode = new byte[20];
            System.arraycopy(datas, 1, tradeCode, 0, 20);
            // 模板代码
            byte[] templateCode = new byte[20];
            System.arraycopy(datas, 21, templateCode, 0, 20);
            // 预留
            byte[] reserve = new byte[20];
            System.arraycopy(datas, 41, reserve, 0, 20);

            setTradeCode(new String(tradeCode).trim());
            setTemplateCode(new String(templateCode).trim());
            setReserve(new String(reserve).trim());

            byte[] realDatas = new byte[datas.length - 61];
            System.arraycopy(datas, 61, realDatas, 0, realDatas.length);
            return realDatas;
        }
        else
            throw new IOException("无此NATP版本类型 " + natpVersion);
    }

    private void readFully(InputStream din, byte[] rec) throws IOException {
        int readed = din.read(rec);
        while (readed < rec.length) {
            try {
                int next = din.read(rec, readed, rec.length - readed);
                readed += next;
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * @param allDatas
     * @return
     */
    private byte[] natpRecvFile(byte[] allDatas, DataInputStream din) throws UnknownHostException, IOException {
        // 获取主机地址
        byte[] hostName = new byte[15];
        System.arraycopy(allDatas, 7, hostName, 0, hostName.length);
        String host = new String(hostName).trim();
        setFileServerName(host);
        return allDatas;
        /*
        String hostIp = queryHostIp(host);
        int port = queryHostPort(host);
        Socket hostSock = new Socket(hostIp, port);
        
        // 进行代理服务
        DataInputStream hostInput =
        	new DataInputStream(hostSock.getInputStream());
        DataOutputStream hostOuput =
        	new DataOutputStream(hostSock.getOutputStream());
        DataOutputStream clientOutput = new DataOutputStream(getOutputStream());
        hostOuput.writeInt(allDatas.length);
        hostOuput.write(allDatas);
        hostOuput.flush();
        while (true) {
        	try {
        		int len = hostInput.readInt();
        		byte[] recv = new byte[len];
        		readFully(hostInput, recv);
        		clientOutput.writeInt(len);
        		clientOutput.write(recv);
        		clientOutput.flush();
        		len = din.readInt();
        		recv = new byte[len];
        		readFully(din, recv);
        		hostOuput.writeInt(len);
        		hostOuput.write(recv);
        		hostOuput.flush();
        	} catch(IOException e){
        		if( e.toString().equals("java.io.EOFException") )
        			return null;
        		else
        			throw e;
        	}
        }
        */
    }

    public void fileTransfer(byte[] firstDatas, InputStream _hostInput, OutputStream _hostOutput) throws IOException {

        // 进行代理服务
        DataInputStream hostInput = new DataInputStream(_hostInput);
        DataOutputStream hostOuput = new DataOutputStream(_hostOutput);
        DataOutputStream clientOutput = new DataOutputStream(getOutputStream());
        DataInputStream clientInput = new DataInputStream(getInputStream());
        hostOuput.writeInt(firstDatas.length);
        hostOuput.write(firstDatas);
        hostOuput.flush();
        while (true) {
            try {
                int len = hostInput.readInt();
                byte[] recv = new byte[len];
                readFully(hostInput, recv);
                clientOutput.writeInt(len);
                clientOutput.write(recv);
                clientOutput.flush();
                len = clientInput.readInt();
                recv = new byte[len];
                readFully(clientInput, recv);
                hostOuput.writeInt(len);
                hostOuput.write(recv);
                hostOuput.flush();
            }
            catch (IOException e) {
                if (e.toString().equals("java.io.EOFException"))
                    return;
                else
                    throw e;
            }
        }
    }

    /**
     * @param allDatas
     * @return
     */
    private byte[] natpRecvData(byte[] allDatas, DataInputStream din) throws IOException {
        int continueFlag = allDatas[2];
        short packNum = bytesToShort(allDatas, 4);
        appendBytes(allDatas, 6, allDatas.length - 10);
        DataOutputStream dout = new DataOutputStream(getOutputStream());
        while (continueFlag == 0x01) {
            natpSendProtocol(dout, (short) (packNum + 1));
            int len = din.readInt();
            byte[] datas = new byte[len];
            readFully(din, datas);
            continueFlag = datas[2];
            packNum = bytesToShort(datas, 4);
            if (Crc32_Agree.crc32_2(datas, 0, datas.length - 4) != getCrc(datas))
                throw new IOException("crc校验不符");
            appendBytes(datas, 6, datas.length - 10);
        }
        return getAllDatas();
    }

    /**
     * @param dout
     * @param packNum
     */
    private void natpSendProtocol(DataOutputStream dout, short packNum) throws IOException {
        byte[] datas = new byte[6];
        datas[0] = 1; /* 目前版本为1 */
        datas[1] = 1; /* 协议包类型为1 */
        datas[2] = 0; /* 无后续包 */
        datas[3] = 0; /* 密压标志 */
        datas[4] = (byte) ((packNum >>> 8) & 0xFF); /* 包序号 */
        datas[5] = (byte) ((packNum >>> 0) & 0xFF);
        dout.writeInt(10);
        dout.write(datas);
        dout.writeInt(Crc32_Agree.crc32_2(datas, 0, datas.length));
        dout.flush();
    }

    /**
     * @param allDatas
     * @param i
     * @param j
     */
    private void appendBytes(byte[] allDatas, int i, int j) {
        System.arraycopy(allDatas, i, recvDatas, dataLen, j);
        dataLen += j;
    }

    private byte[] getAllDatas() {
        byte[] ret = new byte[dataLen];
        System.arraycopy(recvDatas, 0, ret, 0, dataLen);
        for (int i = 0; i < dataLen; i++)
            recvDatas[i] = 0;
        dataLen = 0;
        return ret;
    }

    /**
     * @param allDatas
     * @param i
     * @return
     */
    private short bytesToShort(byte[] allDatas, int i) {
        int i1 = allDatas[i] & 0xFF;
        int i2 = allDatas[i + 1] & 0xFF;
        return (short) ((i1 << 8) + (i2 << 0));
    }

    /**
     * @param allDatas
     * @return
     */
    private int getCrc(byte[] allDatas) throws IOException {
        if (allDatas.length < 4)
            throw new IOException("crc校验时数据长度太小");
        int i1 = allDatas[allDatas.length - 4] & 0xFF;
        int i2 = allDatas[allDatas.length - 3] & 0xFF;
        int i3 = allDatas[allDatas.length - 2] & 0xFF;
        int i4 = allDatas[allDatas.length - 1] & 0xFF;
        return ((i1 << 24) + (i2 << 16) + (i3 << 8) + (i4 << 0));
    }

    /**设置输出流
     * @param stream 输出流
     */
    public void setOutputStream(OutputStream stream) {
        _outputStream = stream;

    }

    /**设置输入流
     * @param stream 输入流
     */
    public void setInputStream(InputStream stream) {
        _inputStream = stream;

    }

    /**获取输入流
     * @return    	输入流
     */
    public InputStream getInputStream() {
        return _inputStream;
    }

    /**获取输出流
     * @return 输出流
    	 */
    public OutputStream getOutputStream() {
        return _outputStream;
    }


    /**构造器
     * 
     */
    public DataTransfer() {

        
    }

    /**
     * @return
     */
    public String getAgentNo() {
        return _agentNo;
    }

    /**
     * @return
     */
    public String getAreaNo() {
        return _areaNo;
    }

    /**
     * @return
     */
    public String getBankNo() {
        return _bankNo;
    }

    /**
     * @return
     */
    public String getBusinessType() {
        return _businessType;
    }

    /**
     * @return
     */
    public String getTellerNo() {
        return _tellerNo;
    }

    /**
     * @return
     */
    public String getTradeCode() {
        return _tradeCode;
    }

    /**
     * @param string
     */
    public void setAgentNo(String string) {
        _agentNo = string;
    }

    /**
     * @param string
     */
    public void setAreaNo(String string) {
        _areaNo = string;
    }

    /**
     * @param string
     */
    public void setBankNo(String string) {
        _bankNo = string;
    }

    /**
     * @param string
     */
    public void setBusinessType(String string) {
        _businessType = string;
    }

    /**
     * @param string
     */
    public void setTellerNo(String string) {
        _tellerNo = string;
    }

    /**
     * @param string
     */
    public void setTradeCode(String string) {
        _tradeCode = string;
    }

    /**
     * @return
     */
    public int getProtoclVersion() {
        return _protoclVersion;
    }

    /**
     * @param i
     */
    public void setProtoclVersion(int i) {
        _protoclVersion = i;
    }

    /**
     * @return
     */
    public String getFileServerName() {
        return _fileServerName;
    }

    /**
     * @param string
     */
    public void setFileServerName(String string) {
        _fileServerName = string;
    }

    /**
     * <DL><DT><B>
     * 读取_reserve.
     * </B></DT><p><DD>
     * 读取成员变量_reserve
     * </DD></DL><p>
     */
    public String getReserve() {
        return _reserve;
    }

    /**
     * <DL><DT><B>
     * 设置_reserve.
     * </B></DT><p><DD>
     * 设置成员变量_reserve
     * </DD></DL><p>
     */
    public void setReserve(String _reserve) {
        this._reserve = _reserve;
    }

    /**
     * <DL><DT><B>
     * 读取_templateCode.
     * </B></DT><p><DD>
     * 读取成员变量_templateCode
     * </DD></DL><p>
     */
    public String getTemplateCode() {
        return _templateCode;
    }

    /**
     * <DL><DT><B>
     * 设置_templateCode.
     * </B></DT><p><DD>
     * 设置成员变量_templateCode
     * </DD></DL><p>
     */
    public void setTemplateCode(String code) {
        _templateCode = code;
    }

    /**
     * <DL><DT><B>
     * 读取_natpVersion.
     * </B></DT><p><DD>
     * 读取成员变量_natpVersion
     * </DD></DL><p>
     */
    public int getNatpVersion() {
        return _natpVersion;
    }

    /**
     * <DL><DT><B>
     * 设置_natpVersion.
     * </B></DT><p><DD>
     * 设置成员变量_natpVersion
     * </DD></DL><p>
     */
    public void setNatpVersion(int version) {
        _natpVersion = version;
    }
}

/*
 * Copyright(C) 2006 Agree Tech, All rights reserved.
 * 
 * Created on 2006-8-11   by Xu Haibo
 */

package cn.com.yitong.util.natp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <DL><DT><B>
 * natp通讯
 * </B></DT><p><DD>
 * 详细介绍
 * </DD></DL><p>
 * 
 * <DL><DT><B>使用范例</B></DT><p><DD>
 * 使用范例说明
 * </DD></DL><p>
 * 
 * @author hxy
 * @author agree
 * @version 1.00, Aug 15, 2006  
 */
public class CommunicationNatp implements INatp {
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(CommunicationNatp.class);

    // default encoding
    private static final String ENCODING = "GBK";

    private DataTransfer dataTransfer;

    private ByteBuffer buf;

    @SuppressWarnings("rawtypes")
	private List nameList;

    @SuppressWarnings("rawtypes")
	private List valueList;

    private StringBuffer sendLog = new StringBuffer();

    private StringBuffer receiveLog = new StringBuffer();

    /**
     * <DL><DT><B>
     * 构造器.
     * </B></DT><p><DD>
     * 构造器说明
     * </DD></DL><p>
     * @param communication
     */
    @SuppressWarnings("rawtypes")
	public CommunicationNatp() {
        super();
        dataTransfer = new DataTransfer();
        buf = ByteBuffer.allocate(4096);
        nameList = new ArrayList();
        valueList = new ArrayList();
    }

    public int getRecordCount(String fieldName) {
        // 1. check
        if (fieldName == null) {
            return -1;
        }

        // 2. count
        int count = 0;
        for (int i = 0; i < nameList.size(); i++) {
            if (fieldName.equals(nameList.get(i))) {
                // match : A compare to A
                count++;
            }
        }
        return count;
    }

    public int init(int natpVersion, String transCode, String templateCode, String reservedCode) {
        dataTransfer.setNatpVersion(natpVersion);
        dataTransfer.setTradeCode(transCode);
        dataTransfer.setTemplateCode(templateCode);
        dataTransfer.setReserve(reservedCode);
        return 0;
    }

    public void pack(String fieldName, String value) throws Exception {
        // 1. check
        if (fieldName == null || value == null) {
            throw new Exception("打包数据不能为null");
        }
        buf.putShort((short) fieldName.getBytes().length);
        buf.put(fieldName.getBytes());
        buf.putShort((short) value.getBytes().length);
        buf.put(value.getBytes());
        sendLog.append("\t\t");
        sendLog.append(fieldName).append(":").append(value).append("\n");
    }

    public void pack(String[] fieldNames, String[] values) throws Exception {
        // 1. check
        if (fieldNames == null || values == null) {
            throw new Exception("打包数据不能为null");
        }
        if (fieldNames.length != values.length) {
            throw new Exception("字段数量不匹配");
        }
        // 2. pack
        for (int i = 0; i < fieldNames.length; i++) {
            buf.putShort((short) fieldNames[i].getBytes().length);
            buf.put(fieldNames[i].getBytes());
            buf.putShort((short) values[i].getBytes().length);
            buf.put(values[i].getBytes());
            //	        if(sendLog.length()>0)
            sendLog.append("\t\t");
            sendLog.append(fieldNames[i]).append(":").append(values[i]).append("\n");
        }
    }

    public String unpack(String fieldName, int iPos) throws Exception {
        // 1. check
        if (fieldName == null) {
            throw new Exception("字段名称不能为null");
        }

        // 2. find iPos value
        int count = 0;
        int i;
        for (i = 0; i < nameList.size(); i++) {
            if (fieldName.equals(nameList.get(i))) {
                // match : A compare to A
                count++;
                if (count >= iPos)
                    break;
            }
        }
        if (i == nameList.size())
            return null;
        return (String) valueList.get(i);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public String[] unpack(String fieldName) throws Exception {
        // 1. check
        if (fieldName == null) {
            throw new Exception("字段名称不能为null");
        }

        // 2. return all values
        List findList = new ArrayList();
        for (int i = 0; i < nameList.size(); i++) {
            if (fieldName.equals(nameList.get(i)))
                findList.add(valueList.get(i));
        }
        return (String[]) findList.toArray(new String[0]);
    }

    @SuppressWarnings("unchecked")
	public void exchange(String serverName) throws Exception {
        Socket socket = null;
        try {
            socket = SocketFactory.createSocket(serverName);
            dataTransfer.setOutputStream(socket.getOutputStream());
            dataTransfer.setInputStream(socket.getInputStream());
            byte[] sendData = new byte[buf.position()];
            for (int i = 0; i < sendData.length; i++) {
                sendData[i] = buf.get(i);
            }
            logger.info("\n\t" + TimeFactory.getCurrentTime() + "发送出的信息：" + "\n" + "\t\t" + "NatpVersion:"
                    + dataTransfer.getNatpVersion() + "\n" + "\t\t" + "TradeCode:" + dataTransfer.getTradeCode() + "\n"
                    + "\t\t" + "TemplateCode:" + dataTransfer.getTemplateCode() + "\n" + "\t\t" + "Reserve:"
                    + dataTransfer.getReserve() + "\n" + sendLog);
            sendLog.setLength(0);
            nameList.clear();
            valueList.clear();
            dataTransfer.natpSendDatas(sendData);
            byte[] retData = dataTransfer.natpRecv();
            ByteArrayInputStream bin = new ByteArrayInputStream(retData);
            DataInputStream din = new DataInputStream(bin);
            while (din.available() > 0) {
                int nLen = din.readShort();
                byte[] byName = new byte[nLen];
                din.read(byName);
                String sName = new String(byName, ENCODING);
                nLen = din.readShort();
                byte[] byValue = new byte[nLen];
                din.read(byValue);
                String sValue = new String(byValue, ENCODING);
                nameList.add(sName);
                valueList.add(sValue);
                //		        if(receiveLog.length()>0)
                receiveLog.append("\t\t");
                receiveLog.append(sName).append(":").append(sValue).append("\n");
            }
            logger.info("\n\t" + TimeFactory.getCurrentTime() + "接收的信息：" + "\n" + receiveLog);
            receiveLog.setLength(0);
            din.close();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception(e);
        }
        finally {
            if (socket != null)
                socket.close();
        }
    }

    @SuppressWarnings("unchecked")
	public String exchange_result(String serverName) throws Exception {
        Socket socket = null;
        try {
            //返回结果
            String result = null;
            socket = SocketFactory.createSocket(serverName);
            dataTransfer.setOutputStream(socket.getOutputStream());
            dataTransfer.setInputStream(socket.getInputStream());
            byte[] sendData = new byte[buf.position()];
            for (int i = 0; i < sendData.length; i++) {
                sendData[i] = buf.get(i);
            }
            logger.info("\n\t" + TimeFactory.getCurrentTime() + "发送出的信息：" + "\n" + "\t\t" + "NatpVersion:"
                    + dataTransfer.getNatpVersion() + "\n" + "\t\t" + "TradeCode:" + dataTransfer.getTradeCode() + "\n"
                    + "\t\t" + "TemplateCode:" + dataTransfer.getTemplateCode() + "\n" + "\t\t" + "Reserve:"
                    + dataTransfer.getReserve() + "\n" + sendLog);
            sendLog.setLength(0);
            nameList.clear();
            valueList.clear();
            dataTransfer.natpSendDatas(sendData);
            byte[] retData = dataTransfer.natpRecv();
            ByteArrayInputStream bin = new ByteArrayInputStream(retData);
            DataInputStream din = new DataInputStream(bin);
            while (din.available() > 0) {
                int nLen = din.readShort();
                byte[] byName = new byte[nLen];
                din.read(byName);
                String sName = new String(byName, ENCODING);
                nLen = din.readShort();
                byte[] byValue = new byte[nLen];
                din.read(byValue);
                String sValue = new String(byValue, ENCODING);
                nameList.add(sName);
                valueList.add(sValue);
                if ("errorCode".equals(sName) && "0000".equals(sValue)) {
                    result = "发送成功";
                }
                
                receiveLog.append("\t\t");
                receiveLog.append(sName).append(":").append(sValue).append("\n");
            }
            logger.info("\n\t" + TimeFactory.getCurrentTime() + "接收的信息：" + "\n" + receiveLog);
            receiveLog.setLength(0);
            din.close();
            return result;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "发送失败!";
        }
        finally {
            if (socket != null)
                socket.close();
        }
    }
}

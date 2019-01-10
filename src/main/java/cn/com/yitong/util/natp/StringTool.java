package cn.com.yitong.util.natp;

import java.util.StringTokenizer;

/**字符串处理工具类<br/>提供一些字符串处理的静态方法
 * @author  陈育生
 * @author agree
 * @version 1.0 
 * @since 第三方通讯网关 1.0
*/
public class StringTool {
    public final static int LEFT = 0;

    public final static int RIGHT = 1;

    public final static int ALL = 2;

    /**
     * 获取一个字符串以一个特定分割符划分的所有分节
     * @param sSource  要分割的字符串
     * @param sDelim   分割符
     * @return 划分后的所有分节
     * @since StringTool 1.0 
     */
    public static String[] getTokens(String sSource, String sDelim) {

        StringTokenizer tokenizer = new StringTokenizer(sSource, sDelim);
        int iCount = tokenizer.countTokens();
        String[] sTokens = new String[iCount];
        for (int i = 0; i < iCount; i++) {
            sTokens[i] = tokenizer.nextToken();
        }
        return (sTokens);
    }

    /**
     * 判断一个字符串是否为合法数字
     * @param sSource  要判断的字符串
     * @param bInt  是否局限于整数：true:整数范围内；false:实数范围内。
     * @return true:合法数字；false:不合法数字
     * @since StringTool 1.0 
     */
    public static boolean isNum(String sSource, boolean bInt) {
        boolean bHex = false;
        try {
            if (bInt) {
                for (int i = 0, n = sSource.length(); i < n; i++) {
                    if (Character.isLetter(sSource.charAt(i))) {
                        bHex = true;
                        break;
                    }
                }
                if (!bHex)
                    Integer.parseInt(sSource);
            }
            else
                Float.parseFloat(sSource);
            return (true);
        }
        catch (NumberFormatException e) { //不是数字
            return (false);
        }
    }

    /**
     * 以指定方式填充指定字符到字符串中
     * @param sSource 要填充的字符串
     * @param ch  填充字符
     * @param nLen  填充后长度
     * @param nLen  填充方向：true:左边；false:右边
     * @return 	填充后字符串
     * @since StringTool 1.0 
     */
    public static String convertToHex(byte[] bySrc, int offset, int len) {
        byte[] byNew = new byte[len];
        String sTmp = "";
        String sResult = "";
        for (int i = 0; i < len; i++)
            byNew[i] = bySrc[offset + i];

        for (int i = 0, n = byNew.length; i < n; i++) {
            sTmp = Integer.toHexString(byNew[i] & 0XFF);
            sTmp = fillChar(sTmp, '0', 2, true) + " ";
            sResult += sTmp;
        }
        return (sResult);
    }

    public static String formatInHex(byte[] bySrc, int nLineLen) {
        final String sRepalce = "\n\t\r\0";
        int nLength = bySrc.length;

        int nLine = nLength / nLineLen + 1;

        String sLineTmp = "";
        byte[] byNew = new byte[nLine * nLineLen];
        for (int i = 0; i < nLength; i++)
            byNew[i] = bySrc[i];

        String sRet = "";

        for (int i = 0; i < nLine; i++) {
            if (byNew[(i + 1) * nLineLen - 1] < 0) {
                sLineTmp = new String(byNew, i * nLineLen, nLineLen - 1);
                sLineTmp += "?";
            }
            else {
                if (byNew[i * nLineLen] < 0) {
                    sLineTmp = new String(byNew, i * nLineLen + 1, nLineLen - 1);
                    sLineTmp = "?" + sLineTmp;
                }
                else
                    sLineTmp = new String(byNew, i * nLineLen, nLineLen);
            }
            sLineTmp = "  " + sLineTmp;
            sRet += "[" + fillChar(Integer.toHexString(i * nLineLen + 1), '0', 4, true) + "-"
                    + fillChar(Integer.toHexString(i * nLineLen + nLineLen), '0', 4, true) + "] ";
            sRet += convertToHex(byNew, i * nLineLen, nLineLen);

            for (int j = 0, n = sRepalce.length(); j < n; j++)
                sLineTmp = sLineTmp.replace(sRepalce.charAt(j), '.');

            sRet += sLineTmp + "\n";
        }

        return (sRet);
    }

    public static String formatInHex(String sSource, int nLineLen) {
        final String sRepalce = "\n\t\r\0";
        byte[] bySrc = sSource.getBytes();
        int nLength = bySrc.length;

        //System.out.println("nLength = "+nLength); 
        int nLine = nLength / nLineLen + 1;

        String sLineTmp = "";
        byte[] byNew = new byte[nLine * nLineLen];
        for (int i = 0; i < nLength; i++)
            byNew[i] = bySrc[i];
        //System.out.println("byNew.nLength = "+byNew.length); 

        String sRet = "";

        for (int i = 0; i < nLine; i++) {
            /*			
            			int k = 0;
            			for( int j=0; j<nLineLen; j++)
            			{
            				if( byNew[i*nLineLen+j]<0 )
            					k++;
            			}
            */
            if (byNew[(i + 1) * nLineLen - 1] < 0) {
                sLineTmp = new String(byNew, i * nLineLen, nLineLen - 1);
                sLineTmp += "?";
            }
            else {
                if (byNew[i * nLineLen] < 0) {
                    sLineTmp = new String(byNew, i * nLineLen + 1, nLineLen - 1);
                    sLineTmp = "?" + sLineTmp;
                }
                else
                    sLineTmp = new String(byNew, i * nLineLen, nLineLen);
            }
            sLineTmp = "  " + sLineTmp;
            sRet += convertToHex(byNew, i * nLineLen, nLineLen);

            for (int j = 0, n = sRepalce.length(); j < n; j++)
                sLineTmp = sLineTmp.replace(sRepalce.charAt(j), '.');

            sRet += sLineTmp + "\n";
        }

        return (sRet);
    }

    public static String getChinese(int nChinese) {
        String sInt = Integer.toString(nChinese, 16);
        byte[] byInt = new byte[2];

        for (int i = 0, n = byInt.length; i < n; i++) {
            String sTmp = sInt.substring(i * 2, (i + 1) * 2);
            byInt[i] = (byte) Integer.parseInt(sTmp, 16);
        }

        return new String(byInt);
    }

    public static byte[] addBytes(byte[] bySrc, byte[] byAdd) {
        int nSrc = bySrc.length;
        byte[] byRet = new byte[bySrc.length + byAdd.length];
        for (int i = 0, n = bySrc.length; i < n; i++) {
            byRet[i] = bySrc[i];
        }
        for (int i = 0, n = byAdd.length; i < n; i++) {
            byRet[i + nSrc] = byAdd[i];
        }
        return byRet;
    }

    /**去除字符串中指定方向上的指定字符
     * @param bySrc 要去除的字符串
     * @param ch 去除的特定字符
     * @param nPos 去除的方向：LEFT:左边；RIGTH:右边；ALL：全部
     * @return 去除好的字符串
     */
    public static byte[] trim(byte[] bySrc, char ch, int nPos) {
        int nLen = bySrc.length;
        byte[] byRet = new byte[nLen];
        if (nPos == LEFT) {
            int i = 0;
            for (; i < nLen; i++) {
                if (bySrc[i] != (byte) ch)
                    break;
            }
            for (int j = i; j < nLen; j++)
                byRet[j - i] = bySrc[j];
        }
        else if (nPos == RIGHT) {
            int i = nLen - 1;
            for (; i >= 0; i--) {
                if (bySrc[i] != (byte) ch)
                    break;
            }
            int nRight = i + 1;
            for (int j = 0; j < nRight; j++)
                byRet[j] = bySrc[j];
        }
        else if (nPos == ALL) {
            int i = nLen - 1;
            for (; i >= 0; i--) {
                if (bySrc[i] != (byte) ch)
                    break;
            }
            int nRight = i + 1;
            for (i = 0; i < nLen; i++) {
                if (bySrc[i] != (byte) ch)
                    break;
            }
            int nLeft = i;
            for (int j = nLeft; j < nRight; j++)
                byRet[j - nLeft] = bySrc[j];
        }
        return trimnull(byRet);
    }

    /**去掉字符串两头的空值
    	 * @param byRet 要去除的字符串
     * @return 去除好的字符串
     */

    public static byte[] trimnull(byte[] byRet) {
        int startPos = 0;
        int endPos = byRet.length - 1;
        for (int i = 0; i < byRet.length; i++) {
            if (byRet[i] != 0) {
                startPos = i;
                break;
            }
            if (i == (byRet.length - 1) && byRet[i] == 0) {
                return null;
            }
        }
        for (int i = byRet.length - 1; i >= 0; i--) {
            if (byRet[i] != 0) {
                endPos = i;
                break;
            }
        }
        byte[] byTmp = new byte[endPos - startPos + 1];
        System.arraycopy(byRet, startPos, byTmp, 0, endPos - startPos + 1);
        return byTmp;
    }

    /**去除字符串中指定方向上的指定字符
     * @param sSrc 要去除的字符串
     * @param ch 去除的特定字符
     * @param nPos 去除的方向：LEFT:左边；RIGTH:右边；ALL：全部
     * @return 去除好的字符串
     */
    public static String trim(String sSrc, char ch, int nPos) {
        if (sSrc == null || sSrc.equals(""))
            return sSrc;
        byte[] bySrc = sSrc.getBytes();
        int nLen = bySrc.length;
        byte[] byRet = new byte[nLen];
        if (nPos == LEFT) {
            int i = 0;
            for (; i < nLen; i++) {
                if (bySrc[i] != (byte) ch)
                    break;
            }
            for (int j = i; j < nLen; j++)
                byRet[j - i] = bySrc[j];
        }
        else if (nPos == RIGHT) {
            int i = nLen - 1;
            for (; i >= 0; i--) {
                if (bySrc[i] != (byte) ch)
                    break;
            }
            int nRight = i + 1;
            for (int j = 0; j < nRight; j++)
                byRet[j] = bySrc[j];
        }
        else if (nPos == ALL) {
            int i = nLen - 1;
            for (; i >= 0; i--) {
                if (bySrc[i] != (byte) ch)
                    break;
            }
            int nRight = i + 1;
            for (i = 0; i < nLen; i++) {
                if (bySrc[i] != (byte) ch)
                    break;
            }
            int nLeft = i;
            for (int j = nLeft; j < nRight; j++)
                byRet[j - nLeft] = bySrc[j];
        }
        return new String(byRet).trim();
    }

    /**用特定字符填充字符串
     * @param sSrc 要填充的字符串
     * @param ch 用于填充的特定字符
     * @param nLen 要填充到的长度
     * @param bLeft 要填充的方向：true:左边；false:右边
     * @return 填充好的字符串
     */
    public static String fill(String sSrc, char ch, int nLen, boolean bLeft) {
        byte[] bTmp = trimnull(sSrc.getBytes());
        sSrc = new String(bTmp);
        if (sSrc == null || sSrc.equals("")) {
            StringBuffer sbRet = new StringBuffer();
            for (int i = 0; i < nLen; i++)
                sbRet.append(ch);

            return sbRet.toString();
        }
        byte[] bySrc = sSrc.getBytes();
        int nSrcLen = bySrc.length;
        if (nSrcLen >= nLen) {
            return sSrc;
        }
        byte[] byRet = new byte[nLen];
        if (bLeft) {
            for (int i = 0, n = nLen - nSrcLen; i < n; i++)
                byRet[i] = (byte) ch;
            for (int i = nLen - nSrcLen, n = nLen; i < n; i++)
                byRet[i] = bySrc[i - nLen + nSrcLen];
        }
        else {
            for (int i = 0, n = nSrcLen; i < n; i++)
                byRet[i] = bySrc[i];
            for (int i = nSrcLen, n = nLen; i < n; i++)
                byRet[i] = (byte) ch;
        }
        return new String(byRet);
    }

    /**
     * 以指定方式填充指定字符到字符串中
     * @param sSource 要填充的字符串
     * @param ch  填充字符
     * @param nLen  填充后长度
     * @param bLeft  填充方向：true:左边；false:右边
     * @return 	填充后字符串
     * @since StringTool 1.0 
     */
    //	public static String fillChar(String sSource, char ch, int nLen, boolean bLeft)
    //	{
    //		//取字符串长度
    //		int nSrcLen = sSource.length();
    //		
    //		if( nSrcLen<=nLen )
    //		{	//左填充
    //			if( bLeft )
    //				for( int i=0; i<(nLen-nSrcLen); i++)
    //					sSource = ch + sSource;
    //			else//右填充
    //				for( int i=0; i<(nLen-nSrcLen); i++)
    //					sSource += ch;
    //		}
    //		
    //		//返回
    //		return(sSource);
    //	}
    public static String fillChar(String sSource, char ch, int nLen, boolean bLeft) {

        //取字符串长度
        int nSrcLen = sSource.length();

        if (nSrcLen <= nLen) { //左填充
            StringBuffer buffer = new StringBuffer();
            if (bLeft) {
                for (int i = 0; i < (nLen - nSrcLen); i++) {
                    buffer.append(ch);
                }
                buffer.append(sSource);
            }
            else //右填充
            {
                buffer.append(sSource);
                for (int i = 0; i < (nLen - nSrcLen); i++)
                    buffer.append(ch);
            }
            return (buffer.toString());
        }
        return sSource;
        //返回

    }

    /**
     * 字节数组转换为十六进制表方法的缺省方法。
     * 产生每行十六个字节，共三列的结果。
     * @param byteSrc 被转换的字节数组
     * @return 十六进制表
     */
    public static String toHexTable(byte[] byteSrc) {
        return toHexTable(byteSrc, 16, 7);
    }

    /**
     * 字节数组转换为十六进制表方法的缺省方法。
     * 产生三列的结果。
     * @param byteSrc 输出时每一行的长度，也就是每一行包含多少个字节的信息
     * @param lengthOfLine 每行显示的字节数
     * @return 十六进制表
     */
    public static String toHexTable(byte[] byteSrc, int lengthOfLine) {
        return toHexTable(byteSrc, lengthOfLine, 7);
    }

    /**
     * 字节数组转换为十六进制表方法。
     * @author 齐海力
     * @param byteSrc 被转换的字节数组
     * @param lengthOfLine 输出时每一行的长度，也就是每一行包含多少个字节的信息
     * @param column 列信息　八进制数据：最左一位为左列；中间一位为中列；最右一位为右列。
     * @return 十六进制表
     */
    public static String toHexTable(byte[] byteSrc, int lengthOfLine, int column) {
        //十六进制表的字符缓冲器
        StringBuffer hexTableBuffer = new StringBuffer(256);
        //需要输出的行数
        //		int lineCount = (int)Math.ceil((double)byteSrc.length/(double)lengthOfLine);
        int lineCount = byteSrc.length / lengthOfLine;
        int totalLen = byteSrc.length;
        if (byteSrc.length % lengthOfLine != 0)
            lineCount += 1;
        //每行的字节数组
        byte[] lineByte;
        //循环添加每一行的字符串信息。
        for (int lineNumber = 0; lineNumber < lineCount; lineNumber++) {
            //取得这一行第一个字节的位置。
            int startPos = lineNumber * lengthOfLine;
            //取得这一行的字节数组。
            lineByte = new byte[Math.min(lengthOfLine, totalLen - startPos)];
            System.arraycopy(byteSrc, startPos, lineByte, 0, lineByte.length);

            int columnA = column & 4;
            if (4 == columnA) {
                int count = 10 * lineNumber;
                //向字符缓冲器中添加字节地址信息。
                String addrStr = Integer.toString(count);
                int len = addrStr.length();
                for (int i = 0; i < 8 - len; i++)
                    hexTableBuffer.append('0');
                //				addrStr = fillChar(addrStr, '0', 8, true);
                hexTableBuffer.append(addrStr);
                hexTableBuffer.append("h: ");
            }

            int columnB = column & 2;
            if (2 == columnB) {
                //向这一行写十六进制数。
                StringBuffer byteStrBuf = new StringBuffer();
                for (int i = 0; i < lineByte.length; i++) {
                    String num = Integer.toHexString(lineByte[i] & 0xff);
                    if (num.length() < 2)
                        byteStrBuf.append('0');
                    byteStrBuf.append(num);
                    byteStrBuf.append(' ');
                    //					byteStrBuf.append(fillChar(Integer.toHexString(lineByte[i]&0xff), '0', 2, true)+ " ");
                }
                //				hexTableBuffer.append(byteStrBuf);
                hexTableBuffer.append(fillChar(byteStrBuf.toString(), ' ', 48, false));
                hexTableBuffer.append("; ");
            }

            int columnC = column & 1;
            if (1 == columnC) {
                //写明码字符。
                for (int i = 0; i < lineByte.length; i++) {
                    char c = (char) lineByte[i];
                    //如果字符的ASCII码小于33就显示"."。
                    if (c < 33)
                        c = '.';
                    try {
                        //如果是中文字符就显示中文字符。
                        if (c >= 0xa0 && i < (lineByte.length - 1)) {
                            char c2 = (char) lineByte[i + 1];
                            if (c2 >= 0xa0) {
                                String str = new String(lineByte, i, 2);
                                hexTableBuffer.append(str);
                                i++;
                                continue;
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    hexTableBuffer.append("");
                    hexTableBuffer.append(c);
                }
            }
            if (lineNumber >= lineCount - 1)
                break;
            hexTableBuffer.append('\n');
        }
        return hexTableBuffer.toString();
    }

    public static void main(String[] args) {
        String[] strData = { "b5", "da", "b5", "00", "da", "01", "88", "41", "00", "00", "04", "00", "00", "00", "11",
                "00", "00", "00", "03", "12", "09", "16", "09", "06", "98", "86", "20", "06", "98", "86", "20", "36",
                "39", "30", "30", "30", "33", "38", "33", "33", "32", "37", "31", "30", "30", "30", "30", "30", "30",
                "30", "30", "30", "30", "37", "31", "30", "30", "31", "00", "20", "32", "32", "36", "3d", "33", "33",
                "37", "3c", "37", "30", "3d", "33", "39", "30", "3b", "31", "3b", "37", "31", "3e", "01", "01", "06",
                "62", "48", "10", "00", "01", "54", "54", "54", "54", "54", "54", "54", "54" };
        //将字符串数组的数据转化成字节数组的数据
        byte[] byteData = new byte[strData.length];
        for (int i = 0; i < strData.length; i++) {
            byteData[i] = (byte) Integer.parseInt(strData[i], 16);
        }
        byte[] test = "12345678ab中文ef12345678ab中文ef12345678ab中文ef12345678ab中文ef12345678ab中文ef12345678ab中文ef12345678ab中文ef12345678ab中文ef"
                .getBytes();
        String str = null;
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++)
            str = toHexTable(test, 16);
        System.out.println("spending time = " + (System.currentTimeMillis() - start));
        System.out.println(str);
    }

}

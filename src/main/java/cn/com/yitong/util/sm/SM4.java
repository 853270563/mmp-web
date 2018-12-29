//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.yitong.util.sm;

import cn.com.yitong.util.sm.SM4Digest;
import cn.com.yitong.util.sm.a;
import org.bouncycastle.util.encoders.Hex;

public class SM4 {
    public SM4() {
    }

    public static String encodeHexStr(String var0, String var1) {
        return a.a(encode(Hex.decode(var0), Hex.decode(var1)));
    }

    public static byte[] encode(byte[] var0, byte[] var1) {
        int var2;
        int var3 = var2 = var0.length;
        if(var2 % 16 > 0) {
            var3 = var2 + (16 - var2 % 16);
        }

        byte[] var4 = new byte[var3];
        System.arraycopy(var0, 0, var4, 0, var2);

        for(int var7 = var2; var7 < var3; ++var7) {
            var4[var7] = 32;
        }

        var0 = new byte[var4.length];
        var2 = 0;

        for(var3 = var4.length; var2 + 16 <= var3; var2 += 16) {
            byte[] var5 = new byte[16];

            for(int var6 = 0; var6 < 16; ++var6) {
                var5[var6] = var4[var2 + var6];
            }

            byte[] var9 = encode16(var5, var1);

            for(int var8 = 0; var8 < var9.length; ++var8) {
                var0[var2 + var8] = var9[var8];
            }
        }

        return var0;
    }

    public static String decodeHexStr(String var0, String var1) {
        return a.a(decode(Hex.decode(var0), Hex.decode(var1)));
    }

    public static byte[] decode(byte[] var0, byte[] var1) {
        byte[] var2 = new byte[var0.length];
        int var3 = 0;

        for(int var4 = var0.length; var3 + 16 <= var4; var3 += 16) {
            byte[] var5 = new byte[16];

            for(int var6 = 0; var6 < 16; ++var6) {
                var5[var6] = var0[var3 + var6];
            }

            byte[] var8 = decode16(var5, var1);

            for(int var7 = 0; var7 < var8.length; ++var7) {
                var2[var3 + var7] = var8[var7];
            }
        }

        return var2;
    }

    public static byte[] encode16(byte[] var0, byte[] var1) {
        byte[] var2 = new byte[16];
        (new SM4Digest()).sms4(var0, 16, var1, var2, 1);
        return var2;
    }

    public static byte[] decode16(byte[] var0, byte[] var1) {
        byte[] var2 = new byte[16];
        (new SM4Digest()).sms4(var0, 16, var1, var2, 0);
        return var2;
    }
}

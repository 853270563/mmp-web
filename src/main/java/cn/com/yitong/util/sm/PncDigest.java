//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.yitong.util.sm;

import cn.com.yitong.util.sm.SM4;
import cn.com.yitong.util.sm.a;
import java.util.Random;

public class PncDigest {
    private static PncDigest a = new PncDigest();

    private PncDigest() {
    }

    public static PncDigest getInstance() {
        return a;
    }

    public byte[] reqEncode(byte[] var1, byte[] var2) {
        String[] var10000 = new String[]{cn.com.yitong.util.sm.a.a(var2)};
        int var4;
        int var5 = (var4 = var2.length) % 16;
        byte[] var6;
        (var6 = new byte[var5 = 16 - var5])[0] = 29;
        Random var7 = new Random();

        for(int var3 = 1; var3 < var5; ++var3) {
            var6[var3] = (byte)(var7.nextInt(150) % 150 + 30);
        }

        byte[] var8 = new byte[var5 + var4];
        System.arraycopy(var2, 0, var8, 0, var4);
        System.arraycopy(var6, 0, var8, var4, var5);
        var2 = var8;
        var10000 = new String[]{cn.com.yitong.util.sm.a.a(var8)};
        var8 = a();
        var10000 = new String[]{cn.com.yitong.util.sm.a.a(var8)};
        byte[] var9 = var8;
        byte[] var10 = new byte[(var5 = var2.length) + 16];
        var8 = a(var2, var8);
        var10000 = new String[]{cn.com.yitong.util.sm.a.a(var8)};
        System.arraycopy(var8, 0, var10, 0, var5);
        System.arraycopy(var9, 0, var10, var5, 16);
        var10000 = new String[]{cn.com.yitong.util.sm.a.a(var10)};
        return SM4.encode(var10, var1);
    }

    public byte[] reqDecode(byte[] var1, byte[] var2) {
        int var5;
        byte[] var3 = new byte[var5 = (var1 = SM4.decode(var2, var1)).length - 16];
        byte[] var4 = new byte[16];
        System.arraycopy(var1, var5, var4, 0, 16);
        System.arraycopy(var1, 0, var3, 0, var5);
        String[] var10000 = new String[]{cn.com.yitong.util.sm.a.a(var1)};
        var10000 = new String[]{cn.com.yitong.util.sm.a.a(var3)};
        var10000 = new String[]{cn.com.yitong.util.sm.a.a(var4)};
        var1 = a(var3, var4);
        var10000 = new String[]{cn.com.yitong.util.sm.a.a(var1)};
        int var6 = 0;

        for(int var7 = 16; var7 > 0; --var7) {
            if(var1[var5 - var7] == 29) {
                var6 = var5 - var7;
                break;
            }
        }

        var4 = new byte[var6];
        System.arraycopy(var1, 0, var4, 0, var6);
        return var4;
    }

    private static byte[] a() {
        Random var0 = new Random();
        byte[] var1 = new byte[16];

        for(int var2 = 0; var2 < 16; ++var2) {
            var1[var2] = (byte)(var0.nextInt(255) % 255);
        }

        return var1;
    }

    private static byte[] a(byte[] var0, byte[] var1) {
        int var2;
        byte[] var3 = new byte[var2 = var0.length];
        var2 /= 16;

        for(int var4 = 0; var4 < var2; ++var4) {
            int var5 = var4 << 4;

            for(int var6 = 0; var6 < 16; ++var6) {
                var3[var5 + var6] = (byte)(var0[var5 + var6] ^ var1[var6]);
            }
        }

        return var3;
    }
}

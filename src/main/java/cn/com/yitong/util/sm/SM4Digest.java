//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.yitong.util.sm;

import cn.com.yitong.util.sm.b;
import java.util.Arrays;

public class SM4Digest {
    public static final int ENCRYPT = 1;
    public static final int DECRYPT = 0;
    public static final int ROUND = 32;
    private byte[] o = new byte[]{(byte)-42, (byte)-112, (byte)-23, (byte)-2, (byte)-52, (byte)-31, (byte)61, (byte)-73, (byte)22, (byte)-74, (byte)20, (byte)-62, (byte)40, (byte)-5, (byte)44, (byte)5, (byte)43, (byte)103, (byte)-102, (byte)118, (byte)42, (byte)-66, (byte)4, (byte)-61, (byte)-86, (byte)68, (byte)19, (byte)38, (byte)73, (byte)-122, (byte)6, (byte)-103, (byte)-100, (byte)66, (byte)80, (byte)-12, (byte)-111, (byte)-17, (byte)-104, (byte)122, (byte)51, (byte)84, (byte)11, (byte)67, (byte)-19, (byte)-49, (byte)-84, (byte)98, (byte)-28, (byte)-77, (byte)28, (byte)-87, (byte)-55, (byte)8, (byte)-24, (byte)-107, (byte)-128, (byte)-33, (byte)-108, (byte)-6, (byte)117, (byte)-113, (byte)63, (byte)-90, (byte)71, (byte)7, (byte)-89, (byte)-4, (byte)-13, (byte)115, (byte)23, (byte)-70, (byte)-125, (byte)89, (byte)60, (byte)25, (byte)-26, (byte)-123, (byte)79, (byte)-88, (byte)104, (byte)107, (byte)-127, (byte)-78, (byte)113, (byte)100, (byte)-38, (byte)-117, (byte)-8, (byte)-21, (byte)15, (byte)75, (byte)112, (byte)86, (byte)-99, (byte)53, (byte)30, (byte)36, (byte)14, (byte)94, (byte)99, (byte)88, (byte)-47, (byte)-94, (byte)37, (byte)34, (byte)124, (byte)59, (byte)1, (byte)33, (byte)120, (byte)-121, (byte)-44, (byte)0, (byte)70, (byte)87, (byte)-97, (byte)-45, (byte)39, (byte)82, (byte)76, (byte)54, (byte)2, (byte)-25, (byte)-96, (byte)-60, (byte)-56, (byte)-98, (byte)-22, (byte)-65, (byte)-118, (byte)-46, (byte)64, (byte)-57, (byte)56, (byte)-75, (byte)-93, (byte)-9, (byte)-14, (byte)-50, (byte)-7, (byte)97, (byte)21, (byte)-95, (byte)-32, (byte)-82, (byte)93, (byte)-92, (byte)-101, (byte)52, (byte)26, (byte)85, (byte)-83, (byte)-109, (byte)50, (byte)48, (byte)-11, (byte)-116, (byte)-79, (byte)-29, (byte)29, (byte)-10, (byte)-30, (byte)46, (byte)-126, (byte)102, (byte)-54, (byte)96, (byte)-64, (byte)41, (byte)35, (byte)-85, (byte)13, (byte)83, (byte)78, (byte)111, (byte)-43, (byte)-37, (byte)55, (byte)69, (byte)-34, (byte)-3, (byte)-114, (byte)47, (byte)3, (byte)-1, (byte)106, (byte)114, (byte)109, (byte)108, (byte)91, (byte)81, (byte)-115, (byte)27, (byte)-81, (byte)-110, (byte)-69, (byte)-35, (byte)-68, (byte)127, (byte)17, (byte)-39, (byte)92, (byte)65, (byte)31, (byte)16, (byte)90, (byte)-40, (byte)10, (byte)-63, (byte)49, (byte)-120, (byte)-91, (byte)-51, (byte)123, (byte)-67, (byte)45, (byte)116, (byte)-48, (byte)18, (byte)-72, (byte)-27, (byte)-76, (byte)-80, (byte)-119, (byte)105, (byte)-105, (byte)74, (byte)12, (byte)-106, (byte)119, (byte)126, (byte)101, (byte)-71, (byte)-15, (byte)9, (byte)-59, (byte)110, (byte)-58, (byte)-124, (byte)24, (byte)-16, (byte)125, (byte)-20, (byte)58, (byte)-36, (byte)77, (byte)32, (byte)121, (byte)-18, (byte)95, (byte)62, (byte)-41, (byte)-53, (byte)57, (byte)72};
    private int[] p = new int[]{462357, 472066609, 943670861, 1415275113, 1886879365, -1936483679, -1464879427, -993275175, -521670923, -66909679, 404694573, 876298825, 1347903077, 1819507329, -2003855715, -1532251463, -1060647211, -589042959, -117504499, 337322537, 808926789, 1280531041, 1752135293, -2071227751, -1599623499, -1128019247, -656414995, -184876535, 269950501, 741554753, 1213159005, 1684763257};

    public SM4Digest() {
    }

    private static int b(int var0, int var1) {
        return var0 << var1 | var0 >>> 32 - var1;
    }

    private int b(int var1) {
        return (this.o[var1 >>> 24] & 255) << 24 | (this.o[var1 >>> 16 & 255] & 255) << 16 | (this.o[var1 >>> 8 & 255] & 255) << 8 | this.o[var1 & 255] & 255;
    }

    private static int c(int var0) {
        return var0 ^ b(var0, 2) ^ b(var0, 10) ^ b(var0, 18) ^ b(var0, 24);
    }

    private static int d(int var0) {
        return var0 ^ b(var0, 13) ^ b(var0, 23);
    }

    public int sms4(byte[] var1, int var2, byte[] var3, byte[] var4, int var5) {
        int var6 = 0;
        int[] var7 = new int[32];
        int[] var9 = var7;
        byte[] var8 = var3;
        SM4Digest var14 = this;
        int[] var15 = new int[4];
        int[] var11 = new int[4];

        for(int var12 = 0; var12 < 4; ++var12) {
            var11[0] = var8[0 + 4 * var12] & 255;
            var11[1] = var8[1 + 4 * var12] & 255;
            var11[2] = var8[2 + 4 * var12] & 255;
            var11[3] = var8[3 + 4 * var12] & 255;
            var15[var12] = var11[0] << 24 | var11[1] << 16 | var11[2] << 8 | var11[3];
        }

        b.c();
        var15[0] ^= -1548633402;
        var15[1] ^= 1453994832;
        var15[2] ^= 1736282519;
        var15[3] ^= -1301273892;

        int var17;
        int var20;
        for(var17 = 0; var17 < 32; var17 += 4) {
            var20 = var15[1] ^ var15[2] ^ var15[3] ^ var14.p[var17];
            var20 = var14.b(var20);
            var9[var17] = var15[0] ^= d(var20);
            var20 = var15[2] ^ var15[3] ^ var15[0] ^ var14.p[var17 + 1];
            var20 = var14.b(var20);
            var9[var17 + 1] = var15[1] ^= d(var20);
            var20 = var15[3] ^ var15[0] ^ var15[1] ^ var14.p[var17 + 2];
            var20 = var14.b(var20);
            var9[var17 + 2] = var15[2] ^= d(var20);
            var20 = var15[0] ^ var15[1] ^ var15[2] ^ var14.p[var17 + 3];
            var20 = var14.b(var20);
            var9[var17 + 3] = var15[3] ^= d(var20);
        }

        if(var5 == 0) {
            for(var17 = 0; var17 < 16; ++var17) {
                var20 = var9[var17];
                var9[var17] = var9[31 - var17];
                var9[31 - var17] = var20;
            }
        }

        b.c();

        for(byte[] var16 = new byte[16]; var2 >= 16; var6 += 16) {
            var3 = Arrays.copyOfRange(var1, var6, var6 + 16);
            int[] var19 = var7;
            byte[] var18 = var16;
            var8 = var3;
            var14 = this;
            int[] var21 = new int[4];
            var11 = new int[4];

            int var13;
            for(var13 = 0; var13 < 4; ++var13) {
                var11[0] = var8[0 + 4 * var13] & 255;
                var11[1] = var8[1 + 4 * var13] & 255;
                var11[2] = var8[2 + 4 * var13] & 255;
                var11[3] = var8[3 + 4 * var13] & 255;
                var21[var13] = var11[0] << 24 | var11[1] << 16 | var11[2] << 8 | var11[3];
            }

            for(var17 = 0; var17 < 32; var17 += 4) {
                var20 = var21[1] ^ var21[2] ^ var21[3] ^ var19[var17];
                var20 = var14.b(var20);
                var21[0] ^= c(var20);
                var20 = var21[2] ^ var21[3] ^ var21[0] ^ var19[var17 + 1];
                var20 = var14.b(var20);
                var21[1] ^= c(var20);
                var20 = var21[3] ^ var21[0] ^ var21[1] ^ var19[var17 + 2];
                var20 = var14.b(var20);
                var21[2] ^= c(var20);
                var20 = var21[0] ^ var21[1] ^ var21[2] ^ var19[var17 + 3];
                var20 = var14.b(var20);
                var21[3] ^= c(var20);
            }

            for(var13 = 0; var13 < 16; var13 += 4) {
                var18[var13] = (byte)(var21[3 - var13 / 4] >> 24);
                var18[var13 + 1] = (byte)(var21[3 - var13 / 4] >>> 16);
                var18[var13 + 2] = (byte)(var21[3 - var13 / 4] >>> 8);
                var18[var13 + 3] = (byte)var21[3 - var13 / 4];
            }

            System.arraycopy(var16, 0, var4, var6, 16);
            var2 -= 16;
        }

        return 0;
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.yitong.util.sm;

import cn.com.yitong.util.sm.b;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

public class SM3Digest {
    private char[] h = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private BigInteger i = new BigInteger("7380166f 4914b2b9 172442d7 da8a0600 a96f30bc 163138aa e38dee4d b0fb0e4e".replaceAll(" ", ""), 16);
    private Integer j = Integer.valueOf("79cc4519", 16);
    private Integer k = Integer.valueOf("7a879d8a", 16);
    private byte[] l = new byte[]{(byte)-128};
    private byte[] m = new byte[1];
    private static SM3Digest n = new SM3Digest();

    private SM3Digest() {
    }

    public static SM3Digest getInstance() {
        return n;
    }

    private static int a(int var0, int var1) {
        return var0 << var1 | var0 >>> -var1;
    }

    private static Integer a(Integer var0) throws Exception {
        return Integer.valueOf(var0.intValue() ^ a(var0.intValue(), 15) ^ a(var0.intValue(), 23));
    }

    private byte[] b(byte[] var1) throws Exception {
        if((long)var1.length >= 2305843009213693952L) {
            throw new Exception();
        } else {
            long var2 = (long)(var1.length << 3);
            long var4;
            if((var4 = 448L - (var2 + 1L) % 512L) < 0L) {
                var4 += 512L;
            }

            ByteArrayOutputStream var6 = new ByteArrayOutputStream();

            try {
                var6.write(var1);
                var6.write(this.l);

                for(long var7 = var4 - 7L; var7 > 0L; var7 -= 8L) {
                    var6.write(this.m);
                }

                var6.write(a(var2));
                var1 = var6.toByteArray();
                return var1;
            } finally {
                var6.close();
            }
        }
    }

    private static byte[] a(long var0) {
        byte[] var2 = new byte[8];

        for(int var3 = 0; var3 < 8; ++var3) {
            var2[var3] = (byte)((int)(var0 >>> (7 - var3 << 3)));
        }

        return var2;
    }

    public byte[] hash(byte[] var1) throws Exception {
        var1 = this.b(var1);
        b.b();
        int var2 = var1.length / 64;
        byte[] var4 = this.i.toByteArray();
        byte[] var3 = null;

        for(int var5 = 0; var5 < var2; ++var5) {
            var3 = Arrays.copyOfRange(var1, var5 << 6, var5 + 1 << 6);
            byte[] var6 = var3;
            var4 = var4;
            SM3Digest var23 = this;
            int var7 = this.b(var4, 0);
            int var8 = this.b(var4, 1);
            int var9 = this.b(var4, 2);
            int var10 = this.b(var4, 3);
            int var11 = this.b(var4, 4);
            int var12 = this.b(var4, 5);
            int var13 = this.b(var4, 6);
            int var14 = this.b(var4, 7);
            int[] var15 = new int[68];
            int[] var16 = new int[64];

            int var17;
            for(var17 = 0; var17 < 16; ++var17) {
                var15[var17] = var23.b(var6, var17);
            }

            int var18;
            int var19;
            int var24;
            for(var17 = 16; var17 < 68; ++var17) {
                var15[var17] = a(Integer.valueOf(var15[var17 - 16] ^ var15[var17 - 9] ^ a(var15[var17 - 3], 15))).intValue() ^ a(var15[var17 - 13], 7) ^ var15[var17 - 6];
                var24 = var15[var17 - 3];
                var19 = var15[var17 - 16] ^ var15[var17 - 9];
                var18 = a(var15[var17 - 3], 15);
                int var20;
                int var21 = a(Integer.valueOf(var20 = var19 ^ var18)).intValue();
                int var22 = a(var15[var17 - 13], 7);
                String.format("%d W3:%s,Temp2:%s", new Object[]{Integer.valueOf(var17), Integer.toBinaryString(var24), Integer.toBinaryString(var18)});
                String.format("%d %08x=%08x %08x %08x %08x %08x ", new Object[]{Integer.valueOf(var17), Integer.valueOf(var15[var17]), Integer.valueOf(var19), Integer.valueOf(var18), Integer.valueOf(var20), Integer.valueOf(var21), Integer.valueOf(var22)});
            }

            b.c();

            for(var17 = 0; var17 < 64; ++var17) {
                var16[var17] = var15[var17] ^ var15[var17 + 4];
                if(var17 == 64) {
                    var15[var17] = Integer.parseInt("353034453433", 16);
                }
            }

            b.c();

            for(var18 = 0; var18 < 64; ++var18) {
                int var10000 = a(var7, 12) + var11;
                int var10001;
                if(var18 >= 0 && var18 <= 15) {
                    var10001 = var23.j.intValue();
                } else {
                    if(var18 < 16 || var18 > 63) {
                        throw new Exception();
                    }

                    var10001 = var23.k.intValue();
                }

                var24 = (var17 = a(var10000 + a(var10001, var18), 7)) ^ a(var7, 12);
                Integer var26 = Integer.valueOf(var7);
                Integer var27 = Integer.valueOf(var8);
                Integer var31 = Integer.valueOf(var9);
                Integer var30 = var27;
                Integer var29 = var26;
                if(var18 >= 0 && var18 <= 15) {
                    var26 = Integer.valueOf(var29.intValue() ^ var30.intValue() ^ var31.intValue());
                } else {
                    if(var18 < 16 || var18 > 63) {
                        throw new Exception();
                    }

                    var26 = Integer.valueOf(var29.intValue() & var30.intValue() | var29.intValue() & var31.intValue() | var30.intValue() & var31.intValue());
                }

                var24 = var26.intValue() + var10 + var24 + var16[var18];
                var26 = Integer.valueOf(var11);
                var27 = Integer.valueOf(var12);
                var31 = Integer.valueOf(var13);
                var30 = var27;
                var29 = var26;
                if(var18 >= 0 && var18 <= 15) {
                    var26 = Integer.valueOf(var29.intValue() ^ var30.intValue() ^ var31.intValue());
                } else {
                    if(var18 < 16 || var18 > 63) {
                        throw new Exception();
                    }

                    var26 = Integer.valueOf(var29.intValue() & var30.intValue() | ~var29.intValue() & var31.intValue());
                }

                var17 = var26.intValue() + var14 + var17 + var15[var18];
                var10 = var9;
                var9 = a(var8, 9);
                var8 = var7;
                var7 = var24;
                var14 = var13;
                var13 = a(var12, 19);
                var12 = var11;
                var11 = Integer.valueOf((var30 = Integer.valueOf(var17)).intValue() ^ a(var30.intValue(), 9) ^ a(var30.intValue(), 17)).intValue();
            }

            ByteArrayOutputStream var25;
            (var25 = new ByteArrayOutputStream(32)).write(b.e(var7));
            var25.write(b.e(var8));
            var25.write(b.e(var9));
            var25.write(b.e(var10));
            var25.write(b.e(var11));
            var25.write(b.e(var12));
            var25.write(b.e(var13));
            var25.write(b.e(var14));
            byte[] var28 = var25.toByteArray();

            for(var19 = 0; var19 < var28.length; ++var19) {
                var28[var19] ^= var4[var19];
            }

            var3 = var28;
            var4 = var28;
            b.b();
        }

        return var3;
    }

    private int b(byte[] var1, int var2) {
        StringBuilder var3 = new StringBuilder("");

        for(int var4 = 0; var4 < 4; ++var4) {
            var3.append(this.h[(byte)((var1[(var2 << 2) + var4] & 240) >> 4)]);
            var3.append(this.h[(byte)(var1[(var2 << 2) + var4] & 15)]);
        }

        return Long.valueOf(var3.toString(), 16).intValue();
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.yitong.util.sm;

final class b {
    static {
        char[] var10000 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    }

    static void b() {
    }

    static void c() {
    }

    public static byte[] e(int var0) {
        byte[] var1;
        (var1 = new byte[4])[0] = (byte)(var0 >> 24);
        var1[1] = (byte)((var0 & 16777215) >>> 16);
        var1[2] = (byte)((var0 & '\uffff') >>> 8);
        var1[3] = (byte)var0;
        return var1;
    }
}

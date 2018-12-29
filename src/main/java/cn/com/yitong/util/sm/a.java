//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.yitong.util.sm;

import org.bouncycastle.util.encoders.Hex;

final class a {
    static String a(byte[] var0) {
        return (new String(Hex.encode(var0))).toUpperCase();
    }
}

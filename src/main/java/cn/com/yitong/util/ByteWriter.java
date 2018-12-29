package cn.com.yitong.util;

public class ByteWriter {
	private byte[] buff;

	private int pos = 0;

	public ByteWriter(int size) {
		buff = new byte[size];
	}

	public ByteWriter() {
		buff = new byte[1024];
	}

	public void append(byte[] bytes) {
		checkLength(bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			buff[pos++] = bytes[i];
		}
	}

	public void append(byte b) {
		checkLength(1);
		buff[pos++] = b;
	}

	/**
	 * 
	 * @param len
	 */
	private void checkLength(int len) {
		if (buff.length < (pos + len)) {
			byte[] b = new byte[buff.length + 1024];
			System.arraycopy(buff, 0, b, 0, pos);
			buff = b;
		}
	}

	public void append(String ebcdicCode) {
		append(ebcdicCode.getBytes());
	}

	public byte[] getByte() {
		byte[] result = new byte[pos];
		System.arraycopy(buff, 0, result, 0, pos);
		return result;
	}
}

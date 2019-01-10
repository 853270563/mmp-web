package mmpYt;

import java.io.UnsupportedEncodingException;

import cn.com.yitong.core.util.ThreadContext;

/**
 * @author luanyu
 * @date   2017年9月24日
 */
public class Main {
	public static void main(String[] args) throws UnsupportedEncodingException {

		new Thread(new Runnable() {

			@Override
			public void run() {
				ThreadContext.put("1", "2");
				// TODO Auto-generated method stub
				System.out.println(ThreadContext.get("1") + "parent");
				new Thread(new Runnable() {

					@Override
					public void run() {
						ThreadContext.remove();
						// TODO Auto-generated method stub
						System.out.println(ThreadContext.get("1") + "clild");

					}
				}).start();

			}
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println(ThreadContext.get("1") + "parent..");
			}
		}).start();
	}
}

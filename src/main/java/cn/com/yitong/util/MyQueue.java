package cn.com.yitong.util;

import java.util.LinkedList;

public class MyQueue {
	private LinkedList ll = new LinkedList();
	private static MyQueue queue = new MyQueue();

	public static MyQueue instance() {
		return queue;
	}

	public void put(Object o) {
		this.ll.addLast(o);
	}

	public void log(Object o) {
		// this.ll.addLast(DateUtil.curTimeStr2() + " " + o);
	}

	public Object get() {
		return this.ll.removeFirst();
	}

	public boolean empty() {
		return this.ll.isEmpty();
	}

	public static void main(String[] args) {
		MyQueue mq = new MyQueue();
		mq.put("zhangsan");
		mq.put("lisi");
		mq.put("wangwu");

		System.out.println(mq.get());
		System.out.println(mq.get());
		System.out.println(mq.get());
		System.out.println(mq.empty());
	}
}
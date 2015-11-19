package util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class test {
	static RingBuffer<Integer> r = new RingBuffer<>( 1001);
	static HashSet<Integer> removed = new HashSet<>();
	
	synchronized public static void testing() {
			TestAddThread[] threads = new TestAddThread[10];
			int j = 0;
			for (int i = 0; i < 1000; i += 100) {
				threads[j++] = new TestAddThread(r, i, i+100);
			}
			for (int i = 0; i < 10; ++i) {
				threads[i].start();
			}
			
	}
	
	public static void main(String[] args) throws InterruptedException {
//		testing();
//		Thread.sleep(1000);
//		validating();
//		testing2();
//		Thread.sleep(1000);
//		check();
		testing3();
	}
	
	synchronized static void check() {
		if(r.size() != 0)
			System.out.println("FalSe");
	}
	
	synchronized public static void validating() {
		boolean[] test = new boolean[1001];
		for(int i = 0; i < 1000; i++) {
			int temp = (int) (r.data[i]);
			if(test[temp] == true)
				System.out.println("False");
			test[temp] = true;
		}
		System.out.println("True");
	}
	
	
	static class TestAddThread extends Thread {
		final RingBuffer<Integer> r;
		int start;
		int end;
		TestAddThread(RingBuffer<Integer> r, int start, int end) {
			this.r = r;
			this.start = start;
			this.end = end;
		}
		public void run() {
			for (int i = start; i < end; i++) {
				r.add(i);
			}
		}
	}
	
	static class RemoveThread extends Thread {
		final RingBuffer<Integer> r;
		int start;
		int end;
		RemoveThread(RingBuffer<Integer> r, int start, int end) {
			this.r = r;
			this.start = start;
			this.end = end;
		}
		public void run() {
			for (int i = start; i < end; i++) {
				try {
					int temp = r.take();
					if(removed.contains(temp))
						System.out.println("FALSE");
					removed.add(temp);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	
	
	class TestSizeThread extends Thread {
		final RingBuffer<Integer> r;
		TestSizeThread(RingBuffer<Integer> r) {
			this.r = r;
		}
		public void run() {
			System.out.println("Size: " + r.size());
		}
	}
	class TestIsEmptyThread extends Thread {
		final RingBuffer<Integer> r;
		TestIsEmptyThread(RingBuffer<Integer> r) {
			this.r = r;
		}
		public void run() {
			System.out.println("Is Empty: " + r.isEmpty());
		}
	}
	class TestPeekThread extends Thread {
		final RingBuffer<Integer> r;
		TestPeekThread(RingBuffer<Integer> r) {
			this.r = r;
		}
		public void run() {
			System.out.println("Peek: " + r.peek());
		}
	}
	class TestPutThread extends Thread {
		final RingBuffer<Integer> r;
		TestPutThread(RingBuffer<Integer> r) {
			this.r = r;
		}
		public void run() {
			try {
				r.put(util.RandomGen.randomNumber());
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("peek throw ");
			}
		}
	}
	synchronized public static void testing2() {
		TestAddThread[] threads = new TestAddThread[10];
		RemoveThread[] rthreads = new RemoveThread[10];
		int j = 0;
		for (int i = 0; i < 1000; i += 100) {
			threads[j] = new TestAddThread(r, i, i+100);
			rthreads[j++] = new RemoveThread(r,i,i+100);
		}
		for (int i = 0; i < 10; ++i) {
			threads[i].start();
			rthreads[i].start();
		}
	}
	
	public static void  testing3() throws InterruptedException {
		RingBuffer<Integer> rb = new RingBuffer<>(3);
//		rb.poll();
		System.out.println(rb.peek());//--------------------

		rb.add(1);
		System.out.println(rb.peek());//--------------------
		if(!rb.contains(1))
			System.out.println("testing3 fail");
		rb.add(2);
		System.out.println(rb.peek());//--------------------
		if(rb.add(2))
			System.out.println("testing3 fail");
		rb.add(3);
		if(rb.size() != 3)
			System.out.println("testing3 fail");
		if(rb.take() != 1)
			System.out.println("testing3 fail");
		if(rb.contains(1))
			System.out.println("testing3 fail");
		rb.add(4);
		Iterator<Integer> i = rb.iterator();
		System.out.println("start iterating");
		while(i.hasNext())
			System.out.println(i.next());
		System.out.println("end iterating");
		System.out.println(rb.peek());//--------------------
		
//		rb.add(5);
	}
	
	public void randomTest() {
		RingBuffer<Integer> rb = new RingBuffer<>(100);
		int count = 0;
		HashSet<Integer> set = new HashSet<>();
		while(count < 100) {
			
		}
	}
}

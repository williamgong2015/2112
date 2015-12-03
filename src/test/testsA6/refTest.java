package test.testsA6;

import java.util.ArrayList;
import java.util.HashSet;

public class refTest {
	static ArrayList<Integer> r = new ArrayList<>();
	static HashSet<Integer> removed = new HashSet<>();
	
	class TestRun implements Runnable{

		@Override
		public void run() {
			
		}
		
	}
	
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
		testing();
		Thread.sleep(1000);
		validating();
//		testing2();
//		Thread.sleep(1000);
//		check();
	}
	
	synchronized static void check() {
		if(r.size() != 0)
			System.out.println("False");
	}
	
	synchronized public static void validating() {
		boolean[] test = new boolean[1001];
		for(int i = 0; i < 1000; i++) {
			int temp = r.get(i);
			if(test[temp] == true)
				System.out.println("False");
			test[temp] = true;
		}
		System.out.println("True");
	}
	
	static class TestAddThread extends Thread {
		final ArrayList<Integer> r;
		int start;
		int end;
		TestAddThread(ArrayList<Integer> r, int start, int end) {
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
		final ArrayList<Integer> r;
		int start;
		int end;
		RemoveThread(ArrayList<Integer> r, int start, int end) {
			this.r = r;
			this.start = start;
			this.end = end;
		}
		public void run() {
			for (int i = start; i < end; i++) {
				int temp = r.remove(0);
				if(removed.contains(temp))
					System.out.println("FALSE");
				removed.add(temp);
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
	
	class TestSizeThread extends Thread {
		final ArrayList<Integer> r;
		TestSizeThread(ArrayList<Integer> r) {
			this.r = r;
		}
		public void run() {
			System.out.println("Size: " + r.size());
		}
	}
	class TestIsEmptyThread extends Thread {
		final ArrayList<Integer> r;
		TestIsEmptyThread(ArrayList<Integer> r) {
			this.r = r;
		}
		public void run() {
			System.out.println("Is Empty: " + r.isEmpty());
		}
	}
	

}

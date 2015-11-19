package util;

import util.Lock;

/**
 * Ref: CS2112 Lab11
 *
 */

public class RingBufferLock {
	
	int reading = 0;           // how many different readers are reading it
	int remove_held_count = 0; // number of times held by current remover
	int writer_held_count = 0; // number of times held by current writer
	Thread writer = null;      // null if not locked by a writer
	Thread remover = null;     // null if not locked by a remover
	Lock rmLock = new RemoveLock();
	Lock wrLock = new WriteLock();
	Lock rdLock = new ReadLock();
	int head = 0;
	int tail = 0;
	final int size;
	
	public RingBufferLock(int capacity) {
		size = capacity;
	}

	public Lock removeLock() {return rmLock; }
	public Lock writeLock() {return wrLock; }
	public Lock readingLock() {return rdLock; }

	class ReadLock implements Lock{

		@Override
		public void lock() {
			synchronized (RingBufferLock.this) {
				// can't read while someone is removing or writing it
				while (remover != null || writer != null) { 
					try {
						RingBufferLock.this.wait();
					} catch (Exception e) {} }
				// one more reader
				reading++;
			}
			
		}

		@Override
		public void unlock() {
			synchronized (RingBufferLock.this) {
				reading--;
				RingBufferLock.this.notifyAll();
			}
		}
		
	}
	
	class RemoveLock implements Lock {
		public void lock() {
			Thread me = Thread.currentThread();
			synchronized (RingBufferLock.this) {
				// already holding the lock
				if (remover == me) { remove_held_count++; return; } 
				// can't remove while someone is reading 
				while (reading != 0 || remover != null || tail == head ) { 
					try {
						RingBufferLock.this.wait();
					} catch (Exception e) {}
				}
				remover = me;
				remove_held_count = 1;
			}	
		}

		@Override
		public void unlock() {
			synchronized (RingBufferLock.this) {
				remove_held_count--;
				if (remove_held_count > 0) return; // still holding it!
				remover = null;
				RingBufferLock.this.notifyAll();
				head = (head+1)%size;
			}
		}
	}

	public class WriteLock implements Lock {
		public void lock() {
			Thread me = Thread.currentThread();
			synchronized (RingBufferLock.this) {
				if (writer == me) { writer_held_count++; return; } // already holding the lock

				while (writer != null || reading != 0 || (tail + size - 1)%size == head) { 
					try {
						RingBufferLock.this.wait();
					} catch (Exception e) {}
				}
				writer = me;
				writer_held_count = 1;
			}
		}
		public void unlock() {
			synchronized (RingBufferLock.this) {
				writer_held_count--;
				if (writer_held_count > 0) return; // still holding it!
				writer = null;
				RingBufferLock.this.notifyAll();
				tail = (tail + size - 1)%size;
			}
		}
		
		
	}
	
	public int getTail() {
		synchronized(this) {
			while(writer != null) {
				try {
					RingBufferLock.this.wait();
				} catch (Exception e) {}
			}
			return tail;
		}
	}
	
	public int getHead() {
		synchronized(this) {
			while(remover != null) {
				try {
					RingBufferLock.this.wait();
				} catch (Exception e) {}
			}
			return head;
		}
	}
	
}

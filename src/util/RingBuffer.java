package util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Generic RingBuffer implements {@code Collection}, {@code Queue} and 
 * {@code BlockingQueue} using RingBufferLock
 *
 */
public class RingBuffer<E> implements Collection<E>, Queue<E>, BlockingQueue<E>{
	
	public E[] data;
	private int head = 0;
	private int tail = 0;
	private int modCount;
	private int size;
	private RingBufferLock lock = new RingBufferLock();
	
	/**
	 * Inner lock class for RingBuffer to enforce isolation and enhance 
	 * concurrency
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
		
		/**
		 * Lock required for reading from the {@code RingBuffer}
		 * Can have multiple concurrent readers, 
		 * but can't read together with writer and remover
		 */
		class ReadLock implements Lock{

			@Override
			public void lock() {
				synchronized (RingBufferLock.this) {
					// can't read while someone is removing or writing it
					while (remover != null || ( writer != null && head == tail)) { 
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
		
		/**
		 * Lock required for removing from the {@code RingBuffer}
		 * Can
		 *
		 */
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
					head = (head+1)%size;
					RingBufferLock.this.notifyAll();
				}
			}
		}

		public class WriteLock implements Lock {
			public void lock() {
				Thread me = Thread.currentThread();
				synchronized (RingBufferLock.this) {
					if (writer == me) { writer_held_count++; return; } // already holding the lock

					while (writer != null || reading != 0 || (tail + size + 1)%size == head) { 
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
					tail = (tail + 1)%size;
					RingBufferLock.this.notifyAll();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public RingBuffer(Class<E> clazz, int size) {
		modCount = 0;
		if(size < 0)
			throw new IllegalArgumentException("Illegal Capacity: "+size);
		data = (E[]) Array.newInstance(clazz, size);
		this.size = size;
	}
	
	/**
	 * Returns the number of elements in this collection. 
	 * Override from {@code Collection}
	 * @return number of the elements in this collection
	 */
	@Override
	public int size() {
		lock.rdLock.lock();
		int result = (tail - head) >= 0 ?  (tail - head) : (tail - head + size);
		lock.rdLock.unlock();
		return result;
	}

	/**
	 * Returns true if this collection contains no elements.
	 * Returns false otherwise
	 * Override from {@code Collection}
	 */
	@Override
	public boolean isEmpty() {
		lock.rdLock.lock();
		boolean result = head == tail;
		lock.rdLock.unlock();
		return result;
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub   1
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub  1
		return null;
	}

	@Override
	public boolean add(E e) {
		if(this.size() == size)
			throw new IllegalStateException();
		lock.wrLock.lock();
		data[tail] = e;
		lock.wrLock.unlock();
		return true;
	}

	@Override
	public boolean offer(E e) {
		// TODO Auto-generated method stub 1
		return false;
	}

	@Override
	public E remove() {
		// TODO Auto-generated method stub 1
		return null;
	}

	@Override
	public E poll() {
		// TODO Auto-generated method stub 1
		return null;
	}

	@Override
	public E element() {
		// TODO Auto-generated method stub 1
		return null;
	}

	/**
	 * Retrieves, but does not remove, the head of this queue, 
	 * or returns null if this queue is empty.
	 * Override from {@code Queue}
	 */
	@Override
	public E peek() {
		lock.rdLock.lock();
		if(this.size == 0) {
			lock.rdLock.unlock();
			return null;
		}
		E temp = data[head];
		lock.rdLock.unlock();
		return temp;
	}

	/**
	 * Inserts the specified element into this queue, 
	 * waiting if necessary for space to become available.
	 * @param e
	 * @throws InterruptedException
	 * Override from {@code BlockingQueue}
	 */
	@Override
	public void put(E e) throws InterruptedException {
		lock.wrLock.lock();
		data[tail] = e;
		lock.wrLock.unlock();
		return;
	}

	@Override
	public E take() throws InterruptedException {
		lock.rmLock.lock();
		E temp = data[head];
		lock.rmLock.unlock();
		return temp;
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int remainingCapacity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
}

package util;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Generic RingBuffer implements {@code Collection}, {@code Queue} and 
 * {@code BlockingQueue} using RingBufferLock
 *
 */
public class RingBuffer<E> implements Collection<E>, Queue<E>, BlockingQueue<E>{
	
	public Object[] data;
	private int head = 0;
	private int tail = 0;
	private volatile int modCount = 0;
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
					while (remover != null ||  writer != null) { 
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
					head = (head + 1) % size;
					RingBufferLock.this.notifyAll();
				}
			}
		}

		public class WriteLock implements Lock {
			public void lock() {
				Thread me = Thread.currentThread();
				synchronized (RingBufferLock.this) {
					if (writer == me) 
						{ writer_held_count++; return; } // already holding the lock

					while (writer != null || reading != 0 
							|| (tail + size + 1) % size == head) { 
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

	public RingBuffer(int size) {
		modCount = 0;
		if(size < 0)
			throw new IllegalArgumentException("Illegal Capacity: "+size);
		this.size = size + 1;
		data = new Object[size + 1];
	}
	
	/**
	 * Returns the number of elements in this collection. 
	 * Override from {@code Collection}
	 * @return number of the elements in this collection
	 */
	@Override
	public int size() {
		lock.rdLock.lock();
		try{
			int result = (tail - head) >= 0 ?  (tail - head) : (tail - head + size);
			return result;
		}
		finally{
			lock.rdLock.unlock();
		}
	}

	/**
	 * Returns true if this collection contains no elements.
	 * Returns false otherwise
	 * Override from {@code Collection}
	 */
	@Override
	public boolean isEmpty() {
		lock.rdLock.lock();
		try{
			return head == tail;
		}
		finally{
			lock.rdLock.unlock();
		}
	}

	/**
	 * 
	 * @param o
	 * @return true if the object{@code o} is in the ringbuffer
	 * Override from {@code Collection}
	 */
	@Override
	public boolean contains(Object o) {
		lock.rdLock.lock();
		try{
			int end;
			if(tail >= head)
				end = tail;
			else
				end = tail + size;
			for(int i = head; i < end; i++) {
				if(data[(i) % size].equals(o))
					return true;
			}
			return false;
		}
		finally {
			lock.rdLock.unlock();
		}
	}

	/**
	 * Returns an iterator over the elements in this collection
	 */
	@Override
	public Iterator<E> iterator() {
		return new Itr<E>();
	}
	
	/**
	 * implementation of the iterator,throw
	 * {@code ConcurrentModificationException} if the
	 * ring buffer is modified when iterating
	 */
	@SuppressWarnings("hiding")
	private class Itr<E> implements Iterator<E> {

		private int expectedModCount;
		private int cur;
		
		Itr() {
			expectedModCount = modCount;
			cur = head;
		}
		
		/**
		 * @return true if there is one more element in the ringbuffer
		 */
		@Override
		public boolean hasNext() {
			if(expectedModCount != modCount)
				throw new ConcurrentModificationException();
			return cur != (tail + size) % size;
		}

		/**
		 * Returns the next element in the iteration
		 */
		@SuppressWarnings("unchecked")
		@Override
		public E next() {
			if(expectedModCount != modCount)
				throw new ConcurrentModificationException();
			E res =  (E)data[cur++];
			cur %= size;
			return res;
		}
		
		@Override
		public void remove() {
			if(expectedModCount != modCount)
				throw new ConcurrentModificationException();
			RingBuffer.this.remove();
		}
	}

	/**
	 * if the ringbuffer is full, throw {@code IllegalStateException}
	 * if the element {@code e} is already in the ringbuffer return false
	 * otherwise return true
	 */
	@Override
	public boolean add(E e) {
		if(size() == size - 1)
			throw new IllegalStateException();
		lock.wrLock.lock();
		try{
			modCount++;
			data[tail] = e;
			return true;
		}
		finally{
			lock.wrLock.unlock();
		}
	}

	/**
	 * insert an element to the ringbuffer
	 * return false if the ringbuffer is full
	 */
	@Override
	public boolean offer(E e) {
		if(size() == size - 1)
			return false;
		lock.wrLock.lock();
		try {
			modCount++;
			data[tail] = e;
			return true;
		}
		finally{
			lock.wrLock.unlock();
		}
	}

	/**
	 * Retrieves and removes the head of this queue. 
	 * This method differs from poll only in that it throws 
	 * an exception{@code NullPointerException} if this queue is empty
	 * Override from {@code Queue}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public E remove() throws NullPointerException{
		if(size() == 0)
			throw new NullPointerException();
		lock.rmLock.lock();
		try{
			E temp = (E)data[head];
			modCount++;
			return temp;
		}
		finally {
			lock.rmLock.unlock();
		}
	}

	/**
	 * Retrieves and removes the head of this queue,
	 *  or returns null if this queue is empty
	 *  Override from {@code Queue}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E poll() {
		if(size() == 0)
			return null;
		lock.rmLock.lock();
		try{
			modCount++;
			Object result = data[head];
			return (E)result;
		}
		finally{
			lock.rmLock.unlock();
		}
	}

	
	/**
	 * Retrieves, but does not remove, the head of this queue
	 * if the ringbuffer is empty, throw {@code NoSuchElementException}
	 * Override from {@code Queue}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E element() {
		if(size() == 0)
			throw new NoSuchElementException();
		lock.rdLock.lock();
		try{
			Object temp = data[head];
			return (E)temp;
		}
		finally{
			lock.rdLock.unlock();
		}
	}

	/**
	 * Retrieves, but does not remove, the head of this queue, 
	 * or returns null if this queue is empty.
	 * Override from {@code Queue}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E peek() {
		if(size() == 0)
			return null;
		lock.rdLock.lock();
		try{
			Object temp = data[head];
			return (E)temp;
		}
		finally{
			lock.rdLock.unlock();
		}
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
		try{
			modCount++;
			data[tail] = e;
			return;
		}
		finally{
			lock.wrLock.unlock();
		}
	}

	/**
	 * Retrieves and removes the head of this queue, 
	 * waiting if necessary until an element becomes available
	 * @throws InterruptedException
	 * Override from {@code BlockingQueue}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E take() throws InterruptedException {
		lock.rmLock.lock();
		try{
			modCount++;
			Object temp = data[head];
			return (E)temp;
		}
		finally{
			lock.rmLock.unlock();
		}
	}

	/**
	 * return true if the Object{@code o} is also
	 * a ringbuffer and everything in o is equal to
	 * that in this and in same order
	 */
	public boolean equals(Object o) {
		if(!(o instanceof RingBuffer))
			return false;
		RingBuffer r = (RingBuffer)o;
		r.lock.rdLock.lock();
		lock.rdLock.lock();
		try {
			Iterator<E> a = r.iterator();
			Iterator<E> b = iterator();
			while(a.hasNext() && b.hasNext()) {
				if(a.next().equals(b.next()))
					continue;
				return false;
			}
			if(a.hasNext() || b.hasNext())
				return false;
			return true;
		}finally {
			r.lock.rdLock.unlock();
			lock.rdLock.unlock();
		}
	}
	
	@Override
	public E poll(long timeout, TimeUnit unit)
			throws InterruptedException {
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
	public boolean offer(E e, long timeout, TimeUnit unit) 
			throws InterruptedException {
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

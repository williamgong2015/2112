package util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class RingBuffer<E> implements Collection<E>, Queue<E>, BlockingQueue<E>{
	
	private E[] data;
	private int head;
	private int tail;
	
	@SuppressWarnings("unchecked")
	public RingBuffer(int size) {
		head = 0;
		tail = 0;
		data = (E[])(new Object[size]);
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub  1
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub  1
		return false;
	}

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
		// TODO Auto-generated method stub  1
		return false;
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

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(E e) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public E take() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
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

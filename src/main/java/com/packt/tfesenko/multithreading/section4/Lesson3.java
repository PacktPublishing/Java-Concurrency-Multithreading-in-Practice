package com.packt.tfesenko.multithreading.section4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Lesson3 {

	public static void main(String[] args) throws InterruptedException {
		demoLocks();
	}

	public static void demoLocks() throws InterruptedException {
		int numberOfWorkers = 2;
		ExecutorService service = Executors.newFixedThreadPool(numberOfWorkers);

		Counter counter = new Counter();
		for (int i = 0; i < 10_000; i++) {
			service.submit(() -> counter.increment());
		}

		service.awaitTermination(1000, TimeUnit.MILLISECONDS);

		System.out.println("Increment 10_000 times: " + counter.getValue());
	}

	public static class Counter {
		// Try this:
		// private final Lock lock = new ReentrantLock();

		private final ReadWriteLock lock = new ReentrantReadWriteLock();
		private final Lock readLock = lock.readLock();
		private final Lock writeLock = lock.writeLock();

		private int value = 0;

		public void increment() {
			writeLock.lock();
			try {
				value++;
			} finally {
				// make sure to unlock!!!!!
				writeLock.unlock();
			}
		}

		public void decrement() {
			writeLock.lock();
			try {
				value--;
			} finally {
				// make sure to unlock!!!!!
				writeLock.unlock();
			}
		}

		/**
		 * tryLock()
		 */
		public void tryIncrement() {
			if (writeLock.tryLock()) {
				try {
					value++;
				} finally {
					// make sure to unlock!!!!!
					writeLock.unlock();
				}
			} else {
				System.out.println("Whatever... I tried");
			}
		}

		public int getValue() {
			readLock.lock();
			try {
				return value;
			} finally {
				readLock.unlock();
			}
		}

		public String getBinaryValue() {
			readLock.lock();
			try {
				return Integer.toBinaryString(value);
			} finally {
				readLock.unlock();
			}
		}

	}
	

}

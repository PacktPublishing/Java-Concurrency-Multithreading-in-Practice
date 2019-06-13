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
		private final Object lock = new Object();

		private int value = 0;

		public void increment() {
			synchronized (lock) {
				value++;
			}
		}

		public void decrement() {
			synchronized (lock) {
				value--;
			}
		}

		public int getValue() {
			synchronized (lock) {
				return value;
			}
		}

	}
}

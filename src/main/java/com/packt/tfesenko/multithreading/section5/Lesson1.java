package com.packt.tfesenko.multithreading.section5;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.packt.tfesenko.multithreading.section2.Lesson2;
import com.packt.tfesenko.multithreading.section4.Lesson4;

public class Lesson1 {

	public static void main(String[] args) throws InterruptedException {
		demoAtomicInteger();

		// Atomic boolean in section 4
		// Lesson4.demoWaitForGreenLight();

	}

	// Modification of
	// com.packt.tfesenko.multithreading.section4.Lesson2.demoSynchronized()
	public static void demoAtomicInteger() throws InterruptedException {
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
		private int value = 0;

		public synchronized void increment() {
			value++;
		}

		public synchronized void decrement() {
			value--;
		}

		public synchronized int getValue() {
			return value;
		}
	}

}

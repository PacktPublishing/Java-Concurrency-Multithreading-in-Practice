package com.packt.tfesenko.multithreading.section4;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Lesson4 {

	public static void main(String[] args) throws InterruptedException {
		demoWaitForGreenLight();
		demoWaitNotifyWithMessageQueue();
	}

	private static void demoWaitNotifyWithMessageQueue() throws InterruptedException {
		ExecutorService service = Executors.newFixedThreadPool(2);
	
		BrokenMessageQueue messageQueue = new BrokenMessageQueue();

		Runnable producer = () -> {
			// American journalist Robert Benchley sent this message from Venice
			// “Streets full of water. Please advise.”
			String[] messages = { "Streets", " full of water.", " Please", "advise." };
			for (String message : messages) {
				System.out.format("%s sending >> %s%n", Thread.currentThread().getName(), message);
				messageQueue.send(message);
				try {
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException e) {
				}
			}
		};

		Runnable consumer = () -> {
			for (int i = 0; i < 4; i++) {
				String message = messageQueue.receive();
				System.out.format("%s received << %s%n", Thread.currentThread().getName(), message);
				try {
					TimeUnit.MILLISECONDS.sleep(0);
				} catch (InterruptedException e) {
				}
			}
		};

		service.submit(producer);
		service.submit(consumer);

		service.awaitTermination(5000, TimeUnit.MILLISECONDS);

	}

	private static class BrokenMessageQueue {

		private final int capacity = 2;

		private final Queue<String> queue = new LinkedList<>();

		// not synchronized
		public void send(String message) {
			while (queue.size() == capacity) {
				// wait until queue is able to accept new elements, not full
			}
			queue.add(message);
			// A new element added to the queue!!!
		}

		public String receive() {
			while (queue.size() == 0) {
				// wait until queue has elements, not empty
			}
			String value = queue.poll();
			// An element removed from a queue!!!
			return value;
		}

	}

	
	public static void demoWaitForGreenLight() throws InterruptedException {
		final AtomicBoolean isGreenLight = new AtomicBoolean(false);

		Runnable waitForGreenLightAndGo = () -> {
			System.out.println("Waiting for the green light...");
			while (!isGreenLight.get()) {
				// just spin-wait
			}
			System.out.println("Go!!!");
		};
		new Thread(waitForGreenLightAndGo).start();

		TimeUnit.MILLISECONDS.sleep(500);

		// from the main thread:
		isGreenLight.set(true);
	}

}

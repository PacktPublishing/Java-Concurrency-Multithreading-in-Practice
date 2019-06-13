package com.packt.tfesenko.multithreading.section5;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class Lesson3 {

	public static void main(String[] args) throws InterruptedException {
		demoBlockingQueue();
		//demoMessageQueue();
	}

	// Modification of
	// com.packt.tfesenko.multithreading.section5.Lesson2.demoConcurrentLinkedQueue()
	private static void demoBlockingQueue() throws InterruptedException {
		Random random = new Random();
		ExecutorService service = Executors.newCachedThreadPool();

		Queue<String> queue = new ConcurrentLinkedQueue<>();
		Consumer<String> joinQueue = (name) -> {
			try {
				TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
				queue.offer(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		service.submit(() -> joinQueue.accept("Alice"));
		service.submit(() -> joinQueue.accept("Bob"));
		service.submit(() -> joinQueue.accept("Carol"));
		service.submit(() -> joinQueue.accept("Daniel"));

		Runnable dequeue = () -> {
			try {
				System.out.println("poll(): " + queue.poll());
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		service.submit(dequeue);
		service.submit(dequeue);
		service.submit(dequeue);
		service.submit(dequeue);

		service.awaitTermination(2000, TimeUnit.MILLISECONDS);

		System.out.println("\n    Remaing elements: ");
		queue.forEach((name) -> System.out.println(name));

	}

	// Modified
	// com.packt.tfesenko.multithreading.section4.Lesson4.demoWaitNotifyWithMessageQueue()
	private static void demoMessageQueue() throws InterruptedException {
		ExecutorService service = Executors.newFixedThreadPool(2);
		MessageQueue messageQueue = new MessageQueue();

		Runnable producer = () -> {
			// American journalist Robert Benchley sent this message from Venice
			// “Streets full of water. Please advise.”
			String[] messages = { "Streets", " full of water.", " Please", "advise." };
			for (String message : messages) {
				System.out.format("%s sending >> %s%n", Thread.currentThread().getName(), message);
				messageQueue.send(message);
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		};

		Runnable consumer = () -> {
			for (int i = 0; i < 4; i++) {
				try {
					String message = messageQueue.receive();
					System.out.format("%s received << %s%n", Thread.currentThread().getName(),
							message);
					TimeUnit.MILLISECONDS.sleep(0);
				} catch (InterruptedException e) {
				}
			}
		};

		service.submit(producer);
		service.submit(consumer);

		service.awaitTermination(5000, TimeUnit.MILLISECONDS);

	}

	private static class MessageQueue {

		private final int capacity = 2;

		private final Queue<String> queue = new LinkedList<>();

		public synchronized void send(String message) {
			while (queue.size() == capacity) {
				// wait until queue is not full
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
			queue.add(message);
			notifyAll();
		}

		public synchronized String receive() {
			while (queue.size() == 0) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
			String value = queue.poll();
			notifyAll();
			return value;
		}

	}

}

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
		// demoBlockingQueue();
		demoMessageQueue();
	}

	// Modification of
	// com.packt.tfesenko.multithreading.section5.Lesson2.demoConcurrentLinkedQueue()
	private static void demoBlockingQueue() throws InterruptedException {
		Random random = new Random();
		ExecutorService service = Executors.newCachedThreadPool();

		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
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
				System.out.println("poll(): " + queue.take());
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

	// MessageQueue implemented with BlockingQueue
	private static class MessageQueue {

		private final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(2);

		public void send(String message) {
			queue.add(message);
		}

		public String receive() throws InterruptedException {
			String value = queue.take();
			return value;
		}

	}
}

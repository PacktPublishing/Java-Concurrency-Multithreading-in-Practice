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
		// Try MessageQueueWithWaitNotify messageQueue = new MessageQueueWithWaitNotify();
		
		// Try MessageQueueWithLockConditions messageQueue = new MessageQueueWithLockConditions();

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

	private static class MessageQueueWithWaitNotify {

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

	private static class MessageQueueWithLockConditions {

		private final int capacity = 2;

		private final Lock lock = new ReentrantLock();

		/** true if ready to send/produce */
		private final Condition queueNotFull = lock.newCondition();

		/** true if ready to receive/consume */
		private final Condition queueNotEmpty = lock.newCondition();

		private final Queue<String> queue = new LinkedList<>();

		public void send(String message) {
			lock.lock();
			try {
				while (queue.size() == capacity) {
					try {
						queueNotFull.await();
					} catch (InterruptedException e) {
					}
				}
				queue.add(message);
				queueNotEmpty.signalAll();
			} finally {
				lock.unlock();
			}
		}

		public String receive() {
			lock.lock();
			try {
				while (queue.size() == 0) {
					try {
						queueNotEmpty.await();
					} catch (InterruptedException e) {
					}
				}
				String value = queue.poll();
				queueNotFull.signalAll();
				return value;
			} finally {
				lock.unlock();
			}
		}

	}
	
	private static void demoWaitForGreenLight() throws InterruptedException {
		demoOnSpinWait();
		demoWaitNotify();
	}

	private static void demoOnSpinWait() throws InterruptedException {
		final AtomicBoolean isGreenLight = new AtomicBoolean(false);

		Runnable waitForGreenLightAndGo = () -> {
			System.out.println("Waiting for the green light...");
			while (!isGreenLight.get()) {
				Thread.onSpinWait();
			}
			System.out.println("Go!!!");
		};
		new Thread(waitForGreenLightAndGo).start();

		TimeUnit.MILLISECONDS.sleep(3000);

		// from the main thread:
		isGreenLight.set(true);
	}

	public static void demoWaitNotify() throws InterruptedException {
		final AtomicBoolean isGreenLight = new AtomicBoolean(false);

		Object lock = new Object();

		Runnable waitForGreenLightAndGo = () -> {
			System.out.println("Waiting for the green light...");
			synchronized (lock) {
				while (!isGreenLight.get()) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			System.out.println("Go!!!");
		};
		new Thread(waitForGreenLightAndGo).start();

		TimeUnit.MILLISECONDS.sleep(3000);

		// from the main thread:
		synchronized (lock) {
			isGreenLight.set(true);
			lock.notify();
		}
	}

}

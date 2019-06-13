package com.packt.tfesenko.multithreading.section4;

public class Lesson1 {

	public static void main(String[] args) throws InterruptedException {
		demoThreadState();
	}

	private static void demoThreadState() throws InterruptedException {
		System.out.println("Main thread: " + Thread.currentThread().getState());
		System.out.println();

		Runnable sayHello = () -> {
			System.out.println("     Hi there!");
		};
		Thread thread = new Thread(sayHello);

		// nothing happens until the thread starts
		System.out.println("After creation: " + thread.getState());

		thread.start();
		System.out.println("After thread.start(): " + thread.getState());

		// Wait until the second thread completes its execution either by sleeping or by
		// joining
		thread.join();
		// or
        try {
			Thread.sleep(500, 0); // == TimeUnit.MILLISECONDS.sleep(1000)
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
		System.out.println("When completed execution: " + thread.getState());
	}

}

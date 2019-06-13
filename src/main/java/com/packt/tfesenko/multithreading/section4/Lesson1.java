package com.packt.tfesenko.multithreading.section4;

public class Lesson1 {

	public static void main(String[] args) throws InterruptedException {
		demoThreadState();
	}

	private static void demoThreadState() throws InterruptedException {
		System.out.println("Main thread: ");
		System.out.println();

		Runnable sayHello = () -> {
			System.out.println("     Hi there!");
		};
		Thread thread = new Thread(sayHello);

		System.out.println("After creation: ");

		System.out.println("After thread.start(): ");
        
		System.out.println("When completed execution: ");
	}

}

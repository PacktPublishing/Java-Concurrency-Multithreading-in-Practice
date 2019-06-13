package com.packt.tfesenko.multithreading.section2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Lesson1 {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		demoFutureWithCallable();
	}

	public static void demoFutureWithCallable() throws InterruptedException, ExecutionException {
		System.out.println();
		System.out.println("Demo Future with Callable");
		
		ExecutorService pool = Executors.newCachedThreadPool();
		
			
		// These tasks are done by the restaurant
		System.out.println("   Restaurant> Slicing tomatoes");
		System.out.println("   Restaurant> Chopping onions");
		System.out.println("   Restaurant> Spreading with tomato sauce and sprinkle with toppings");
		System.out.println("   Restaurant> Baking pizza");
		TimeUnit.MILLISECONDS.sleep(300);
		Pizza pizza = new Pizza();

		// These tasks are done by me
		System.out.println("Me: Call my brother");
		TimeUnit.MILLISECONDS.sleep(200);
		System.out.println("Me: Walk the dog");

		System.out.println("Me: Eat the pizza: " + pizza);
		
		pool.shutdown();
		System.out.println();
		System.out.println();
	}


	public static class Pizza {

		@Override
		public String toString() {
			return "Classic margherita";
		}

	}

}
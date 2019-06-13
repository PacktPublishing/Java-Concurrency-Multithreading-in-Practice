package com.packt.tfesenko.multithreading.section2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Lesson3 {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// executor is not provided, CompletableFuture.supplyAsync will use ForkJoinPool
		// by default
		ExecutorService executor = Executors.newCachedThreadPool();

		Future<String> cookPizza = executor.submit(() -> {
			// prep ingredients
			try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
			}
			System.out.println("   Restaurant> Slicing tomatoes");
			System.out.println("   Restaurant> Chopping onions");
			String toppings = "Onions Tomatoes";

			// prep pizza
			System.out.println("   Restaurant> Spreading with tomato sauce and sprinkle with toppings: " + toppings);
			String rawPizza = "Raw pizza with " + toppings;

			// bake pizza
			System.out.println("   Restaurant> Baking pizza: " + rawPizza);
			try {
				TimeUnit.MILLISECONDS.sleep(300);
			} catch (InterruptedException e) {
			}
			return "Pizza";
		});

		System.out.println(cookPizza.get());
		
		executor.shutdown();
	}

}

package com.packt.tfesenko.multithreading.section2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Lesson1 {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		demoFutureWithCallable();
		demoCallableVsRunnable();
	}

	public static void demoFutureWithCallable() throws InterruptedException, ExecutionException {
		System.out.println();
		System.out.println("Demo Future with Callable");
		ExecutorService pool = Executors.newCachedThreadPool();

		Future<Pizza> pizzaPickupOrder = pool.submit(() -> {
			System.out.println("   Restaurant> Slicing tomatoes");
			System.out.println("   Restaurant> Chopping onions");
			System.out.println("   Restaurant> Spreading with tomato sauce and sprinkle with toppings");
			System.out.println("   Restaurant> Baking pizza");
			TimeUnit.MILLISECONDS.sleep(300);
			return new Pizza();
		});

		System.out.println("Me: Call my brother");
		TimeUnit.MILLISECONDS.sleep(200);
		System.out.println("Me: Walk the dog");

		// Try this: pizzaPickupOrder.cancel(true);
		if (pizzaPickupOrder.isCancelled()) {
			System.out.println("Me: pizza is cancelled, order something else");
			System.out.println("pizzaPickupOrder.isDone(): " + pizzaPickupOrder.isDone());
		} else if (!pizzaPickupOrder.isDone()) {
			System.out.println("Me: Watch a TV show");
		}
		Pizza pizza = pizzaPickupOrder.get();

		System.out.println("Me: Eat the pizza: " + pizza);

		pool.shutdown();
		System.out.println();
		System.out.println();
	}

	public static void demoCallableVsRunnable() throws InterruptedException, ExecutionException {
		System.out.println();
		System.out.println("Demo: Callable vs Runnable");
		ExecutorService pool = Executors.newCachedThreadPool();

		Runnable makePizza = () -> {
			System.out.println("   Restaurant> Slicing tomatoes");
			System.out.println("   Restaurant> Chopping onions");
			System.out.println("   Restaurant> Spreading with tomato sauce and sprinkle with toppings");
			System.out.println("   Restaurant> Baking pizza");
			// Compare to Callable: need to handle exception here
			try {
				TimeUnit.MILLISECONDS.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Compare to Callable: nothing to return
		};
		
		// compare to submit(Callable): Future<?> here vs Future<T> there
		Future<?> pizzaPickupOrder = pool.submit(makePizza);
		
		// try this: pool.execute(makePizza);

		System.out.println("Me: Calling my brother");
		TimeUnit.MILLISECONDS.sleep(200);
		System.out.println("Me: Walk the dog");

		Object pizza = pizzaPickupOrder.get(); // null
		System.out.println("Me: Eat the pizza: " + pizza);

		pool.shutdown();
	}

	public static class Pizza {

		@Override
		public String toString() {
			return "Classic margherita";
		}

	}

}
package com.packt.tfesenko.multithreading.section5;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Lesson2 {

	public static void main(String[] args) throws InterruptedException {
		// demoConcurrentHashMap();
		demoConcurrentLinkedQueue();

	}

	private static void demoConcurrentHashMap() throws InterruptedException {
		Random random = new Random();
		ExecutorService service = Executors.newCachedThreadPool();

		String brandNewShoes = "Brand new shows";
		String oldPhone = "Old phone";
		String leatherHat = "Leather hat";
		String cowboyShoes = "Cowboy shoes";

		ConcurrentMap<String, String> itemToBuyerMap = new ConcurrentHashMap<>();

		BiConsumer<String, String> buyItemIfNotTaken = (buyer, item) -> {
			try {
				TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
				itemToBuyerMap.putIfAbsent(item, buyer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		service.submit(() -> {
			buyItemIfNotTaken.accept("Alice", brandNewShoes);
			buyItemIfNotTaken.accept("Alice", cowboyShoes);
			buyItemIfNotTaken.accept("Alice", leatherHat);
		});

		service.submit(() -> {
			buyItemIfNotTaken.accept("Bob", brandNewShoes);
			buyItemIfNotTaken.accept("Bob", cowboyShoes);
			buyItemIfNotTaken.accept("Bob", leatherHat);
		});

		service.submit(() -> {
			buyItemIfNotTaken.accept("Carol", brandNewShoes);
			buyItemIfNotTaken.accept("Carol", cowboyShoes);
			buyItemIfNotTaken.accept("Carol", leatherHat);
		});

		service.awaitTermination(2000, TimeUnit.MILLISECONDS);

		itemToBuyerMap
				.forEach((item, buyer) -> System.out.printf("%s bought by %s%n", item, buyer));
	}

	private static void demoConcurrentLinkedQueue() throws InterruptedException {
		Random random = new Random();
		ExecutorService service = Executors.newCachedThreadPool();

		ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
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

		// Try null:
		// service.submit(() -> joinQueue.accept(null));

		// wait so at least several elements are in the queue
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
		}

		service.submit(() -> System.out.println("poll(): " + queue.poll()));
		service.submit(() -> System.out.println("poll(): " + queue.poll()));
		service.submit(() -> System.out.println("poll(): " + queue.poll()));
		service.submit(() -> System.out.println("poll(): " + queue.poll()));
		service.submit(() -> System.out.println("poll(): " + queue.poll()));

		service.awaitTermination(2000, TimeUnit.MILLISECONDS);

		System.out.println("\n    Remaing elements: ");
		queue.forEach((name) -> System.out.println(name));

	}

}

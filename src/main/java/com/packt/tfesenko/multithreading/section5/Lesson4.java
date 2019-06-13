package com.packt.tfesenko.multithreading.section5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Lesson4 {

	public static void main(String[] args) throws InterruptedException {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

		// For Java 8, use Arrays.asList(...)
		List<String> initialElements = List.of("Ella", "Eclair", "Larry", "Felix");

		List<String> cats = new CopyOnWriteArrayList<>(initialElements);

		Runnable feedCats = () -> {
			try {
				for (String cat : cats) {
					System.out.println("Feeding " + cat);
				}
				System.out.println();

			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		scheduler.scheduleAtFixedRate(feedCats, 0, 100, TimeUnit.MILLISECONDS);

		AtomicInteger communityCatNumber = new AtomicInteger(1);
		// This will eventually cause a ConcurrentModificationException
		Runnable adoptCommunityCat = () -> {
			try {
				cats.add("Community cat " + communityCatNumber.getAndIncrement());
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		scheduler.scheduleAtFixedRate(adoptCommunityCat, 1, 1000, TimeUnit.MILLISECONDS);

		TimeUnit.SECONDS.sleep(20);
		scheduler.shutdown();

	}

}

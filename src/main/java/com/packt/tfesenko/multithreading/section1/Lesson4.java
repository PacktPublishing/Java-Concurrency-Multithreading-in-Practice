package com.packt.tfesenko.multithreading.section1;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Lesson4 {

	public static void main(String[] args) throws InterruptedException {
		AppleTree[] appleTrees = AppleTree.newTreeGarden(12);
		PickFruitAction task = new PickFruitAction(appleTrees, 0, appleTrees.length - 1);

		ForkJoinPool pool = ForkJoinPool.commonPool();

		pool.invoke(task);
		// try this: pool.execute(task); 
		// try this: pool.execute(task); task.join();
		// try this: pool.execute(task); pool.awaitTermination(10, TimeUnit.SECONDS);

		System.out.println();
		System.out.println("Done!");
	}

	public static class PickFruitAction extends RecursiveAction {

		private final AppleTree[] appleTrees;
		private final int startInclusive;
		private final int endInclusive;

		private final int taskThreadshold = 4;

		public PickFruitAction(AppleTree[] array, int startInclusive, int endInclusive) {
			this.appleTrees = array;
			this.startInclusive = startInclusive;
			this.endInclusive = endInclusive;
		}

		@Override
		protected void compute() {
			if (endInclusive - startInclusive < taskThreadshold) {
				doCompute();
				return;
			}
			int midpoint = startInclusive + (endInclusive - startInclusive) / 2;

			PickFruitAction leftSum = new PickFruitAction(appleTrees, startInclusive, midpoint);
			PickFruitAction rightSum = new PickFruitAction(appleTrees, midpoint + 1, endInclusive);

			rightSum.fork(); // computed asynchronously
			leftSum.compute();// computed synchronously: immediately and in the current thread
			rightSum.join();
		}

		protected void doCompute() {
			IntStream.rangeClosed(startInclusive, endInclusive)//
					.forEach(i -> appleTrees[i].pickApples());

		}
	}
}

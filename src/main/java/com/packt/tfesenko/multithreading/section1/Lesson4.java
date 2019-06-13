package com.packt.tfesenko.multithreading.section1;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.packt.tfesenko.multithreading.section1.Lesson3.PickFruitTask;

public class Lesson4 {

	public static void main(String[] args) {
		AppleTree[] appleTrees = AppleTree.newTreeGarden(12);
		ForkJoinPool pool = ForkJoinPool.commonPool();

		long startTime = System.currentTimeMillis();
		PickFruitAction task = new PickFruitAction(appleTrees, 0, appleTrees.length - 1);
		int result = pool.invoke(task);

		System.out.println("Total apples picked: " + result);
		long endTime = System.currentTimeMillis();
		System.out.println();
		System.out.println("Done in " + (endTime - startTime));
	}

	public static class PickFruitAction extends RecursiveTask<Integer> {

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
		protected Integer compute() {
			if (endInclusive - startInclusive < taskThreadshold) {
				return doCompute();
			}
			int midpoint = startInclusive + (endInclusive - startInclusive) / 2;

			PickFruitAction leftSum = new PickFruitAction(appleTrees, startInclusive, midpoint);
			PickFruitAction rightSum = new PickFruitAction(appleTrees, midpoint + 1, endInclusive);

			rightSum.fork(); // computed asynchronously

			return leftSum.compute()// computed synchronously: immediately and in the current thread
					+ rightSum.join();
		}

		protected Integer doCompute() {
			return IntStream.rangeClosed(startInclusive, endInclusive)//
					.map(i -> appleTrees[i].pickApples())//
					.sum();
		}
	}
}

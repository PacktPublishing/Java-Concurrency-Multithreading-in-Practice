package com.packt.tfesenko.multithreading.section1;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Lesson5 {

	private static final int treeNumber = 12;

	public static void main(String[] args) {
		AppleTree[] appleTrees = AppleTree.newTreeGarden(treeNumber);
		ForkJoinPool pool = ForkJoinPool.commonPool();

		PickFruitTask task = new PickFruitTask(appleTrees, 0, appleTrees.length - 1);
		int result = pool.invoke(task);

		System.out.println();
		System.out.println("Total apples picked: " + result);
	}

	public static class SomethingWentWrongException extends Exception {
	}

	public static class PickFruitTask extends RecursiveTask<Integer> {

		private final AppleTree[] appleTrees;
		private final int startInclusive;
		private final int endInclusive;

		private final int taskThreadshold = 4;

		public PickFruitTask(AppleTree[] array, int startInclusive, int endInclusive) {
			this.appleTrees = array;
			this.startInclusive = startInclusive;
			this.endInclusive = endInclusive;
		}

		@Override
		protected Integer compute() {
			// throw an exception for any task from the right side of the array
			if (startInclusive >= treeNumber / 2) {
				// try this: int throwException = 10/0;
				// try this: throw new SomethingWentWrongException();
				// try this: completeExceptionally(new SomethingWentWrongException());
			}
			if (endInclusive - startInclusive < taskThreadshold) {
				return doCompute();
			}
			int midpoint = startInclusive + (endInclusive - startInclusive) / 2;

			PickFruitTask leftSum = new PickFruitTask(appleTrees, startInclusive, midpoint);
			PickFruitTask rightSum = new PickFruitTask(appleTrees, midpoint + 1, endInclusive);

			rightSum.fork(); // computed asynchronously

			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// try this: rightSum.cancel(true);

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

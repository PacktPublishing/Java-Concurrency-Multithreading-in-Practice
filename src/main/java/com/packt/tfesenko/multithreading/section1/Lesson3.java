package com.packt.tfesenko.multithreading.section1;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

public class Lesson3 {

	public static void main(String[] args) {
		AppleTree[] appleTrees = AppleTree.newTreeGarden(12);
		ForkJoinPool pool = ForkJoinPool.commonPool();
		
		PickFruitTask task = new PickFruitTask(appleTrees, 0, appleTrees.length - 1);


		System.out.println();
		System.out.println("Total apples picked: ");
	}

	public static class PickFruitTask {

		private final AppleTree[] appleTrees;
		private final int startInclusive;
		private final int endInclusive;

		private final int taskThreadshold = 4;

		public PickFruitTask(AppleTree[] array, int startInclusive, int endInclusive) {
			this.appleTrees = array;
			this.startInclusive = startInclusive;
			this.endInclusive = endInclusive;
		}

		protected Integer doCompute() {
			return IntStream.rangeClosed(startInclusive, endInclusive)//
					.map(i -> appleTrees[i].pickApples())//
					.sum();
	
		}

	}

}

package com.packt.tfesenko.multithreading.section2;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Lesson4 {

	public static void main(String[] args) {
		// General pattern:
		// 1. Create stream
		// 2. Optional: Intermediate Operation aka Transformation
		// 3. Terminal Operation aka Action

//		demoStreamOf();
		demoListToStream();
//		demoArrayToStream();
//		demoRange();
//		demoIntermediateOperationsAreLazy();
	}

	public static void demoListToStream() {
		System.out.println("Cat count (List): ");

		// List.of() was introduced in Java 9, use Arrays.asList for earlier versions
		List<String> petList = List.of("Fluffy, the cat", // cat
				"Caesar, the dog", //
				"Eclair, the cat", // cat
				"Ella, the dog", //
				"Spots, the cat"// cat
		);
		int catCountOld = 0;
		for (String cat : petList) {
			if (cat.endsWith("the cat")) {
				catCountOld++;
			}
		}
		System.out.print("\t");
		System.out.println(catCountOld);

		// Create a stream: from a list
		System.out.println();
	}

	public static void demoArrayToStream() {
		System.out.println("Cat count (Array): ");

		String[] petsArray = { "Fluffy, the cat", // cat
				"Caesar, the dog", //
				"Eclair, the cat", // cat
				"Ella, the dog", //
				"Spots, the cat"// cat
		};

		int catCountOld = 0;
		for (String cat : petsArray) {
			if (cat.endsWith("the cat")) {
				catCountOld++;
			}
		}
		System.out.print("\t");
		System.out.println(catCountOld);

		// Create a stream: from a list
		System.out.println();
	}

	public static void demoStreamOf() {
		System.out.println("Cat count (Stream.of)");
		// Create a stream
//				"Fluffy, the cat", // cat
//				"Caesar, the dog", //
//				"Eclair, the cat", // cat
//				"Ella, the dog", //
//				"Spots, the cat"// cat

		System.out.println();
	}

	public static void demoRange() {
		int[] array = generateArrayWithRandomNumbers(1000);
		int startIndex = 10;
		int endIndex = 900;

		System.out.println("Sum elements in array start from from index 10 up to 900:");
		// without streams
		int sumWithFor = 0;
		for (int i = startIndex; i < endIndex; i++) {
			sumWithFor += array[i];
		}
		System.out.print("\t");
		System.out.println(sumWithFor);

		// the same with streams
		System.out.print("\t");

		System.out.println();
	}

	public static void demoIntermediateOperationsAreLazy() {
		int[] array = generateArrayWithRandomNumbers(1000);

		System.out.println("Print elements of an array:");

		System.out.println();
	}

	private static int[] generateArrayWithRandomNumbers(int arrayLength) {
		return new Random().ints(arrayLength, 1, 1000).toArray();
	}

}

package com.packt.tfesenko.multithreading.section3;

import java.util.Random;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

public class Lesson4 {

	public static void main(String[] args) {
		SubmissionPublisher<WeatherForecast> weatherForecastPublisher = new WeatherForecastPublisher();

		weatherForecastPublisher.subscribe(new DatabaseSubscriber());
		weatherForecastPublisher.subscribe(new TwitterSubscriber<WeatherForecast>());

		Flow.Processor<WeatherForecast, MetricWeatherForecast> metricConverter = new UsToMetricProcessor();
		weatherForecastPublisher.subscribe(metricConverter);
		metricConverter.subscribe(new TwitterSubscriber<MetricWeatherForecast>());

		// close the publisher and associated resources after 10 seconds
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		weatherForecastPublisher.close();
	}

	public static class UsToMetricProcessor extends SubmissionPublisher<MetricWeatherForecast>
			implements Flow.Processor<WeatherForecast, MetricWeatherForecast> {

		private Flow.Subscription subscription;

		@Override
		public void onSubscribe(Flow.Subscription subscription) {
			this.subscription = subscription;
			subscription.request(1);
		}

		@Override
		public void onNext(WeatherForecast item) {
			submit(MetricWeatherForecast.fromImperial(item));
			subscription.request(1);
		}

		@Override
		public void onError(Throwable e) {
			e.printStackTrace();
		}

		@Override
		public void onComplete() {
			close();
		}
	}

	public static class MetricWeatherForecast {

		public static MetricWeatherForecast fromImperial(WeatherForecast imperial) {
			int windSpeedInKMH = (int) (imperial.windSpeedInMPH * 1.60934);
			int temperatureInC = (int) ((imperial.temperatureInF - 32) / 1.8);
			return new MetricWeatherForecast(temperatureInC, windSpeedInKMH,
					imperial.weatherCondition);
		}

		private final int temperatureInC;
		private final int windSpeedInKMH;
		private final String weatherCondition;

		private static final Random random = new Random();
		private static final String[] allWeatherConditions = new String[] { "☁️", "☀️", "⛅", "🌧",
				"⛈️" };

		public static WeatherForecast nextRandomWeatherForecast() {
			String weatherCondition = allWeatherConditions[random
					.nextInt(allWeatherConditions.length)];
			int temperatureInF = random.nextInt(95);
			int windSpeedInMPH = 5 + random.nextInt(30);
			return new WeatherForecast(temperatureInF, windSpeedInMPH, weatherCondition);
		}

		public MetricWeatherForecast(int temperatureInC, int windSpeedInKMH,
				String weatherCondition) {
			super();
			this.temperatureInC = temperatureInC;
			this.windSpeedInKMH = windSpeedInKMH;
			this.weatherCondition = weatherCondition;
		}

		@Override
		public String toString() {
			return weatherCondition + " " + temperatureInC + "°C wind: " + windSpeedInKMH + "km/h";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + temperatureInC;
			result = prime * result
					+ ((weatherCondition == null) ? 0 : weatherCondition.hashCode());
			result = prime * result + windSpeedInKMH;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MetricWeatherForecast other = (MetricWeatherForecast) obj;
			if (temperatureInC != other.temperatureInC)
				return false;
			if (weatherCondition == null) {
				if (other.weatherCondition != null)
					return false;
			} else if (!weatherCondition.equals(other.weatherCondition))
				return false;
			if (windSpeedInKMH != other.windSpeedInKMH)
				return false;
			return true;
		}

	}

	private static final class DatabaseSubscriber implements Flow.Subscriber<WeatherForecast> {
		private Flow.Subscription subscription;
		private final String name = "Database Subscriber";

		@Override
		public void onSubscribe(Subscription subscription) {
			System.out.println(name + " subscribed!");
			this.subscription = subscription;
			subscription.request(1);
		}

		@Override
		public void onNext(WeatherForecast weatherForecast) {
			System.out.println(
					Thread.currentThread().getName() + " > Saving to DB: " + weatherForecast);
			subscription.request(1);
		}

		@Override
		public void onError(Throwable throwable) {
		}

		@Override
		public void onComplete() {
		}
	}

	private static final class TwitterSubscriber<T> implements Flow.Subscriber<T> {
		private Flow.Subscription subscription;
		private final String name = "Twitter Subscriber";

		@Override
		public void onSubscribe(Subscription subscription) {
			System.out.println(name + " subscribed!");
			this.subscription = subscription;
			subscription.request(1);
		}

		@Override
		public void onNext(T weatherForecast) {
			System.out
					.println(Thread.currentThread().getName() + " > Twitting: " + weatherForecast);
			// You can alternatively use subscription.request(Long.MAX_VALUE) in
			// onSubscribe()
			subscription.request(1);
		}

		@Override
		public void onError(Throwable throwable) {
			System.err.println(name + " got an error: " + throwable.getMessage());
		}

		@Override
		public void onComplete() {
			System.out.println(name + " completed.");
		}
	}

	/**
	 * Modification of the example from JavaDoc for
	 * java.util.concurrent.SubmissionPublisher<T>
	 */
	public static class WeatherForecastPublisher extends SubmissionPublisher<WeatherForecast> {
		final ScheduledFuture<?> periodicTask;
		final ScheduledExecutorService scheduler;

		WeatherForecastPublisher() {
			scheduler = new ScheduledThreadPoolExecutor(1);
			periodicTask = scheduler.scheduleAtFixedRate( //
					// runs submit()
					() -> submit(WeatherForecast.nextRandomWeatherForecast()), //
					500, 500, TimeUnit.MILLISECONDS);
		}

		public void close() {
			periodicTask.cancel(false);
			scheduler.shutdown();
			super.close();
		}
	}

	/**
	 * Weather Forecast in United States customary units
	 */
	public static class WeatherForecast {
		private final int temperatureInF;
		private final int windSpeedInMPH;
		private final String weatherCondition;

		private static final Random random = new Random();
		private static final String[] allWeatherConditions = new String[] { "☁️", "☀️", "⛅", "🌧",
				"⛈️" };

		public static WeatherForecast nextRandomWeatherForecast() {
			String weatherCondition = allWeatherConditions[random
					.nextInt(allWeatherConditions.length)];
			int temperatureInF = random.nextInt(95);
			int windSpeedInMPH = 5 + random.nextInt(30);
			return new WeatherForecast(temperatureInF, windSpeedInMPH, weatherCondition);
		}

		public WeatherForecast(int temperatureInF, int windSpeedInMPH, String weatherCondition) {
			super();
			this.temperatureInF = temperatureInF;
			this.windSpeedInMPH = windSpeedInMPH;
			this.weatherCondition = weatherCondition;
		}

		@Override
		public String toString() {
			return weatherCondition + " " + temperatureInF + "°F wind: " + windSpeedInMPH + "mph";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + temperatureInF;
			result = prime * result
					+ ((weatherCondition == null) ? 0 : weatherCondition.hashCode());
			result = prime * result + windSpeedInMPH;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WeatherForecast other = (WeatherForecast) obj;
			if (temperatureInF != other.temperatureInF)
				return false;
			if (weatherCondition == null) {
				if (other.weatherCondition != null)
					return false;
			} else if (!weatherCondition.equals(other.weatherCondition))
				return false;
			if (windSpeedInMPH != other.windSpeedInMPH)
				return false;
			return true;
		}
	}

}

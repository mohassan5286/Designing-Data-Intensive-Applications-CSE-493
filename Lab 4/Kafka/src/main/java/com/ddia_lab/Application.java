package com.ddia_lab;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import java.net.InetAddress;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
public class Application {

	public static double median(List<Long> times) {
		Collections.sort(times);
		int n = times.size();
		if (n % 2 == 0)
			return (times.get(n / 2 - 1) + times.get(n / 2)) / 2.0;
		else
			return times.get(n / 2);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner (KafkaTemplate<String, String> kafkaTemplate, KafkaListeners kafkaListeners) {
		return args -> {
			System.out.println("Program started");

			List<Long> produceTimes = new ArrayList<>();
			List<Long> consumeTimes = new ArrayList<>();
//			int l = 100;

			AtomicLong startTime = new AtomicLong();
///*
			for (int i = 0; i < 10_000; i++) {
				kafkaTemplate.send("topic16", String.valueOf(System.nanoTime()));
			}

//			Thread.sleep(20000);

			// Start the listener
			Properties config = new Properties();
			config.put("client.id", InetAddress.getLocalHost().getHostName());
			config.put("group.id", UUID.randomUUID().toString());
			config.put("bootstrap.servers", "localhost:9092");
			config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
			config.put("max.poll.records", 10000);

			List<String> topics = Arrays.asList("topic16");

			KafkaConsumer<String, String> kvKafkaConsumer = new KafkaConsumer(config);

			kvKafkaConsumer.subscribe(topics);

//			while (l > 0) {
			ConsumerRecords<String, String> records = kvKafkaConsumer.poll(Long.MAX_VALUE );
			records.forEach(record -> {
//				System.out.println(Long.valueOf(record.value()));
				consumeTimes.add(System.nanoTime() - Long.valueOf(record.value()) );
			});
//			l--;
//			kvKafkaConsumer.commitSync();
//			}
//
			System.out.println("Median Latency: " + median(consumeTimes) + " ns");
			System.out.println(consumeTimes.size());
//*/

/*
			Properties config = new Properties();
			config.put("client.id", InetAddress.getLocalHost().getHostName());
			config.put("group.id", UUID.randomUUID().toString());
			config.put("bootstrap.servers", "localhost:9092");
			config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


			List<String> topics = Arrays.asList("topic1");

			KafkaConsumer<String, String> kvKafkaConsumer = new KafkaConsumer(config);

			kvKafkaConsumer.subscribe(topics);

//			 Poll for new messages

			int i = 1000;
//			while (i > 0) {
				ConsumerRecords<String, String> records = kvKafkaConsumer.poll(Duration.ofMillis(1000 * i));
				records.forEach(record -> {
					System.out.println("Topic: " + record.topic() + ", Received message: " + record.value());
				});
//				kvKafkaConsumer.commitSync();
				System.out.println(i);
//				i--;
//			}
*/
			System.out.println("Program ended");
		};
	}

}

package deepseek;

import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.util.*;

public class ConsumerLatencyTest {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", String.valueOf(UUID.randomUUID()));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("max.poll.records", "1"); // Fetch one record per poll
        props.put("auto.offset.reset", "earliest"); // Start from the beginning

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("test-topic-99"));
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < 1000; i++) {
            long start = System.currentTimeMillis();
            ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
            long end = System.currentTimeMillis();
            if (!records.isEmpty()) {
                latencies.add(end - start);
                System.out.println(records);
            }
        }

        Collections.sort(latencies);
        long median = latencies.get(latencies.size() / 2);
        System.out.println("Consume Latency: " + latencies);
        System.out.println("Consume Median Latency: " + median + " ms");
        consumer.close();
    }
}
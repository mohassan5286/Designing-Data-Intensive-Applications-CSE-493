import org.apache.kafka.clients.producer.*;
import java.util.*;
import java.util.concurrent.*;

public class ProducerLatencyTest {
    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(1000);

        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                long start = System.currentTimeMillis();
                producer.send(new ProducerRecord<>("test-topic-99", "key-" + i, "value-" + j), (metadata, e) -> {
                    long end = System.currentTimeMillis();
                    latencies.add(end - start);
                    latch.countDown();
                });
            }
        }

        latch.await(30, TimeUnit.SECONDS); // Wait for all callbacks
        Collections.sort(latencies);
        long median = latencies.get(latencies.size() / 2);
        System.out.println("Produce Latency: " + latencies);
        System.out.println("Produce Median Latency: " + median + " ms");
        producer.close();
    }
}
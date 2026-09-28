package com.ddia_lab;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class BasicConsumeLoop<K, V> implements Runnable {
    private final KafkaConsumer<K, V> consumer;
    private final List<String> topics;
    private final AtomicBoolean shutdown;
    private final CountDownLatch shutdownLatch;

    public BasicConsumeLoop(Properties config, List<String> topics) {
        this.consumer = new KafkaConsumer<>(config);
        this.topics = topics;
        this.shutdown = new AtomicBoolean(false);
        this.shutdownLatch = new CountDownLatch(1);
    }

//    public abstract void process(ConsumerRecord<K, V> record);

    public void run() {
        try {
            consumer.subscribe(topics);

            while (!shutdown.get()) {
                ConsumerRecords<K, V> records = consumer.poll(500);
                records.forEach(record -> record.value());
//                records.forEach(record -> process(record));
            }
        } finally {
            consumer.close();
            shutdownLatch.countDown();
        }
    }

    public void shutdown() throws InterruptedException {
        shutdown.set(true);
        shutdownLatch.await();
    }
}
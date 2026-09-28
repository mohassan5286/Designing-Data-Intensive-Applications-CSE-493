package org.example;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumerMain {
    public static void main(String[] args) throws InterruptedException {
        final int threadCount = Runtime.getRuntime().availableProcessors();
        final int TotalMessages = 170000;
        final String queueName = "Queue";
        final String brokerUrl = "tcp://localhost:61616";

        AtomicInteger messageCounter = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
                    Connection connection = factory.createConnection();
                    connection.start();

                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    Destination destination = session.createQueue(queueName);
                    MessageConsumer consumer = session.createConsumer(destination);

                    while (true) {
                        Message message = consumer.receive(7);
                        if (message == null) break;
                        messageCounter.incrementAndGet();
                    }

                    consumer.close();
                    session.close();
                    connection.close();
                } catch (Exception e) {
                    System.err.println("Error in consumer thread: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        long duration = System.currentTimeMillis() - startTime;
        double throughput = (TotalMessages * 1000.0) / duration;

        System.out.println("✅ Consumer finished.");
        System.out.println("Total messages received: " + TotalMessages);
        System.out.println("Time taken: " + duration + " ms");
        System.out.printf("Achieved throughput: %.2f messages/second%n", throughput);
    }
}

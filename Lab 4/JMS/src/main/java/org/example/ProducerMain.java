package org.example;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProducerMain {
    public static void main(String[] args) throws InterruptedException {
        final int totalMessages = 170000;
        final int threadCount = Runtime.getRuntime().availableProcessors();

        final String queueName = "Queue";
        final String brokerUrl = "tcp://localhost:61616";

        int messagesPerThread = totalMessages / threadCount;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        final String text = "\0".repeat(1024) ;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    Connection connection = factory.createConnection();
                    connection.start();

                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    Destination destination = session.createQueue(queueName);
                    MessageProducer producer = session.createProducer(destination);
                    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                    TextMessage message = session.createTextMessage(text);

                    for (int j = 0; j < messagesPerThread; j++) producer.send(message);

                    session.close();
                    connection.close();
                } catch (Exception e) {
                    System.err.println("Error in producer thread: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        long duration = System.currentTimeMillis() - startTime;
        double throughput = (totalMessages * 1000.0) / duration;

        System.out.println("Producer finished.");
        System.out.println("Total messages sent: " + totalMessages);
        System.out.println("Time taken: " + duration + " ms");
        System.out.printf("Achieved throughput: %.2f messages/second%n", throughput);
    }
}

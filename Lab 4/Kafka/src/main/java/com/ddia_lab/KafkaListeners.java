package com.ddia_lab;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KafkaListeners {

    public  static List<Long> consumeTimes = new ArrayList<>();

//    @KafkaListener(
//            topics = "topic1",
//            groupId = "group1",
//            autoStartup = "false"
//    )
//    void listener(@Payload String data,@Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {
////        System.out.println("Listener received: " + data + " at " + ts);
//        consumeTimes.add(ts);
//        if (consumeTimes.size() % 1000 == 0) {
//            System.out.println("Listener consumed: " + consumeTimes.size() + " messages");
//        }
//    }

}

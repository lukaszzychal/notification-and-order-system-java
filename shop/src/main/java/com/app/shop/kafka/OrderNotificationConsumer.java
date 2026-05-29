package com.app.shop.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderNotificationConsumer {

    @KafkaListener(topics = "orders", groupId = "shop-group")
    public void consumeNotification(String message) {
        System.out.println("Otrzymano powiadomienie o zamówieniu: " + message);
    }

}

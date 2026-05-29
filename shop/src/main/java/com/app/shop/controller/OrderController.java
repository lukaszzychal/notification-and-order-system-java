package com.app.shop.controller;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.shop.repository.OrderRepository;

import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.shop.model.Order;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderController(OrderRepository orderRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {

        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            return ResponseEntity.ok(order);
        }

        return ResponseEntity.notFound().build();

    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order savedOrder = orderRepository.save(order);

        String topic = "orders";
        String message = "Nowe zamówienie o ID: " + savedOrder.getId();
        kafkaTemplate.send(topic, message);
        System.out.println("Wysłano wiadomość do tematu: " + topic + " Treść: " + message);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);

    }

}
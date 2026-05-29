package com.app.shop.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.app.shop.model.Order;
import com.app.shop.model.ProductStock;
import com.app.shop.repository.OrderRepository;
import com.app.shop.repository.ProductStockRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductStockRepository productStockRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, ProductStockRepository productStockRepository,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.productStockRepository = productStockRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Order placeOrder(Order order) {
        ProductStock productStock = productStockRepository.findByProductCode(order.getProductName())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (productStock.getAvailableQuantity() < order.getQuantity()) {
            throw new IllegalArgumentException("Product out of stock");
        }

        productStock.setAvailableQuantity(productStock.getAvailableQuantity() - order.getQuantity());
        productStockRepository.save(productStock);

        Order savedOrder = orderRepository.save(order);
        kafkaTemplate.send("order-topic",
                "Created order: " + savedOrder.getId() + ", product name: " + savedOrder.getProductName());

        return savedOrder;
    }

}

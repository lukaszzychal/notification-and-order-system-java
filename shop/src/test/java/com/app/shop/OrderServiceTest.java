package com.app.shop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.app.shop.model.Order;
import com.app.shop.model.ProductStock;
import com.app.shop.repository.OrderRepository;
import com.app.shop.repository.ProductStockRepository;
import com.app.shop.service.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductStockRepository stockRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void shouldThrowExceptionWhenNotEnoughStock() {
        Order order = new Order();
        order.setProductName("Laptop");
        order.setQuantity(5);

        ProductStock stock = new ProductStock();
        stock.setProductCode("Laptop");
        stock.setAvailableQuantity(2);

        when(stockRepository.findByProductCode("Laptop")).thenReturn(Optional.of(stock));

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder(order);
        });

        verify(orderRepository, never()).save(any(Order.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}

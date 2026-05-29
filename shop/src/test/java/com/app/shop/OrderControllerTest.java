package com.app.shop;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.shop.controller.OrderController;
import com.app.shop.model.Order;
import com.app.shop.repository.OrderRepository;
import com.app.shop.service.OrderService;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    public void shouldCreateOrderAndReturn201() throws Exception {
        Order order = createSampleOrder();

        Mockito.when(orderService.placeOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated());
    }

    private Order createSampleOrder() {
        return Order.builder()
                .id(1L)
                .productName("Test product")
                .quantity(1)
                .price(new BigDecimal("10.00"))
                .build();
    }

}

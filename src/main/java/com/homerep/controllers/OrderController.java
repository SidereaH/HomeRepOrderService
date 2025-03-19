package com.homerep.controllers;

import com.homerep.models.Order;
import com.homerep.repositories.AddressRepository;
import com.homerep.repositories.CityRepository;
import com.homerep.repositories.OrderRepository;
import com.homerep.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {


        private final OrderService orderService;

        public OrderController(OrderService orderService) {
            this.orderService = orderService;
        }

        @PostMapping
        public ResponseEntity<Order> createOrder(@RequestBody Order order) {
            return ResponseEntity.ok(orderService.createOrder(order));
        }

        @GetMapping
        public ResponseEntity<List<Order>> getAllOrders() {
            return ResponseEntity.ok(orderService.getAllOrders());
        }


}

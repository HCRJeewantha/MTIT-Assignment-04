package com.sliit.mtit9.ordersservice.controllers;

import com.sliit.mtit9.ordersservice.dto.Orders;
import com.sliit.mtit9.ordersservice.persistence.OrdersRepository;
import com.sliit.mtit9.ordersservice.services.OrdersServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api/orders", produces = "application/json")
public class OrderController {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersServiceImpl ordersService;


    @PostMapping(value = "/createOrder")
    public @ResponseBody
    Iterable<Orders> addCart(@RequestBody Orders order) {
        ResponseEntity<Float> getUserCartTotalById = ordersService.getCartByUserId(order.getUserId());
        Orders order1 = new Orders();
        order1.setTotalPrice(getUserCartTotalById.getBody().floatValue());
        order1.setPaymentStatus("Pending");
        order1.setUserId(order.getUserId());


        ordersRepository.save(order1);

        return ordersRepository.findAll();
    }

    @GetMapping("/getOrderById/{id}")
    public  @ResponseBody
    Optional<Orders> getOrderById(@PathVariable Integer id){
        return ordersRepository.findById(id);
    }

    @PostMapping(value = "/updateStatus")
    public @ResponseBody
    Iterable<Orders> updateOrderStatus(@RequestBody Orders order) {
        ordersRepository.save(order);
        return ordersRepository.findAll();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

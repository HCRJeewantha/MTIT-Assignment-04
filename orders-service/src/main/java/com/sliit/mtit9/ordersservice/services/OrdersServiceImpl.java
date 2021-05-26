package com.sliit.mtit9.ordersservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrdersServiceImpl {

    @Autowired
    private RestTemplate restTemplate;


    public ResponseEntity<Float> getCartByUserId(Integer id){
        Map<String, Integer> params = new HashMap<>();
        params.put("id", id);
        ResponseEntity<Float> paymentResponse = restTemplate.getForEntity("http://localhost:8181/api/cart/getCartTotal/{id}", Float.class , params);
        return paymentResponse;
    }

}

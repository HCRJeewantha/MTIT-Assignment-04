package com.sliit.mtit9.paymentservice.services;

import com.sliit.mtit9.paymentservice.dto.CartRequest;
import com.sliit.mtit9.paymentservice.dto.OrderRequest;
import com.sliit.mtit9.paymentservice.dto.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
public class PaymentServiceImpl {

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<CartRequest> getCart(Integer id){
        Map<String, Integer> params = new HashMap<>();
        params.put("id", id);
        ResponseEntity<CartRequest> paymentResponse = restTemplate.getForEntity("http://localhost:8181/api/cart/getCartItemById/{id}",CartRequest.class , params);
        return paymentResponse;
    }

    public ResponseEntity<ProductRequest> getProduct(Integer id){
        Map<String, Integer> params = new HashMap<>();
        params.put("id", id);
        ResponseEntity<ProductRequest> productResponse = restTemplate.getForEntity("http://localhost:8083/api/products/getProduct/{id}",ProductRequest.class , params);
        return productResponse;
    }

    public String deleteCart(Integer id){
        Map<String, Integer> params = new HashMap<>();
        params.put("id", id);
        restTemplate.delete("http://localhost:8181/api/cart/deleteCartItem/{id}", params);
        return "Cart Item Deleted, Cart Id: " + id;
    }

    public String deleteCartAll(){
        restTemplate.delete("http://localhost:8181/api/cart/deleteCartAll");
        return "Cart Item Deleted";
    }

    public ResponseEntity<OrderRequest> getOrder(Integer id){
        Map<String, Integer> params = new HashMap<>();
        params.put("id", id);
        ResponseEntity<OrderRequest> orderResponse = restTemplate.getForEntity("http://localhost:8186/api/orders/getOrderById/{id}",OrderRequest.class , params);
        return orderResponse;
    }

    public ResponseEntity<String> updateOrderStatus(OrderRequest orderRequest){
        ResponseEntity<String> orderResponse = restTemplate.postForEntity("http://localhost:8186/api/orders/updateStatus",orderRequest, String.class);
        return orderResponse;
    }
}

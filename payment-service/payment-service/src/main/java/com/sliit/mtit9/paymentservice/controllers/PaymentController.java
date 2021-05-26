package com.sliit.mtit9.paymentservice.controllers;


import com.sliit.mtit9.paymentservice.dto.OrderRequest;
import com.sliit.mtit9.paymentservice.dto.Payment;
import com.sliit.mtit9.paymentservice.dto.PaymentResponse;
import com.sliit.mtit9.paymentservice.persistence.PaymentRepository;
import com.sliit.mtit9.paymentservice.services.PaymentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/payment", produces = "application/json")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PaymentServiceImpl paymentService;

    @PostMapping(value = "/addPayment")
    public @ResponseBody
    String addPayment(@RequestBody Payment payment){

        List<Payment> payments = jdbcTemplate.query("SELECT * FROM payment WHERE cart_id="+payment.getCartId(), (resultSet, rowNum) -> new Payment(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getString("payment_method"),
                resultSet.getFloat("total"),
                resultSet.getString("payment_status"),
                resultSet.getInt("cart_id"),
                resultSet.getInt("product_id"),
                resultSet.getInt("order_id")
        ));
        
        if(payments.isEmpty()){
            Payment payment1 = new Payment();

            payment1.setPaymentMethod(payment.getPaymentMethod());
            payment1.setUserId(payment.getUserId());
            payment1.setTotal(payment.getTotal());
            payment1.setPaymentStatus(payment.getPaymentStatus());
            payment1.setCartId(payment.getCartId());
            payment1.setOrderId(0);
            payment1.setProductId(paymentService.getCart(payment.getCartId()).getBody().getId());

            paymentRepository.save(payment1);

            paymentService.deleteCart(payment.getCartId());

            return "Payment added successfully. User Id: " + payment.getUserId() + ". Item Removed from cart. Cart Id " + payment.getCartId();

        }else{
            return "You already paid";
        }
    }

    @PostMapping(value = "/addOrderPayment")
    public @ResponseBody
    String addOrderPayment(@RequestBody Payment payment){


        List<Payment> payments = jdbcTemplate.query("SELECT * FROM payment WHERE order_id="+payment.getOrderId(), (resultSet, rowNum) -> new Payment(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getString("payment_method"),
                resultSet.getFloat("total"),
                resultSet.getString("payment_status"),
                resultSet.getInt("cart_id"),
                resultSet.getInt("product_id"),
                resultSet.getInt("order_id")
        ));
        ResponseEntity<OrderRequest> order = paymentService.getOrder(payment.getOrderId());

        if(payments.isEmpty()){
            Payment payment1 = new Payment();

            payment1.setPaymentMethod(payment.getPaymentMethod());
            payment1.setUserId(payment.getUserId());
            payment1.setTotal(payment.getTotal());
            payment1.setPaymentStatus(payment.getPaymentStatus());
            payment1.setCartId(0);
            payment1.setOrderId(payment.getOrderId());

            paymentRepository.save(payment1);

            OrderRequest orderResponse = new OrderRequest();


            orderResponse.setId(order.getBody().getId());
            orderResponse.setPaymentStatus("Paid");
            orderResponse.setTotalPrice(order.getBody().getTotalPrice());
            orderResponse.setUserId(order.getBody().getUserId());

            paymentService.updateOrderStatus(orderResponse);

            return "Payment added successfully. User Id: " + payment.getUserId() + "Order Id: " + payment.getOrderId();

        }else{
            return "You already paid";
        }

    }

    @GetMapping(value = "/getAllPayment")
    public @ResponseBody Iterable<Payment> getAllPayment(){
        return paymentRepository.findAll();
    }

    @GetMapping(value = "/getPayment/{id}")
    public @ResponseBody
    List<Object> getPayment(@PathVariable Integer id){

        List<Payment> payments = jdbcTemplate.query("SELECT * FROM payment WHERE user_id="+id, (resultSet, rowNum) -> new Payment(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getString("payment_method"),
                resultSet.getFloat("total"),
                resultSet.getString("payment_status"),
                resultSet.getInt("cart_id"),
                resultSet.getInt("product_id"),
                resultSet.getInt("order_id")
        ));
        List<Object> userPayment = new ArrayList<>();


        for(Payment payment : payments){
            PaymentResponse paymentResponse = new PaymentResponse();

            paymentResponse.setId(payment.getId());
            paymentResponse.setUserId(payment.getUserId());
            paymentResponse.setTotal(payment.getTotal());
            paymentResponse.setCartId(payment.getCartId());
            paymentResponse.setProductId(paymentService.getProduct(payment.getProductId()).getBody().getId());
            paymentResponse.setProductName(paymentService.getProduct(payment.getProductId()).getBody().getProductName());

            userPayment.add(paymentResponse);
        }
        return userPayment;
    }

    @PutMapping(value = "/updatePayment")
    public String updatePayment(@RequestBody Payment payment){
        paymentRepository.save(payment);
        return payment.getId() + " Payment Updated Successfully";
    }

    @DeleteMapping(value = "/deletePayment/{id}")
    public @ResponseBody
    String deletePayment(@PathVariable Integer id){
        paymentRepository.deleteById(id);
        return id + " Payment Deleted Successfully";
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

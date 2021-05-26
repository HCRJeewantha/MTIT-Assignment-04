package com.sliit.mtit9.cartservice.controllers;


import com.sliit.mtit9.cartservice.dto.Cart;
import com.sliit.mtit9.cartservice.dto.CartResponse;
import com.sliit.mtit9.cartservice.persistence.CartRepository;
import com.sliit.mtit9.cartservice.services.CardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/cart", produces = "application/json")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CardServiceImpl cartService;

    @PostMapping(value = "/addCart")
    public @ResponseBody
    String addCart(@RequestBody Cart cart) {
        cartRepository.save(cart);
        return "Add to cart successfully. Product Id: " + cart.getProductId();
    }

    @GetMapping(value = "/getAllCart")
    public @ResponseBody
    Iterable<Cart> getAllCart(){
        return cartRepository.findAll();
    }

    //Display cart
    @GetMapping(value = "/getCart/{id}")
    public @ResponseBody
    List<Object> getCart(@PathVariable Integer id){

        //get cart items by user
        List<Cart> carts = jdbcTemplate.query("SELECT * FROM cart WHERE user_id="+id, (resultSet, rowNum) -> new Cart(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getInt("product_id"),
                resultSet.getInt("quantity")
        ));

        //Cart Item Array list
        List<Object> userCart = new ArrayList<>();

        //Loop cart items
        for(Cart cart : carts){
            CartResponse cartResponse = new CartResponse();

            cartResponse.setId(cart.getId());
            cartResponse.setQuantity(cart.getQuantity());
            cartResponse.setProductName(cartService.getProduct(cart.getProductId()).getBody().getProductName());
            cartResponse.setDescription(cartService.getProduct(cart.getProductId()).getBody().getDescription());
            cartResponse.setPrice(cartService.getProduct(cart.getProductId()).getBody().getPrice());
            cartResponse.setProductId(cartService.getProduct(cart.getProductId()).getBody().getId());

            userCart.add(cartResponse);
        }
        return userCart;
    }

    @GetMapping(value = "/getCartTotal/{id}")
    public @ResponseBody
    float getCartTotal(@PathVariable Integer id){

        //get cart items by user
        List<Cart> carts = jdbcTemplate.query("SELECT * FROM cart WHERE user_id="+id, (resultSet, rowNum) -> new Cart(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getInt("product_id"),
                resultSet.getInt("quantity")
        ));

        float CartTotal = 0;

        //Loop cart items
        for(Cart cart : carts){

            float Total = cart.getQuantity() * cartService.getProduct(cart.getProductId()).getBody().getPrice();
            CartTotal = CartTotal + Total;
        }
        return CartTotal;
    }

    @PutMapping(value = "/updateCart")
    public String updateCart(@RequestBody Cart cart){
        cartRepository.save(cart);
        return cart.getId() + " Cart Updated Successfully";
    }

    @DeleteMapping(value = "/deleteCartItem/{id}")
    public @ResponseBody
    String deleteCart(@PathVariable Integer id){
        cartRepository.deleteById(id);
        return id + " Product Deleted Successfully";
    }

    @DeleteMapping(value = "/deleteCartAll")
    public @ResponseBody
    String deleteCartAll(){
        cartRepository.deleteAll();
        return " Cart Deleted Successfully";
    }

    @GetMapping(value = "/getCartItemById/{id}")
    public Object getItemById(@PathVariable Integer id){

        Optional<Cart> cartItem = cartRepository.findById(id);
        if(cartItem.isEmpty()){
            return "cart item not found";
        }else {

            List<Cart> cart = jdbcTemplate.query("SELECT * FROM cart WHERE id=" + id, (resultSet, rowNum) -> new Cart(
                    resultSet.getInt("id"),
                    resultSet.getInt("user_id"),
                    resultSet.getInt("product_id"),
                    resultSet.getInt("quantity")
            ));

            CartResponse cartResponse = new CartResponse();

            cartResponse.setQuantity(cart.get(0).getQuantity());
            cartResponse.setProductName(cartService.getProduct(cart.get(0).getProductId()).getBody().getProductName());
            cartResponse.setDescription(cartService.getProduct(cart.get(0).getProductId()).getBody().getDescription());
            cartResponse.setPrice(cartService.getProduct(cart.get(0).getProductId()).getBody().getPrice());
            cartResponse.setId(cartService.getProduct(cart.get(0).getProductId()).getBody().getId());


            return cartResponse;
        }
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}

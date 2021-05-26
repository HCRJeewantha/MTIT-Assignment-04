package com.sliit.mtit9.paymentservice.dto;

public class PaymentResponse {
    private Integer Id;
    private Integer UserId;
    private Float Total;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Integer getUserId() {
        return UserId;
    }

    public void setUserId(Integer userId) {
        this.UserId = userId;
    }


    public Float getTotal() {
        return Total;
    }

    public void setTotal(Float total) {
        Total = total;
    }
}

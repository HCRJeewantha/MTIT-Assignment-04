package com.sliit.mtit9.paymentservice.persistence;


import com.sliit.mtit9.paymentservice.dto.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Integer> {
}

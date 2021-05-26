package com.sliit.mtit9.ordersservice.persistence;

import com.sliit.mtit9.ordersservice.dto.Orders;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends CrudRepository<Orders, Integer> {
}

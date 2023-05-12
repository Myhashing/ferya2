package com.mlmfreya.ferya2.repository;

import com.mlmfreya.ferya2.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
package com.mlmfreya.ferya2.service;

import com.mlmfreya.ferya2.exception.ResourceNotFoundException;
import com.mlmfreya.ferya2.model.Order;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;



    public Order findOrderById(Long id){
        return orderRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Order not found","id", id));
    }
}

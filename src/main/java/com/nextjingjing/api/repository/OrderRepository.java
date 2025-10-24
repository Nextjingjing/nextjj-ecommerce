package com.nextjingjing.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextjingjing.api.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

}

package com.nextjingjing.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextjingjing.api.entity.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long>{

}

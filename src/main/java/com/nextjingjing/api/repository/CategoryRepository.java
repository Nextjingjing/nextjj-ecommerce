package com.nextjingjing.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextjingjing.api.entity.Order;

public interface CategoryRepository extends JpaRepository<Order, Long>{

}

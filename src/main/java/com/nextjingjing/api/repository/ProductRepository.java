package com.nextjingjing.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextjingjing.api.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}

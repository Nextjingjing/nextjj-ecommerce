package com.nextjingjing.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextjingjing.api.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrder_Id(Long orderId);
}


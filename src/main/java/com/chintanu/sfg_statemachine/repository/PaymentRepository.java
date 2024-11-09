package com.chintanu.sfg_statemachine.repository;

import com.chintanu.sfg_statemachine.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}

package com.chintanu.sfg_statemachine.service;

import com.chintanu.sfg_statemachine.model.Payment;
import com.chintanu.sfg_statemachine.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {

        payment = Payment.builder().id(UUID.fromString("0c9d1a78-af97-4b82-996e-a5a9b27a18e9")).amount(12L).build();
        paymentService.newPayment(payment);

        //System.out.println(paymentRepository.getReferenceById(payment.getId()));
    }

    @Test
    @Transactional
    void testNewInteractionsValidTransition() {

        paymentService.preAuth(UUID.fromString("0c9d1a78-af97-4b82-996e-a5a9b27a18e9"));
    }

    @Test
    @Transactional
    void testNewInteractionsInvalidTransition() {

        paymentService.auth(UUID.fromString("0c9d1a78-af97-4b82-996e-a5a9b27a18e9"));
    }

    @RepeatedTest(10)
    @Transactional
    void testAuthInteraction() {

        paymentService.preAuth(UUID.fromString("0c9d1a78-af97-4b82-996e-a5a9b27a18e9"));
        paymentService.auth(UUID.fromString("0c9d1a78-af97-4b82-996e-a5a9b27a18e9"));
    }
}
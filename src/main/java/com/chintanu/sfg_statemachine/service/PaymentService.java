package com.chintanu.sfg_statemachine.service;

import com.chintanu.sfg_statemachine.model.Payment;

import java.util.UUID;

public interface PaymentService {

    void newPayment(Payment payment);
    void preAuth(UUID paymentId);
    void approvePreAuth(UUID paymentId);
    void declinePreAuth(UUID paymentId);
    void auth(UUID paymentId);
    void approveAuth(UUID paymentId);
    void declineAuth(UUID paymentId);
}

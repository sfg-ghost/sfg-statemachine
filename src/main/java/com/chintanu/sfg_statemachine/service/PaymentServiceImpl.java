package com.chintanu.sfg_statemachine.service;

import com.chintanu.sfg_statemachine.model.Payment;
import com.chintanu.sfg_statemachine.model.PaymentEvents;
import com.chintanu.sfg_statemachine.model.PaymentStates;
import com.chintanu.sfg_statemachine.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentStates, PaymentEvents> machineFactory;
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    @Override
    public void newPayment(Payment payment) {

        //System.out.println("In newPayment");
        payment.setState(PaymentStates.NEW);
        paymentRepository.save(payment);
    }

    @Override
    public void preAuth(UUID paymentId) {

        //System.out.println("In preAuth");

        StateMachine<PaymentStates, PaymentEvents> machine = build(paymentId);

        sendEvent(paymentId, PaymentEvents.PRE_AUTH, machine);
    }

    @Override
    public void approvePreAuth(UUID paymentId) {

        StateMachine<PaymentStates, PaymentEvents> machine = build(paymentId);

        sendEvent(paymentId, PaymentEvents.PRE_AUTH_APPROVE, machine);
    }

    @Override
    public void declinePreAuth(UUID paymentId) {

        StateMachine<PaymentStates, PaymentEvents> machine = build(paymentId);

        sendEvent(paymentId, PaymentEvents.PRE_AUTH_ERROR, machine);
    }

    @Override
    public void auth(UUID paymentId) {

        StateMachine<PaymentStates, PaymentEvents> machine = build(paymentId);

        sendEvent(paymentId, PaymentEvents.AUTH, machine);
    }

    @Override
    public void approveAuth(UUID paymentId) {

        StateMachine<PaymentStates, PaymentEvents> machine = build(paymentId);

        sendEvent(paymentId, PaymentEvents.AUTH_APPROVE, machine);
    }

    @Override
    public void declineAuth(UUID paymentId) {

        StateMachine<PaymentStates, PaymentEvents> machine = build(paymentId);

        sendEvent(paymentId, PaymentEvents.AUTH_ERROR, machine);
    }

    private void sendEvent(UUID paymentId, PaymentEvents event, StateMachine<PaymentStates, PaymentEvents> machine) {

        System.out.println("In sendEvent : " + paymentId.toString() + " : " + event.toString());
        Message<PaymentEvents> msg = MessageBuilder.withPayload(event).setHeader("paymentId", paymentId).build();

        machine.sendEvent(Mono.just(msg)).subscribe();
    }

    //@Transactional
    private StateMachine<PaymentStates, PaymentEvents> build(UUID paymentId) {

        //System.out.println("Inside Build");

        Payment payment = paymentRepository.getReferenceById(paymentId);

        StateMachine<PaymentStates, PaymentEvents> machine = machineFactory.getStateMachine(paymentId);
        machine.stopReactively().subscribe();

        machine.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.resetStateMachineReactively(new DefaultStateMachineContext<>(payment.getState(), null, null, null)).subscribe();
                    sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
                });

        machine.startReactively().subscribe();
        System.out.println("Restart : " + machine.getState());
        return machine;
    }
}

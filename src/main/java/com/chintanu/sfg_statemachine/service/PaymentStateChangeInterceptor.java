package com.chintanu.sfg_statemachine.service;

import com.chintanu.sfg_statemachine.model.Payment;
import com.chintanu.sfg_statemachine.model.PaymentEvents;
import com.chintanu.sfg_statemachine.model.PaymentStates;
import com.chintanu.sfg_statemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentStates, PaymentEvents> {

    private final PaymentRepository paymentRepository;
    @Override
    public void preStateChange(State<PaymentStates, PaymentEvents> state, Message<PaymentEvents> message,
                               Transition<PaymentStates, PaymentEvents> transition, StateMachine<PaymentStates, PaymentEvents> stateMachine,
                               StateMachine<PaymentStates, PaymentEvents> rootStateMachine) {

        UUID uuid = message.getHeaders().get("paymentId", UUID.class);
        System.out.println(uuid);

        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(uuid).ifPresent(paymentId -> {

                System.out.println("State preStateChange : " + state.getId());
                Payment payment = paymentRepository.getReferenceById(paymentId);
                payment.setState(state.getId());
                paymentRepository.save(payment);
            });
        });

    }

}

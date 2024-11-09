package com.chintanu.sfg_statemachine.bootstrap;

import com.chintanu.sfg_statemachine.model.PaymentEvents;
import com.chintanu.sfg_statemachine.model.PaymentStates;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

//@Component
@RequiredArgsConstructor
public class MachineLoader implements CommandLineRunner {

    private final StateMachine<PaymentStates, PaymentEvents> automata;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("State : " + automata.getState());
        automata.startReactively().subscribe();
        //automata.start();
        System.out.println("State after start : " + automata.getState());

        Message<PaymentEvents> event = MessageBuilder.withPayload(PaymentEvents.PRE_AUTH_APPROVE).build();
        automata.sendEvent(Mono.just(event)).subscribe();
        //automata.sendEvent(event);

        System.out.println("State after event : " + automata.getState());

        //automata.stopReactively();
        //automata.stop();
    }
}

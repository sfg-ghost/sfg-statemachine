package com.chintanu.sfg_statemachine.config;

import com.chintanu.sfg_statemachine.model.PaymentEvents;
import com.chintanu.sfg_statemachine.model.PaymentStates;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.*;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import reactor.core.publisher.Mono;

import java.util.EnumSet;
import java.util.Random;
import java.util.UUID;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfiguration extends StateMachineConfigurerAdapter<PaymentStates, PaymentEvents> {

    //Random random = new Random();

    @Override
    public void configure(StateMachineStateConfigurer<PaymentStates, PaymentEvents> states) throws Exception {

        states.withStates()
                .states(EnumSet.allOf(PaymentStates.class))
                .initial(PaymentStates.NEW)
                .end(PaymentStates.AUTH)
                .end(PaymentStates.AUTH_ERROR)
                .end(PaymentStates.PRE_AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentStates, PaymentEvents> transitions) throws Exception {

        transitions.withExternal()
                .source(PaymentStates.NEW).target(PaymentStates.NEW).event(PaymentEvents.PRE_AUTH).action(preAuthAction())
                .and().withExternal()
                .source(PaymentStates.NEW).target(PaymentStates.PRE_AUTH).event(PaymentEvents.PRE_AUTH_APPROVE)
                .and().withExternal()
                .source(PaymentStates.NEW).target(PaymentStates.PRE_AUTH_ERROR).event(PaymentEvents.PRE_AUTH_ERROR)
                .and().withExternal()
                .source(PaymentStates.PRE_AUTH).target(PaymentStates.PRE_AUTH).event(PaymentEvents.AUTH).action(authAction())
                .and().withExternal()
                .source(PaymentStates.PRE_AUTH).target(PaymentStates.AUTH).event(PaymentEvents.AUTH_APPROVE)
                .and().withExternal()
                .source(PaymentStates.PRE_AUTH).target(PaymentStates.AUTH_ERROR).event(PaymentEvents.AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentStates, PaymentEvents> config) throws Exception {

        StateMachineListenerAdapter<PaymentStates, PaymentEvents> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PaymentStates, PaymentEvents> from, State<PaymentStates, PaymentEvents> to) {

                System.out.println("State Changed");

                if (null != from) {

                    System.out.println("State changed from : " + from.getId());
                }

                if (null != to) {

                    System.out.println("State changed to : " + to.getId());
                }
            }
        };

        config.withConfiguration().listener(adapter);
    }

    private Action<PaymentStates, PaymentEvents> preAuthAction() {

        //System.out.println("In preAuthAction");

        return context -> {

            //System.out.println("In preAuthAction - 1");
            int next = new Random().nextInt(10);
            StateMachine<PaymentStates, PaymentEvents> machine = context.getStateMachine();
            Object paymentId = context.getMessageHeader("paymentId");
            Message<PaymentEvents> msg;

            //System.out.println("Next preauth : " + next);

            if (next < 8) {

                msg = MessageBuilder.withPayload(PaymentEvents.PRE_AUTH_APPROVE).setHeader("paymentId", paymentId).build();
            } else {

                msg = MessageBuilder.withPayload(PaymentEvents.PRE_AUTH_ERROR).setHeader("paymentId", paymentId).build();
            }

            machine.sendEvent(Mono.just(msg)).subscribe();
        };
    }

    private Action<PaymentStates, PaymentEvents> authAction() {

        //System.out.println("In authAction");

        return context -> {

            //System.out.println("In authAction - 1");
            int next = new Random().nextInt(10);
            StateMachine<PaymentStates, PaymentEvents> machine = context.getStateMachine();
            Object paymentId = context.getMessageHeader("paymentId");
            Message<PaymentEvents> msg;

            //System.out.println("Next auth : " + next);

            if (next < 8) {

                msg = MessageBuilder.withPayload(PaymentEvents.AUTH_APPROVE).setHeader("paymentId", paymentId).build();
            } else {

                msg = MessageBuilder.withPayload(PaymentEvents.AUTH_ERROR).setHeader("paymentId", paymentId).build();
            }

            machine.sendEvent(Mono.just(msg)).subscribe();
        };
    }
}

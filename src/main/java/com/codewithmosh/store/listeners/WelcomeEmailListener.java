package com.codewithmosh.store.listeners;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.codewithmosh.store.events.UserRegisteredEvent;

@Component
public class WelcomeEmailListener {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void SendEmail(UserRegisteredEvent event) {
        System.out.println("Sending welcome email to " + event.getEmail());
    }
}

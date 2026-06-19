package com.codewithmosh.store.services;

import org.springframework.stereotype.Service;

@Service
public class PaypalPaymentService implements PaymentService {
    public void processPayment(double amount) {
        System.out.println("PAYPAL");
        System.out.println("Amount: " + amount);
    }
}

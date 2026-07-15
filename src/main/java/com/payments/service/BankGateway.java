package com.payments.service;

import com.payments.exception.GatewayException;
import com.payments.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;

/**
 * Simulates the downstream bank connection.
 * The bank allows only a LIMITED number of connections at the same time,
 * so we must not let more than 5 threads inside settle(...) simultaneously.
 */
@Service
public class BankGateway {

    private final Semaphore permitSemaphore;

    public BankGateway(@Value("${gateway.maxConnections:5}") int maxConcurrentConnections) {
        this.permitSemaphore = new Semaphore(maxConcurrentConnections);
    }

    public Transaction settle(Transaction txn) throws GatewayException {
        try {
            permitSemaphore.acquire();
            Thread.sleep(50);
            return txn.settled();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GatewayException("Settlement process was interrupted");
        } finally {
            permitSemaphore.release();
        }
    }
}

package com.payments.service;

import com.payments.exception.GatewayException;
import com.payments.model.Transaction;
import org.springframework.stereotype.Component;

/**
 * Simulates the downstream bank connection.
 * The bank allows only a LIMITED number of connections at the same time,
 * so we must not let more than 5 threads inside settle(...) simultaneously.
 */
@Component
public class BankGateway {

    // TODO 1: Declare a Semaphore field (final).

    public BankGateway() {
        // TODO 2: Constructor
        //         - Initialise the Semaphore with 5 permits.
    }

    public Transaction settle(Transaction txn) throws GatewayException {
        // TODO 3: public Transaction settle(Transaction txn) throws GatewayException
        //         Steps:
        //           a) Acquire a permit  (this BLOCKS if the limit is reached).
        //           b) In a try block:
        //                - Thread.sleep(50) to simulate contacting the bank.
        //                - Return a settled copy of the transaction: txn.settled()
        //           c) If interrupted (InterruptedException) ->
        //                throw a new GatewayException(...).
        //           d) In a finally block:
        //                - ALWAYS release the permit, even if an exception was thrown.
        //         IMPORTANT: the permit release MUST be in finally, so a failure
        //         never leaks a permit.
        return null;
    }
}

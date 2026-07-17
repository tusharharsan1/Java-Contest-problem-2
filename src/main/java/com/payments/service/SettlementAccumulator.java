package com.payments.service;

import org.springframework.stereotype.Component;

/**
 * Shared across ALL worker threads during a batch run.
 * Multiple threads will call addSettledAmount(...) at the SAME time,
 * so the updates below MUST be made thread-safe.
 */
@Component
public class SettlementAccumulator {

    // Shared mutable state — accessed by many threads concurrently.
    private double totalSettled = 0.0;
    private int settledCount = 0;

    // TODO 1: (Optional) declare a Lock here if you prefer explicit locking
    //         over the synchronized keyword. Either approach is acceptable.

    public void addSettledAmount(double amount) {
        // TODO 2: void addSettledAmount(double amount)
        //         - Add "amount" to totalSettled AND increment settledCount.
        //         - This MUST be thread-safe: if 8 threads call this at once,
        //           NO update may be lost.
        //         (Hint: synchronized method / synchronized block / Lock.)
    }

    public double getTotalSettled() {
        // TODO 3: double getTotalSettled()
        //         - Return the current total safely.
        return 0.0;
    }

    public int getSettledCount() {
        // TODO 4: int getSettledCount()
        //         - Return the current count safely.
        return 0;
    }
}

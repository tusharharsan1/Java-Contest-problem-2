package com.payments.service;

import org.springframework.stereotype.Service;

/**
 * Shared across ALL worker threads during a batch run.
 * Multiple threads will call addSettledAmount(...) at the SAME time,
 * so the updates below MUST be made thread-safe.
 */
@Service
public class SettlementAccumulator {

    private double totalSettled = 0.0;
    private int settledCount = 0;

    public synchronized void addSettledAmount(double amount) {
        this.totalSettled += amount;
        this.settledCount++;
    }

    public synchronized double getTotalSettled() {
        return totalSettled;
    }

    public synchronized int getSettledCount() {
        return settledCount;
    }
}

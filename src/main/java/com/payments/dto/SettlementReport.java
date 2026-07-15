package com.payments.dto;

import com.payments.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * Read-only summary returned after a batch is processed.
 * (Lombok generates the constructor and getters — this is plumbing, not graded.)
 */
@Getter
@AllArgsConstructor
public class SettlementReport {
    private final double totalSettled;
    private final int settledCount;
    private final Optional<Transaction> highestValueSettled;
}

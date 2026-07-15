package com.payments.model;

import com.payments.exception.InvalidTransactionException;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A single payment transaction.
 *
 * This class must be IMMUTABLE:
 *   - the class is declared final (already done for you)
 *   - every field is final
 *   - there are NO setters
 *   - "settling" a transaction returns a NEW Transaction, it never changes this one.
 */
public final class Transaction {

    private static final AtomicInteger counter = new AtomicInteger(0);

    private final String transactionId;
    private final double amount;
    private final TransactionStatus status;

    public Transaction(double amount) {
        if (amount < 0) {
            throw new InvalidTransactionException("Amount cannot be negative");
        }
        this.transactionId = "T-" + counter.incrementAndGet();
        this.amount = amount;
        this.status = TransactionStatus.PENDING;
    }

    private Transaction(String transactionId, double amount, TransactionStatus status) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.status = status;
    }

    public Transaction settled() {
        return new Transaction(this.transactionId, this.amount, TransactionStatus.SETTLED);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                '}';
    }
}

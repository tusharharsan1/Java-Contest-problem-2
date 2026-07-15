package com.payments.model;

import com.payments.exception.InvalidTransactionException;

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

    // TODO 1: Declare a static counter (shared across all transactions),
    //         starting at 0. It will be used to generate unique IDs.

    // TODO 2: Declare the fields, all final:
    //         - transactionId : String
    //         - amount        : double
    //         - status        : TransactionStatus

    public Transaction(double amount) {
        // TODO 3: PUBLIC constructor(amount)
        //         - If amount is negative -> throw InvalidTransactionException.
        //         - Increment the static counter and build the transactionId
        //           in the format "T-1", "T-2", ...
        //         - Set amount, and set status to PENDING.
    }

    private Transaction(String transactionId, double amount, TransactionStatus status) {
        // TODO 4: PRIVATE constructor(transactionId, amount, status)
        //         - Used ONLY internally to build a settled copy.
        //         - Simply assigns the given values to the final fields.
        //         (This one does NOT touch the counter and does NOT validate again.)
    }

    public Transaction settled() {
        // TODO 5: public Transaction settled()
        //         - Return a NEW Transaction that has:
        //             the SAME transactionId,
        //             the SAME amount,
        //             but status = SETTLED.
        //         - IMPORTANT: do not modify "this" object in any way.
        //         (Hint: use the private constructor from TODO 4.)
        return null;
    }

    public String getTransactionId() {
        // TODO 6: Getters for transactionId, amount, status.
        return null;
    }

    public double getAmount() {
        // TODO 6: Getters for transactionId, amount, status.
        return 0.0;
    }

    public TransactionStatus getStatus() {
        // TODO 6: Getters for transactionId, amount, status.
        return null;
    }

    @Override
    public boolean equals(Object o) {
        // TODO 7: Override equals() and hashCode()
        //         - Two Transactions are equal ONLY if their transactionId matches.
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        // TODO 7: Override equals() and hashCode()
        return super.hashCode();
    }
}

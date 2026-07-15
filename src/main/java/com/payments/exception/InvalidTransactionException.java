package com.payments.exception;

// Unchecked exception: thrown when a transaction amount is invalid (negative).
public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}

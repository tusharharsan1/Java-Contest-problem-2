package com.payments.exception;

// CHECKED exception: thrown when settlement at the bank gateway fails
// (e.g. the worker thread is interrupted mid-settlement).
// Note: this extends Exception (not RuntimeException) — so any method that
// can throw it must declare it with "throws" or handle it with try-catch.
public class GatewayException extends Exception {
    public GatewayException(String message) {
        super(message);
    }
}

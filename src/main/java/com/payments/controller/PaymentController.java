package com.payments.controller;

import com.payments.dto.SettlementReport;
import com.payments.model.Batch;
import com.payments.model.Transaction;
import com.payments.service.PaymentBatchProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Thin layer that just forwards calls to the PaymentBatchProcessor.
 * It must contain NO business logic of its own.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentBatchProcessor processor;

    public PaymentController(PaymentBatchProcessor processor) {
        this.processor = processor;
    }

    @PostMapping("/batch")
    public SettlementReport processBatch(@RequestBody Batch<Transaction> batch) {
        return processor.processBatch(batch);
    }
}

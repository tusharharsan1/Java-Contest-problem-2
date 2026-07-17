package com.payments.controller;

import com.payments.dto.SettlementReport;
import com.payments.model.Batch;
import com.payments.model.Transaction;
import com.payments.service.PaymentBatchProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Thin layer that just forwards calls to the PaymentBatchProcessor.
 * It must contain NO business logic of its own.
 */
// NOTE: We have annotated this class for you as a REST controller.
@RestController
public class PaymentController {

    private final PaymentBatchProcessor processor;

    // NOTE: We have declared and injected the PaymentBatchProcessor for you.
    public PaymentController(PaymentBatchProcessor processor) {
        this.processor = processor;
    }

    // NOTE: We have added the necessary Spring web annotations for you.
    @PostMapping("/process")
    public SettlementReport processBatch(@RequestBody Batch<Transaction> batch) {
        return processor.processBatch(batch);
    }
}

package com.payments.controller;

import com.payments.dto.SettlementReport;
import com.payments.model.Batch;
import com.payments.model.Transaction;
import com.payments.service.PaymentBatchProcessor;
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

    public SettlementReport processBatch(Batch<Transaction> batch) {
        // TODO 1: processBatch(Batch<Transaction> batch) -> delegate to the processor.

        // NOTE: This method should be ONE line that calls the matching method
        //       on the processor. No loops, no threads, no logic here.
        return null;
    }
}

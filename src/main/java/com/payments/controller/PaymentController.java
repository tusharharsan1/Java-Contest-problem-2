package com.payments.controller;

import com.payments.dto.SettlementReport;
import com.payments.model.Batch;
import com.payments.model.Transaction;
import com.payments.service.PaymentBatchProcessor;

/**
 * Thin layer that just forwards calls to the PaymentBatchProcessor.
 * It must contain NO business logic of its own.
 */
// TODO 1: Annotate this class so Spring treats it as a REST controller.
public class PaymentController {

    // TODO 2: Declare a PaymentBatchProcessor field and inject it via the
    //         CONSTRUCTOR (constructor injection, so Spring auto-wires it).

    public SettlementReport processBatch(Batch<Transaction> batch) {
        // TODO 3: processBatch(Batch<Transaction> batch) -> delegate to the processor.

        // NOTE: This method should be ONE line that calls the matching method
        //       on the processor. No loops, no threads, no logic here.
        return null;
    }
}

package com.payments.service;

import com.payments.dto.SettlementReport;
import com.payments.model.Batch;
import com.payments.model.Transaction;

import java.util.List;

/**
 * The heart of the system.
 * Splits a batch of transactions across worker threads, settles them in
 * parallel through the BankGateway, safely accumulates the results, and
 * returns a SettlementReport.
 */
// TODO 1: Annotate this class so Spring treats it as a service component.
public class PaymentBatchProcessor {

    // TODO 2: Declare two final fields (composition — this class HAS-A each):
    //         - BankGateway gateway
    //         - SettlementAccumulator accumulator

    public PaymentBatchProcessor(BankGateway gateway, SettlementAccumulator accumulator) {
        // TODO 3: Constructor(BankGateway gateway, SettlementAccumulator accumulator)
        //         - Assign both fields (constructor injection).
    }

    public SettlementReport processBatch(Batch<Transaction> batch) {
        // TODO 4: public SettlementReport processBatch(Batch<Transaction> batch)
        //         Follow these steps:
        //
        //         1. Find the number of CPU cores:
        //               int cores = Runtime.getRuntime().availableProcessors();
        //
        //         2. Create a FIXED thread pool of size "cores":
        //               ExecutorService pool = Executors.newFixedThreadPool(cores);
        //
        //         3. Split the batch into chunks:
        //               List<List<Transaction>> chunks = batch.getChunks(cores);
        //
        //         4. For EACH chunk, build a Callable<Double> task (a lambda) that:
        //               - loops over the transactions in that chunk
        //               - calls gateway.settle(txn) for each   (handle GatewayException)
        //               - calls accumulator.addSettledAmount(settledTxn.getAmount())
        //               - keeps a running subtotal for the chunk
        //               - returns that subtotal
        //
        //         5. SUBMIT ALL tasks FIRST (collect the Future<Double> objects in a list).
        //            Do NOT call get() inside this loop — submit everything before
        //            you start collecting, so the work runs in parallel.
        //
        //         6. Then COLLECT the results:
        //               - in a try block, loop over the futures and call future.get()
        //               - catch InterruptedException / ExecutionException and handle it
        //               - in a FINALLY block, ALWAYS call pool.shutdown()
        //
        //         7. Find the highest-value SETTLED transaction using Streams,
        //            returning it as an Optional<Transaction>.
        //            (Empty batch -> Optional.empty())
        //
        //         8. Build and return a SettlementReport containing:
        //               accumulator.getTotalSettled(),
        //               accumulator.getSettledCount(),
        //               the Optional<Transaction> from step 7.
        return null;
    }

    public double aggregateSubtotals(List<? extends Number> subtotals) {
        // TODO 5: public double aggregateSubtotals(List<? extends Number> subtotals)
        //         - Sum all the numbers in the list using Streams.
        //         - The wildcard "? extends Number" lets this accept a List<Integer>,
        //           a List<Double>, etc.
        //         (Hint: map each element with Number::doubleValue, then sum.)
        return 0.0;
    }
}

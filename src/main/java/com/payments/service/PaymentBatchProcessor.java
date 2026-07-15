package com.payments.service;

import com.payments.dto.SettlementReport;
import com.payments.exception.GatewayException;
import com.payments.model.Batch;
import com.payments.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * The heart of the system.
 * Splits a batch of transactions across worker threads, settles them in
 * parallel through the BankGateway, safely accumulates the results, and
 * returns a SettlementReport.
 */
@Service
public class PaymentBatchProcessor {

    private final BankGateway gateway;
    private final SettlementAccumulator accumulator;

    public PaymentBatchProcessor(BankGateway gateway, SettlementAccumulator accumulator) {
        this.gateway = gateway;
        this.accumulator = accumulator;
    }

    public SettlementReport processBatch(Batch<Transaction> batch) {
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(cores);

        List<List<Transaction>> chunks = batch.getChunks(cores);
        List<Future<Double>> futures = new ArrayList<>();

        for (List<Transaction> chunk : chunks) {
            Callable<Double> task = () -> {
                double subtotal = 0.0;
                for (Transaction txn : chunk) {
                    try {
                        Transaction settledTxn = gateway.settle(txn);
                        accumulator.addSettledAmount(settledTxn.getAmount());
                        subtotal += settledTxn.getAmount();
                    } catch (GatewayException e) {
                        // ignore and continue for other transactions
                    }
                }
                return subtotal;
            };
            futures.add(pool.submit(task));
        }

        try {
            for (Future<Double> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        } finally {
            pool.shutdown();
        }

        Optional<Transaction> highestValueSettled = batch.getItems().stream()
                .max((t1, t2) -> Double.compare(t1.getAmount(), t2.getAmount()))
                .map(Transaction::settled);

        return new SettlementReport(
                accumulator.getTotalSettled(),
                accumulator.getSettledCount(),
                highestValueSettled
        );
    }

    public double aggregateSubtotals(List<? extends Number> subtotals) {
        return subtotals.stream()
                .mapToDouble(Number::doubleValue)
                .sum();
    }
}

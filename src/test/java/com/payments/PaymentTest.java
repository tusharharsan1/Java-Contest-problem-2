package com.payments;

import com.payments.controller.PaymentController;
import com.payments.dto.SettlementReport;
import com.payments.exception.GatewayException;
import com.payments.exception.InvalidTransactionException;
import com.payments.model.Batch;
import com.payments.model.Transaction;
import com.payments.model.TransactionStatus;
import com.payments.service.BankGateway;
import com.payments.service.PaymentBatchProcessor;
import com.payments.service.SettlementAccumulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentTest {

    // ==========================================
    // 1. MODEL & IMMUTABILITY TESTS
    // ==========================================

    @Test
    void testTransactionImmutability() {
        Transaction tx = new Transaction(100.0);
        assertEquals(TransactionStatus.PENDING, tx.getStatus());
        assertTrue(Modifier.isFinal(Transaction.class.getModifiers()), "Transaction class should be final");

        for (Field field : Transaction.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                assertTrue(Modifier.isFinal(field.getModifiers()), "Field " + field.getName() + " should be final");
            }
        }

        Transaction settledTx = tx.settled();
        assertNotSame(tx, settledTx, "settled() should return a new object");
        assertEquals(TransactionStatus.SETTLED, settledTx.getStatus());
        assertEquals(tx.getTransactionId(), settledTx.getTransactionId());
        assertEquals(tx.getAmount(), settledTx.getAmount());
        assertEquals(TransactionStatus.PENDING, tx.getStatus());
    }

    @Test
    void testTransactionInvalidAmount() {
        assertThrows(InvalidTransactionException.class, () -> new Transaction(-10.0));
    }

    @Test
    void testBatchChunks() {
        List<Transaction> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) items.add(new Transaction(10.0));
        Batch<Transaction> batch = new Batch<>(items);

        List<List<Transaction>> chunks = batch.getChunks(3);
        assertEquals(3, chunks.size(), "Should split into exactly 3 chunks");
        assertEquals(10, chunks.stream().mapToInt(List::size).sum(), "Total items across chunks should be exactly 10");
        assertTrue(chunks.get(0).size() >= chunks.get(2).size(), "Chunks should be evenly distributed");
    }

    @Test
    void testBatchChunksEdgeCases() {
        List<Transaction> items = Arrays.asList(new Transaction(10.0), new Transaction(20.0));
        Batch<Transaction> batch = new Batch<>(items);

        // Anti-bypass: What if chunk count is greater than item size?
        List<List<Transaction>> oversizedChunks = batch.getChunks(5);
        assertEquals(2, oversizedChunks.size(), "Should not create empty chunks if items < chunkCount");

        // Anti-bypass: What if chunk count is 0 or negative?
        List<List<Transaction>> zeroChunks = batch.getChunks(0);
        assertTrue(zeroChunks.isEmpty(), "Should handle 0 or negative chunk counts gracefully");
    }

    // ==========================================
    // 2. CONCURRENCY & THREAD SAFETY TESTS
    // ==========================================

    @Test
    void testSettlementAccumulatorThreadSafety() throws InterruptedException {
        SettlementAccumulator acc = new SettlementAccumulator();
        int threads = 10;
        int additionsPerThread = 1000;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                for (int j = 0; j < additionsPerThread; j++) {
                    acc.addSettledAmount(10.0);
                }
                latch.countDown();
            });
        }

        latch.await();
        pool.shutdown();

        assertEquals(threads * additionsPerThread * 10.0, acc.getTotalSettled(), "Updates to shared total should not be lost");
        assertEquals(threads * additionsPerThread, acc.getSettledCount(), "Updates to shared count should not be lost");
    }

    @Test
    void testBankGatewayReleasesPermitOnInterrupt() throws GatewayException, InterruptedException {
        BankGateway gateway = new BankGateway(1);
        Transaction tx = new Transaction(50.0);

        Thread t = new Thread(() -> {
            try {
                gateway.settle(tx);
            } catch (GatewayException e) {
                // Expected interruption exception
            }
        });

        t.start();
        t.interrupt();
        t.join();

        // If permit wasn't released inside finally, the next acquire will block indefinitely.
        assertDoesNotThrow(() -> {
            CompletableFuture.supplyAsync(() -> {
                try {
                    return gateway.settle(new Transaction(20.0));
                } catch (GatewayException e) {
                    return null;
                }
            }).get(2, TimeUnit.SECONDS);
        });
    }

    // ==========================================
    // 3. PROCESSOR LOGIC & BYPASS PREVENTION
    // ==========================================

    @Test
    void testPaymentBatchProcessorActuallyUsesMultipleThreads() throws GatewayException {
        // Anti-bypass: Ensure the student didn't just write a sequential for-loop in the main thread
        int cores = Runtime.getRuntime().availableProcessors();
        if (cores < 2) return; // Cannot test parallelism on a single-core machine

        BankGateway mockGateway = mock(BankGateway.class);
        SettlementAccumulator mockAccumulator = mock(SettlementAccumulator.class);
        PaymentBatchProcessor processor = new PaymentBatchProcessor(mockGateway, mockAccumulator);

        List<Transaction> items = new ArrayList<>();
        for (int i = 0; i < cores * 2; i++) {
            items.add(new Transaction(100.0));
        }

        Set<String> executingThreads = ConcurrentHashMap.newKeySet();

        when(mockGateway.settle(any(Transaction.class))).thenAnswer(inv -> {
            executingThreads.add(Thread.currentThread().getName());
            return ((Transaction) inv.getArgument(0)).settled();
        });

        processor.processBatch(new Batch<>(items));

        assertFalse(executingThreads.contains(Thread.currentThread().getName()),
                "Processing should not happen on the main thread");
        assertTrue(executingThreads.size() > 1,
                "Should use multiple threads from an ExecutorService");
    }

    @Test
    void testProcessorContinuesOnGatewayException() throws GatewayException {
        // Anti-bypass: Ensure a single failed transaction doesn't crash the entire batch/chunk
        BankGateway mockGateway = mock(BankGateway.class);
        SettlementAccumulator mockAccumulator = mock(SettlementAccumulator.class);
        PaymentBatchProcessor processor = new PaymentBatchProcessor(mockGateway, mockAccumulator);

        Batch<Transaction> batch = new Batch<>(Arrays.asList(
                new Transaction(10.0),
                new Transaction(20.0) // This one will pass
        ));

        // First call throws exception, second call succeeds
        when(mockGateway.settle(any(Transaction.class)))
                .thenThrow(new GatewayException("Connection Timeout"))
                .thenAnswer(inv -> ((Transaction) inv.getArgument(0)).settled());

        processor.processBatch(batch);

        // Verify the accumulator was still called for the successful transaction
        verify(mockAccumulator, times(1)).addSettledAmount(anyDouble());
    }

    @Test
    void testProcessorHandlesEmptyBatchGracefully() {
        BankGateway mockGateway = mock(BankGateway.class);
        SettlementAccumulator mockAccumulator = mock(SettlementAccumulator.class);
        PaymentBatchProcessor processor = new PaymentBatchProcessor(mockGateway, mockAccumulator);

        when(mockAccumulator.getTotalSettled()).thenReturn(0.0);
        when(mockAccumulator.getSettledCount()).thenReturn(0);

        SettlementReport report = processor.processBatch(new Batch<>(new ArrayList<>()));

        assertEquals(0.0, report.getTotalSettled());
        assertEquals(0, report.getSettledCount());
        assertFalse(report.getHighestValueSettled().isPresent(), "Highest value should be empty for empty batch");
    }

    @Test
    void testAggregateSubtotalsWithWildcard() {
        PaymentBatchProcessor processor = new PaymentBatchProcessor(null, null);
        List<Integer> ints = Arrays.asList(1, 2, 3);
        List<Double> doubles = Arrays.asList(1.5, 2.5);

        assertEquals(6.0, processor.aggregateSubtotals(ints));
        assertEquals(4.0, processor.aggregateSubtotals(doubles));
    }

    // ==========================================
    // 4. CONTROLLER DELEGATION
    // ==========================================

    @Test
    void testControllerDelegation() {
        PaymentBatchProcessor mockProcessor = mock(PaymentBatchProcessor.class);
        PaymentController controller = new PaymentController(mockProcessor);

        Batch<Transaction> batch = new Batch<>(new ArrayList<>());
        SettlementReport expected = new SettlementReport(0, 0, Optional.empty());
        when(mockProcessor.processBatch(batch)).thenReturn(expected);

        SettlementReport actual = controller.processBatch(batch);

        assertEquals(expected, actual);
        verify(mockProcessor, times(1)).processBatch(batch);
    }
}
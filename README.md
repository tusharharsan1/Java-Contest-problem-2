# Concurrent Payment Batch Processor

## Problem Statement

You are building the core backend for a payment processing platform (like Razorpay or Stripe). Merchants submit large batches of transactions that must be processed as fast as possible by splitting the work across multiple worker threads. Each transaction is validated and settled independently, and the system aggregates the total settled amount once all workers finish.

Because payments run concurrently, correctness under parallel execution is critical: two threads updating the shared settlement total at the same time must never corrupt the running total, and the system must never open more simultaneous connections to the downstream bank gateway than the gateway allows. A miscount in the settled total, a lost transaction update, or exceeding the gateway's concurrent-connection limit must never occur.

The system splits each batch into chunks, processes the chunks in parallel using a thread pool, safely accumulates the results, and returns an aggregated settlement report. A limited number of "gateway connection permits" controls how many transactions may contact the bank gateway at the same time.

## Tasks

### Task 1 — Transaction Model & Generic Batch Container

Complete `Transaction` and the generic container `Batch<T>`. It must:

- Represent a `Transaction` with an immutable `transactionId` (auto-generated via a static counter formatted as `"T-1"`, `"T-2"`, etc.), an `amount`, and a `status` (`PENDING` / `SETTLED` / `FAILED`).
- Throw an `InvalidTransactionException` (unchecked) if the `amount` is negative during construction.
- Make `Transaction` immutable: all fields `final`, no setters, the class declared `final`. Settling a transaction must produce a new `Transaction` object (a settled copy), never mutate the original.
- Implement the generic class `Batch<T>` that holds a `List<T>` of items and exposes `size()`, `getItems()`, and a method `getChunks(int chunkCount)` that splits the batch into roughly equal sub-lists for parallel processing.
- Determine equality between two transactions strictly by their `transactionId`.

### Task 2 — Concurrent Settlement Accumulator (Thread-Safety)

Implement `SettlementAccumulator`. It must:

- Maintain a shared running total of the settled amount and a shared count of settled transactions.
- Provide a method `addSettledAmount(double amount)` that is thread-safe — multiple worker threads will call it concurrently. Protect the shared state so no update is ever lost (use `synchronized` or an explicit `Lock`).
- Expose `getTotalSettled()` and `getSettledCount()` returning consistent, non-corrupted values after all workers finish.

### Task 3 — Gateway Permit Control (Semaphore)

Complete `BankGateway`. It must:

- Accept a maximum number of concurrent connections in its constructor and enforce it using a counting `Semaphore`.
- Implement `settle(Transaction txn)` which: acquires a permit (blocking if the limit is reached), simulates contacting the bank (a short `Thread.sleep`), returns a settled copy of the transaction, and always releases the permit in a `finally` block — even if an exception is thrown mid-settlement.
- Throw a checked `GatewayException` if settlement is interrupted, and ensure the permit is still released.

### Task 4 — Parallel Batch Processing Engine (Executors + Callable + Future)

Implement `PaymentBatchProcessor`. It must:

- Use composition: hold a `BankGateway` and a `SettlementAccumulator` as fields (constructor-injected).
- Implement `SettlementReport processBatch(Batch<Transaction> batch)` which:
  - Creates a fixed thread pool sized to `Runtime.getRuntime().availableProcessors()`.
  - Splits the batch into chunks (one per worker).
  - Wraps each chunk in a `Callable<Double>` task that settles every transaction in its chunk (via the gateway), adds each settled amount to the shared `SettlementAccumulator`, and returns that chunk's subtotal.
  - Submits all tasks first, then collects results via `Future.get()` (submit-first / collect-last).
  - Uses `try-catch-finally` around the collection loop and always calls `shutdown()` on the executor in the `finally` block.
  - Returns a `SettlementReport` (total settled, settled count, and the `Optional<Transaction>` of the highest-value settled transaction, found via Streams).
- Implement `aggregateSubtotals(List<? extends Number> subtotals)` — a method using a wildcard (`? extends Number`) that sums a list of chunk subtotals using Streams.

*(Note: Spring annotations like `@Service` are already provided for you in the starter code).*

### Task 5 — Controller Delegation (Provided)

The `PaymentController` has already been implemented and annotated for you. It serves as a thin REST layer delegating directly to the `PaymentBatchProcessor` without holding any business logic. You do not need to modify this file, but you can review it to understand the entry point of the API.

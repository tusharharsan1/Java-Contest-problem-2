package com.payments.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic container holding a batch of items of any type T.
 * We use it as Batch<Transaction>, but it must work for any type.
 */
public class Batch<T> {

    private final List<T> items;

    public Batch(List<T> items) {
        this.items = items;
    }

    public int size() {
        return items.size();
    }

    public List<T> getItems() {
        return items;
    }

    public List<List<T>> getChunks(int chunkCount) {
        List<List<T>> chunks = new ArrayList<>();
        if (chunkCount <= 0 || items.isEmpty()) {
            return chunks;
        }

        int totalItems = items.size();
        int baseSize = totalItems / chunkCount;
        int remainder = totalItems % chunkCount;

        int start = 0;
        for (int i = 0; i < chunkCount; i++) {
            if (start >= totalItems) break;

            int end = start + baseSize + (i < remainder ? 1 : 0);
            chunks.add(items.subList(start, end));
            start = end;
        }
        return chunks;
    }
}

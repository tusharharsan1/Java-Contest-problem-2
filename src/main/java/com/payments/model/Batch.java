package com.payments.model;

import java.util.List;

/**
 * A generic container holding a batch of items of any type T.
 * We use it as Batch<Transaction>, but it must work for any type.
 */
public class Batch<T> {

    // TODO 1: Declare a final field:  List<T> items

    public Batch(List<T> items) {
        // TODO 2: Constructor(List<T> items) -> store the list.
    }

    public int size() {
        // TODO 3: public int size()
        //         - Return the number of items in the batch.
        return 0;
    }

    public List<T> getItems() {
        // TODO 4: public List<T> getItems()
        //         - Return the items list.
        return null;
    }

    public List<List<T>> getChunks(int chunkCount) {
        // TODO 5: public List<List<T>> getChunks(int chunkCount)
        //         - Split "items" into chunkCount roughly-equal sub-lists.
        //         - Example: 10 items, chunkCount = 3  ->  [4 items], [3 items], [3 items]
        //           (exact split can vary, just make sure NO item is lost
        //            and the combined size of all chunks equals size()).
        //         - Each returned sub-list will later be handed to one worker thread.
        //         (Hint: List.subList(from, to) is useful here.)
        return null;
    }
}

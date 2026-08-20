package org.whoodle.sinks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ParallelSinkCollector<T> implements Collector<T, List<T>, Void> {
    // TODO: review this skeleton implementation of a Sink as a terminator of Stream chain from Gemini
    // 1. LIFECYCLE: Initialization (Thread-local container setup)
    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    // 2. PROCESSING: Accumulate items into the local thread buffer
    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return List::add;
    }

    // 3. PARALLELISM: Merge buffers from different threads safely
    @Override
    public BinaryOperator<List<T>> combiner() {
        return (left, right) -> {
            left.addAll(right);
            return left;
        };
    }

    // 4. LIFECYCLE: Terminal hook to flush data and close resources
    @Override
    public Function<List<T>, Void> finisher() {
        return finalBatch -> {
            if (!finalBatch.isEmpty()) {
                flushToExternalSystem(finalBatch);
            }
            closeResources();
            return null; // Return void since the value isn't needed
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        // Do NOT include IDENTITY_FINISH because we have custom finisher logic
        return Collections.emptySet();
    }

    private void flushToExternalSystem(List<T> data) {
        System.out.println("Thread " + Thread.currentThread().getName()
                + " flushing " + data.size() + " items to external system.");
        // Your database write, file append, or network push logic goes here
    }

    private void closeResources() {
        System.out.println("Lifecycle complete. Resources closed cleanly.");
    }
}

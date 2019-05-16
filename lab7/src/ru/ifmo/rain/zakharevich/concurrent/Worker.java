package ru.ifmo.rain.zakharevich.concurrent;

import java.util.function.Function;
import java.util.stream.Stream;

class Worker<T, R> {
    private R result;
    private final Thread thread;

    Worker(final Function<Stream<? extends T>, R> map, Stream<? extends T> elements) {
        this.thread = new Thread(() -> result = map.apply(elements));
        this.thread.start();
    }

    R getResult() throws InterruptedException {
        thread.join();
        return result;
    }
}
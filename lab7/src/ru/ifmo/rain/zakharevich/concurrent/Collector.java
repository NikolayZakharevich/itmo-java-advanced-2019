package ru.ifmo.rain.zakharevich.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

class Collector<T> {
    private final List<T> values;
    private final Function<? super Stream<T>, T> collector;

    Collector(Function<? super Stream<T>, T> collector) {
        this.collector = collector;
        values = new ArrayList<>();
    }

    void add(T value) {
        values.add(value);
    }

    T collect() {
        return collector.apply(values.stream());
    }
}

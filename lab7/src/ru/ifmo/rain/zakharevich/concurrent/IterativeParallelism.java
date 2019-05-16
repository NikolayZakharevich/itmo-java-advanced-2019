package ru.ifmo.rain.zakharevich.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IterativeParallelism implements ListIP {

    private ParallelMapper parallelMapper;

    public IterativeParallelism() {
        this.parallelMapper = null;
    }

    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    @Override
    public String join(int threads, List<?> elements) throws InterruptedException {
        return process(threads, elements,
                s -> s.map(Object::toString).collect(Collectors.joining()),
                s -> s.collect(Collectors.joining()) );
    }

    @Override
    public <T> List<T> filter(int threads, List<? extends T> elements, Predicate<? super T> predicate) throws InterruptedException {
        return process(threads, elements,
                s -> s.filter(predicate).collect(Collectors.toList()),
                s -> s.flatMap(List::stream).collect(Collectors.toList()));
    }

    @Override
    public <T, U> List<U> map(int threads, List<? extends T> elements, Function<? super T, ? extends U> mapper) throws InterruptedException {
        return process(threads, elements,
                s -> s.map(mapper).collect(Collectors.toList()),
                s -> s.flatMap(List::stream).collect(Collectors.toList()));
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> elements, Comparator<? super T> comparator) throws InterruptedException {
        Function<Stream<? extends T>, T> function = s -> s.max(comparator).orElse(null);
        return process(threads, elements, function, function);
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> elements, Comparator<? super T> comparator) throws InterruptedException {
        Function<Stream<? extends T>, T> function = s -> s.min(comparator).orElse(null);
        return process(threads, elements, function, function);
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> elements, Predicate<? super T> predicate) throws InterruptedException {
        return process(threads, elements,
                s -> s.allMatch(predicate),
                s -> s.allMatch(val -> val));
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> elements, Predicate<? super T> predicate) throws InterruptedException {
        return process(threads, elements,
                s -> s.anyMatch(predicate),
                s -> s.anyMatch(val -> val));
    }

    private <T, R> R process(int threads, final List<? extends T> elements,
                            final Function<Stream<? extends T>, R> function,
                            final Function<? super Stream<R>, R> collect) throws InterruptedException {
        if (threads <= 0) {
            throw new IllegalArgumentException("Threads number must be positive");
        }
        if (parallelMapper != null) {
            return collect.apply(parallelMapper.map(function, split(threads, elements)).stream());
        }

        Collector<R> collector = new Collector<>(collect);
        List<Stream<? extends T>> tasks = split(threads, elements);
        List<Worker<T, R>> workers = new ArrayList<>();
        for (Stream<? extends T> task : tasks) {
            workers.add(new Worker<>(function, task));
        }
        for (Worker<T, R> worker : workers) {
            collector.add(worker.getResult());
        }

        return collector.collect();
    }

    private static <T> List<Stream<? extends T>> split(final int parts, final List<? extends T> elements) {
        int elementsSize = elements.size();
        int batchSize = elementsSize / parts;
        List<Stream<? extends T>> groupedElements = new ArrayList<>();

        int index = 0;
        int tailSize = elementsSize - batchSize * parts;
        while (index < elementsSize) {
            int tempBatchSize = batchSize;
            if (tailSize > 0) {
                tempBatchSize++;
                tailSize--;
            }
            groupedElements.add(elements.subList(index, index + tempBatchSize).stream());
            index += tempBatchSize;
        }

        return groupedElements;
    }
}

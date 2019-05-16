package ru.ifmo.rain.zakharevich.mapper;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {

    private final List<Thread> threads;
    private final Queue<Runnable> tasks;

    public ParallelMapperImpl(int threads) {
        this.threads = new ArrayList<>(threads);
        this.tasks = new LinkedList<>();

        while (threads-- > 0) {
            this.threads.add(new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        Runnable task;
                        synchronized (tasks) {
                            while (tasks.isEmpty()) {
                                tasks.wait();
                            }
                            task = tasks.poll();
                        }
                        if (task != null) {
                            task.run();
                        }
                    }
                } catch (InterruptedException ignored) {
                }
            }));
        }
        apply(Thread::start);
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        int elementsSize = args.size();
        List<R> result = new ArrayList<>(Collections.nCopies(elementsSize, null));
        Counter counter = new Counter(elementsSize);
        for (int index = 0; index < elementsSize; index++) {
            final int elementIndex = index;
            synchronized (tasks) {
                tasks.add(() -> {
                    result.set(elementIndex, f.apply(args.get(elementIndex)));
                    synchronized (counter) {
                        counter.inc();
                    }
                });
                tasks.notify();
            }
        }

        synchronized (counter) {
            while (!counter.isReady()) {
                counter.wait();
            }
        }

        return result;
    }

    @Override
    public void close() {
        apply(Thread::interrupt);
        apply(t -> {
            try {
                t.join();
            } catch (InterruptedException ignored) {
            }
        });
    }

    private void apply(Consumer<? super Thread> action) {
        threads.forEach(action);
    }

    private final class Counter {
        private int done;
        private int tasksNumber;

        Counter(int tasksNumber) {
            this.tasksNumber = tasksNumber;
        }

        void inc() {
            if (++done == tasksNumber) {
                this.notifyAll();
            }
        }

        private boolean isReady() {
            return done == tasksNumber;
        }
    }
}

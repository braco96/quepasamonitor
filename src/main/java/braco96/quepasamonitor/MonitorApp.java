package braco96.quepasamonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Proyecto: QuePasaMonitor
 * Autor: braco96
 *
 * Idea: monitor (lock + condiciones) para un buffer acotado con múltiples
 * productores y consumidores. Demuestra exclusión mutua, espera-condición
 * y ausencia de indeterminación en el acceso a datos compartidos.
 */
public class MonitorApp {

    public static void main(String[] args) throws Exception {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(8);

        List<Thread> all = new ArrayList<>();
        // 3 productores
        for (int p = 0; p < 3; p++) {
            int id = p;
            Thread prod = new Thread(() -> produce(buffer, id), "Prod-" + id);
            all.add(prod);
        }
        // 2 consumidores
        for (int c = 0; c < 2; c++) {
            int id = c;
            Thread cons = new Thread(() -> consume(buffer, id), "Cons-" + id);
            all.add(cons);
        }

        all.forEach(Thread::start);
        for (Thread t : all) t.join();

        System.out.println("Ejecución finalizada.");
    }

    private static void produce(BoundedBuffer<Integer> buffer, int producerId) {
        try {
            for (int i = 0; i < 20; i++) {
                int value = producerId * 1000 + i;
                buffer.put(value);
                System.out.printf("→ %s produjo %d%n", Thread.currentThread().getName(), value);
                TimeUnit.MILLISECONDS.sleep(20);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void consume(BoundedBuffer<Integer> buffer, int consumerId) {
        try {
            for (int i = 0; i < 30; i++) {
                Integer value = buffer.take();
                System.out.printf("← %s consumió %d%n", Thread.currentThread().getName(), value);
                TimeUnit.MILLISECONDS.sleep(35);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ----------------- Monitor -----------------
    public static class BoundedBuffer<T> {
        private final Object[] data;
        private int head = 0, tail = 0, size = 0;

        private final ReentrantLock lock = new ReentrantLock(true); // justo (fair) para reducir hambre
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        public BoundedBuffer(int capacity) {
            this.data = new Object[capacity];
        }

        public void put(T item) throws InterruptedException {
            lock.lock();
            try {
                while (size == data.length) {
                    notFull.await();
                }
                data[tail] = item;
                tail = (tail + 1) % data.length;
                size++;
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        @SuppressWarnings("unchecked")
        public T take() throws InterruptedException {
            lock.lock();
            try {
                while (size == 0) {
                    notEmpty.await();
                }
                T item = (T) data[head];
                head = (head + 1) % data.length;
                size--;
                notFull.signal();
                return item;
            } finally {
                lock.unlock();
            }
        }

        public int size() {
            lock.lock();
            try {
                return size;
            } finally {
                lock.unlock();
            }
        }
    }
}
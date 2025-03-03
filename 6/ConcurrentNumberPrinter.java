import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class NumberSequencePrinter {
    public void printZero() {
        System.out.print("0");
    }

    public void printEven(int num) {
        System.out.print(num);
    }

    public void printOdd(int num) {
        System.out.print(num);
    }
}

class SequenceController {
    private final NumberSequencePrinter printer;
    private final Lock lock;
    private final Condition zeroCondition;
    private final Condition oddCondition;
    private final Condition evenCondition;
    private int currentState; // 0: zero, 1: odd, 2: even
    private int count;
    private final int limit;

    public SequenceController(int limit, NumberSequencePrinter printer) {
        this.printer = printer;
        this.lock = new ReentrantLock();
        this.zeroCondition = lock.newCondition();
        this.oddCondition = lock.newCondition();
        this.evenCondition = lock.newCondition();
        this.currentState = 0; // Start with zero
        this.count = 1;
        this.limit = limit;
    }

    public void printZero() throws InterruptedException {
        lock.lock();
        try {
            while (count <= limit) {
                if (currentState != 0) {
                    zeroCondition.await();
                }
                if (count > limit) break; // Exit if we've printed all numbers
                printer.printZero();
                currentState = (count % 2 == 1) ? 1 : 2; // Next state: odd or even
                if (currentState == 1) {
                    oddCondition.signal(); // Signal odd thread
                } else {
                    evenCondition.signal(); // Signal even thread
                }
                zeroCondition.await(); // Wait for next turn
            }
            // Signal all threads to exit
            oddCondition.signal();
            evenCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    public void printOdd() throws InterruptedException {
        lock.lock();
        try {
            while (count <= limit) {
                if (currentState != 1) {
                    oddCondition.await();
                }
                if (count > limit) break; // Exit if we've printed all numbers
                printer.printOdd(count);
                count++;
                currentState = 0; // Next state: zero
                zeroCondition.signal(); // Signal zero thread
                oddCondition.await(); // Wait for next turn
            }
        } finally {
            lock.unlock();
        }
    }

    public void printEven() throws InterruptedException {
        lock.lock();
        try {
            while (count <= limit) {
                if (currentState != 2) {
                    evenCondition.await();
                }
                if (count > limit) break; // Exit if we've printed all numbers
                printer.printEven(count);
                count++;
                currentState = 0; // Next state: zero
                zeroCondition.signal(); // Signal zero thread
                evenCondition.await(); // Wait for next turn
            }
        } finally {
            lock.unlock();
        }
    }
}

public class ConcurrentNumberPrinter {
    public static void main(String[] args) {
        int limit = 9;
        NumberSequencePrinter printer = new NumberSequencePrinter();
        SequenceController controller = new SequenceController(limit, printer);

        Thread zeroThread = new Thread(() -> {
            try {
                controller.printZero();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread oddThread = new Thread(() -> {
            try {
                controller.printOdd();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread evenThread = new Thread(() -> {
            try {
                controller.printEven();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        zeroThread.start();
        oddThread.start();
        evenThread.start();

        try {
            zeroThread.join();
            oddThread.join();
            evenThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
/*There is a CubbyHole that can keep a single integer variable. A producer thread writes (updates) integer values to the variable.
        A consumer thread reads out the value of this integer variable. When the comsumer reads the current integer value of the CubbyHole,
        it should mark that the content has been read. The producer should NOT write a new integer value until the previously written value has been read by the consumer.
        Similarly, the consumer should NOT read out the same value twice.

        Provide a multi-threaded solution to this producer-consumer problem.
*/

public class ProducerConsumerTest {
    public static void main(String[] args) {
        CubbyHole c = new CubbyHole();
        Producer p1 = new Producer(c, 1);
        Consumer c1 = new Consumer(c, 1);
        p1.start();
        c1.start();
    }	}
//=============================
class Producer extends Thread {
    private CubbyHole cubbyhole;
    private int number;

    public Producer(CubbyHole c, int number) {
        cubbyhole = c;
        this.number = number;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            cubbyhole.put(i);
            System.out.println("Producer #" + this.number	+ " put: " + i);
            try {
                sleep((int)(Math.random() * 100));
            } catch (InterruptedException e) { }
        }}}
//==========================
class Consumer extends Thread {
    private CubbyHole cubbyhole;
    private int number;
    public Consumer(CubbyHole c, int number) {
        cubbyhole = c;
        this.number = number;
    }
    public void run() {
        int value = 0;
        for (int i = 0; i < 10; i++) {
            value = cubbyhole.get();
            System.out.println("Consumer #" + this.number
                    + " got: " + value);
        }
    }
}
//============================

class CubbyHole {
    private int contents;
    private boolean available = false;
    public synchronized int get() {
        while(available == false) {
            try {
                wait();
            }
            catch (InterruptedException e) {
            }
        }
        available = false;
        notifyAll();
        return contents;
    }
    public synchronized void put(int value) {
        while (available == true) {
            try {
                wait();
            }
            catch (InterruptedException e) {
            }
        }
        contents = value;
        available = true;
        notifyAll();
    }	}
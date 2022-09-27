package src.readerwriter;

import java.util.concurrent.locks.ReentrantLock;

public class RWLock {
    
    private final ReentrantLock lock = new ReentrantLock(true);
    /* private boolean reading;
    private boolean writing; */
    
    public RWLock(){
        /* this.reading = false;
        this.writing = false; */
    }

    public /* synchronized */ void acquireRead() throws InterruptedException {
        lock.lock();
        /* while (reading || writing) {
            this.wait();
        } this.reading = true; */
    }

    public /* synchronized */ void acquireWrite() throws InterruptedException {
        lock.lock();
        /* while (reading) {
            this.wait();
        } this.reading = true; this.writing = true; */
    }

    public /* synchronized */ void releaseRead() {
        lock.unlock();
        /* this.reading = false;
        this.notify(); */
    }

    public /* synchronized */ void releaseWrite() {
        lock.unlock();
        /* this.reading = false;
        this.writing = false;
        this.notify(); */
    }
}

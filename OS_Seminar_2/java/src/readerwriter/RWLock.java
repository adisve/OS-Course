package src.readerwriter;

public class RWLock {

    private boolean locked;
    
    public RWLock(){
        this.locked = false;
    }

    public synchronized void acquireRead() throws InterruptedException {
        while (locked) {
            this.wait();
        } this.locked = true;
    }

    public synchronized void acquireWrite() throws InterruptedException {
        while (locked) {
            this.wait();
        } this.locked = true;
    }

    public synchronized void releaseRead(){
        this.locked = false;
        this.notify();
    }

    public synchronized void releaseWrite() {
        this.locked = false;
        this.notify();
    }
}

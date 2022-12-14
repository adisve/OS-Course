package src.philosophers;

public class Table {

	private int nbrOfChopsticks;
	private final boolean available = true;
	private final boolean unavailable = false;
	private boolean chopstick[];

	public Table(int nbrOfSticks) {
		nbrOfChopsticks = nbrOfSticks;
		chopstick = new boolean[nbrOfChopsticks];
		for (int i = 0; i < nbrOfChopsticks; i++) chopstick[i] = available;
	}

	public synchronized void getLeftChopstick(int i) throws InterruptedException {
		// Only allow a philosopher to pick up their left chopstick if
		// both of the chopsticks are currently available
		while(!chopstick[i] && !chopstick[(i+1) % chopstick.length]) this.wait();
		chopstick[i] = unavailable;
	}

	public synchronized void getRightChopstick(int n) throws InterruptedException {
		while(!chopstick[(n+1) % chopstick.length]) this.wait(); 
		chopstick[(n+1) % chopstick.length] = unavailable;
	}

	public synchronized void releaseLeftChopstick(int n) {
		chopstick[n] = available;
		this.notifyAll();
	}

	public synchronized void releaseRightChopstick(int n) {
		chopstick[(n+1) % chopstick.length] = available;
		this.notifyAll();
	}
}
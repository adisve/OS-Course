import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StopWatch {
    public static void main(String[] args) {
        try {

            // Inline declaration of thread function
            Thread t = new Thread(() -> {
                try {
                    _threadWorkerMethod();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            System.out.println("Main thread. Waiting for stopwatch thread...");
            t.start();  // Start second thread
            t.join();   // Join second thread with main thread
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void _threadWorkerMethod() throws InterruptedException {
        DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        format.setMaximumFractionDigits(2);
        float elapsedTime = 0;

        while(Math.floor(elapsedTime) != 500) {
            Thread.sleep(10);
            elapsedTime += 1;
            System.out.println("Stopwatch thread. Elapsed: " + format.format(elapsedTime/100.0) + " seconds.");
        }
        return;
    }
}


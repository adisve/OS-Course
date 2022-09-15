import java.io.*;
import java.util.*;
import java.util.logging.Logger;

class ExternalThreadWorker implements Runnable {
    final String command;
    final Logger logger;
    
    ExternalThreadWorker(String command, Logger logger) {
        this.command = command;
        this.logger = logger;
    }

    @Override
    public void run() {
        List<String> input = Arrays.asList(command.split(" "));
        ProcessBuilder processBuilder = new ProcessBuilder(input);
        BufferedReader bufferReader = null;

        try {
            Process proc = processBuilder.start();
            InputStream inputStream = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            bufferReader = new BufferedReader(isr);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                System.out.println(line );
            }
            bufferReader.close();

        } catch (java.io.IOException ioe) {
            System.err.println("Error");
            System.err.println(ioe);
            logger.info(ioe.getMessage());
            
        } finally {
            if (bufferReader != null) {
                try {
                    bufferReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

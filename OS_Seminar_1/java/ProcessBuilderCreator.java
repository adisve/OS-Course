import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class ProcessBuilderCreator {
    private static Logger logger = Logger.getLogger("ThreadErrorLogger");

    static void createProcess(String command, Logger logger) throws java.io.IOException {
        ExternalThreadWorker process = new ExternalThreadWorker(command, logger);
        Thread t = new Thread(process);
        t.start();
    }
    public static void main(String[] args) throws java.io.IOException {
        String cmdLineArgument;

        // External file handle
        FileHandler fh = new FileHandler("ThreadErrorLogger.log");
        fh.setFormatter(new SimpleFormatter());

        Logger.getLogger("ThreadErrorLogger").addHandler(fh);
        BufferedReader reader = new BufferedReader(new FileReader("ThreadErrorLogger.log"));

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n\n***** Welcome to the Java Command Shell *****");
        System.out.println("If you want to exit the shell, type END and press RETURN.\n");

        while (true) {
            System.out.print("jsh>");
            cmdLineArgument = scanner.nextLine();
            if (cmdLineArgument.equals("showerrlog")) {
                System.out.println("\nLIST OF ERROR LOGS\n");
                while(reader.readLine() != null)
                {
                    System.out.println(reader.readLine() + "\n");
                }
                continue;
            }
            if (cmdLineArgument.toLowerCase().equals("end")) {
                System.out.println("\n***** Command Shell Terminated. See you next time. BYE for now. *****\n");
                scanner.close();
                reader.close();
                System.exit(0);
            }
            createProcess(cmdLineArgument, logger);
        }
    }

}

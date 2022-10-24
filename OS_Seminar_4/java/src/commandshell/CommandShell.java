package commandshell;

import java.io.*;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class CommandShell {

    static void createProcess(String command) throws java.io.IOException {

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
        } finally {
            if (bufferReader != null) {
                bufferReader.close();
            }
        }
    }

    public static void main(String[] args) throws java.io.IOException {
        String commandLine;
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n\n***** Welcome to the Java Command Shell *****");
        System.out.println("If you want to exit the shell, type 'end' and press RETURN.\n");
    
        while (true) {
            System.out.print("jsh>");
            commandLine = scanner.nextLine();
            String[] arguments = commandLine.split(" ");
            String initialCommand = arguments[0];
            switch (initialCommand) {
                case CommandStrings.fileDump:
                    _fileDump(arguments);
                    break;
                case CommandStrings.copyFile:
                    _copyFile(arguments);
                    break;
                case CommandStrings.nic:
                    _nicDump(arguments);
                    break;
                case CommandStrings.end:
                    System.out.println("\n***** Command Shell Terminated. See you next time. BYE for now. *****\n");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    createProcess(commandLine);
                    break;
            }
        }   
    }
    private static void _fileDump(String[] arguments) throws FileNotFoundException, IOException {
        if(_isValidCommand(arguments, 2))
        {
            try (Stream<String> stream = Files.lines(Paths.get(arguments[1]))) {
                stream.forEach(System.out::println);
            }
        }
    }

    private static void _nicDump(String[] arguments) throws SocketException {
        try (Stream<NetworkInterface> stream = NetworkInterface.networkInterfaces()) {
            stream.forEach(AddressParser::writeNic);
            System.out.println("\nWrote list of available NIC's on host device to NIC_DUMP.txt\n");
        }
    }

    private static void _copyFile(String[] arguments) throws IOException {
        if(_isValidCommand(arguments, 3))
        {
            try (PrintWriter writer = new PrintWriter(arguments[2], "UTF-8")) {
                try (Stream<String> stream = Files.lines(Paths.get(arguments[1]))) {
                    stream.forEach(writer::println);
                }
            }
            
        }
    }
    
    /// Wrapper function
    private static boolean _isValidCommand(String[] arguments, int len) {
        if(arguments.length < len || arguments.length > len) {
            System.out.printf("\nWrong usage of %s\n", arguments[0]);
            return false;
        }
        File f = new File(arguments[1]);
        if (!(f.exists() && !f.isDirectory())) {
            System.out.printf("\nERROR: The file '%s' could not be found\n\n", arguments[1]);
            return false;
        }
        return true;
    }
}

package commandshell;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.stream.Stream;


/* 
 * This class functions as an intermediary between my command shell
 * application and the NetworkInterface package provided by Java. The main
 * purpose is to retrieve available Network Interface Cards on the hosts computer
 * and write their specifications into a file, NIC_DUMP.txt.
 * 
 * This is a singleton class only allowing for one instance at a time,
 * and is accessed by the CommandShell class as a private property. 
 * The run() function executes a default method overriden by the Runnable interface.
 * 
 * The _buildMac() function parses a byte array into the corresponding MAC address
 * of the NIC, it does this by converting it to hexadecimal and separating the bytes for
 * readability.
 * 
 * The _buildIp() function does a similar thing as _buildMac, but with both the IPv4 and IPv6
 * addresses retrieved from the underlying hardware interface. The '00' strings in the result
 * are replaced by double colons ('::') as is usually done with IP addresses in IPv6 format, as
 * seen when running the 'ifconfig' command on a Unix machine.
 * The IPv4 address is converted by applying an AND mask of a full word (0xFF) to each byte.
 * 
 */
public class NicHandle implements Runnable {

    final private String IPV4 = "IPv4";
    final private String IPV6 = "IPv6";
    private String NICDUMP = "NIC_DUMP.txt";
    private ArrayList<String> NICDUMPSTRINGS = new ArrayList<>();
    private Stream<NetworkInterface> NICSTREAM;
    public static NicHandle INSTANCE;

    
    /**
     * Gives CommandShell class access to a singleton
     * instance of NicHandle.
     * @return [NicHandle]
     */
    public static NicHandle getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new NicHandle();
        }
        return INSTANCE;
    }

    @Override
    public void run() {
        if(INSTANCE == null) 
        {
           System.out.println("\nCould not write device NIC to file.\n");
           return;
        }
        try {
            this.NICSTREAM = NetworkInterface.networkInterfaces();
            if(this.NICSTREAM != null) 
            {
                try (PrintWriter writer = new PrintWriter(NICDUMP)) {
                    NICSTREAM.forEach(INSTANCE::writeNic);
                    NICDUMPSTRINGS.stream().forEach(writer::println);
                    System.out.println("\nWrote list of available NIC's on host device to NIC_DUMP.txt\n");
                    NICDUMPSTRINGS.clear();
                }
            }
            else
            {
                System.out.println("\nCould not write device NIC to file.\n");
            }
        } catch (SocketException e) {
            System.out.println("\nCould not write device NIC to file.\n");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROR: IO Exception occured");
        }
        
    }

    private void writeNic(NetworkInterface nic) {
        try (PrintWriter writer = new PrintWriter(NICDUMP)) {
            Enumeration<InetAddress> inetAddresses = nic.getInetAddresses();
            ListIterator<String> inetTypes = Arrays.asList("IPv6", "IPv4").listIterator();
    
            NICDUMPSTRINGS.add(String.format("\n\n\nSpecifications for Network Interface Card '%s'\n", nic.getName()));
            NICDUMPSTRINGS.add(String.format("\nDisplay name: %s", nic.getDisplayName()));
            while (inetAddresses.hasMoreElements()) {
                String inetType = inetTypes.next();
                byte[] ip = inetAddresses.nextElement().getAddress();
                String ipString = _buildIp(ip, inetType);
                NICDUMPSTRINGS.add(String.format("\n%s: %s", inetType, ipString));
            }
            NICDUMPSTRINGS.add(String.format("\nMAC Address: %s", _buildMac(nic.getHardwareAddress())));
            NICDUMPSTRINGS.add(String.format("\nMTU size: %s", nic.getMTU()));
            NICDUMPSTRINGS.add(String.format("\nIs up: %s", nic.isUp()));
            NICDUMPSTRINGS.add(String.format("\nSupports multicast: %s\n\n", nic.supportsMulticast()));

        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("ERROR: File could not be created and written to");
            fileNotFoundException.printStackTrace();
        } catch (SocketException socketException) {
            System.out.println("ERROR: Socket exception occurred");
        }
    }

    private String _buildMac(byte[] mac) {
        if(mac == null) {
            return "Not available";
        }
        StringBuilder sb = new StringBuilder(18);
        for (byte b : mac) {
            if (sb.length() > 0)
                sb.append(':');
            sb.append(String.format("%02x", b).toUpperCase());
        }
        return sb.toString();
    }

    private String _buildIp(byte[] ip, String protocolType) {
        if(ip == null) {
            return "Not available";
        }
        switch (protocolType) {
            case IPV4:
                StringBuilder ipv4 = new StringBuilder(10);
                for (byte b : ip) {
                    if (ipv4.length() > 0)
                        ipv4.append('.');
                    ipv4.append(String.format("%s", b & 0xFF));
                }
                return ipv4.toString();
        
            case IPV6:
                StringBuilder ipv6 = new StringBuilder(32);
                int m = 0;
                for (byte b : ip) {
                    if ((m % 2 == 0 && m != 0))
                        ipv6.append(':');
                    ipv6.append(String.format("%02X", b));
                    m++;
                }
                return ipv6.toString().replaceAll("00",":").replaceAll("((:)\\2{2})\\2+", "::");
            default:
                return "";
        }
    }
}
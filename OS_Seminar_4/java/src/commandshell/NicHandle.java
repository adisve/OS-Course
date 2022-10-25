package commandshell;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
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
 * The _buildIpv4() and _buildIpv6() functions do a similar thing as _buildMac, but with both 
 * the IPv4 and IPv6 addresses retrieved from the underlying hardware interface. The '00' strings in the result
 * are replaced by double colons ('::') as is usually done with IP addresses in IPv6 format, as
 * seen when running the 'ifconfig' command on a Unix machine or ipconfig on windows.
 * The IPv4 address is converted by applying an AND mask of a full word (0xFF) to each byte.
 * 
 */
public class NicHandle implements Runnable {

    final private String IPV4 = "IPv4";
    final private String IPV6 = "IPv6";
    final private Map<String, String> inetTypes = Map.of(
        "IPv4", "IPv4",
        "IPv6", "IPv6"
        );
    private final String NIC_DUMP = "NIC_DUMP.txt";
    private ArrayList<String> NIC_DUMP_STRINGS = new ArrayList<>();
    private Stream<NetworkInterface> NIC_STREAM;
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
            this.NIC_STREAM = NetworkInterface.networkInterfaces();
            if(this.NIC_STREAM != null) 
            {
                try (PrintWriter writer = new PrintWriter(NIC_DUMP)) {
                    NIC_STREAM.forEach(INSTANCE::writeNic);
                    NIC_DUMP_STRINGS.stream().forEach(writer::println);
                    System.out.println("\nWrote list of available NIC's on host device to NIC_DUMP.txt\n");
                    NIC_DUMP_STRINGS.clear();
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

    /**
     * Adds specifications about a certain NIC to
     * [NIC_DUMP_STRINGS], that are then parsed and written
     * to [NIC_DUMP.txt]
     * @param nic: Specific network interface card
     */
    private void writeNic(NetworkInterface nic) {
        try {
            Enumeration<InetAddress> inetAddresses = nic.getInetAddresses();
            NIC_DUMP_STRINGS.add(String.format("\n\n\nSpecifications for Network Interface Card '%s'\n", nic.getName()));
            NIC_DUMP_STRINGS.add(String.format("\nDisplay name: %s", nic.getDisplayName()));
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                String inetType;
                if(inetAddress instanceof Inet6Address) {
                    inetType = inetTypes.get(IPV6);
                } else {
                    inetType = inetTypes.get(IPV4);
                }
                
                byte[] ip = inetAddress.getAddress();
                String ipString;
                switch (inetType) {
                    case IPV4:
                        ipString = _buildIpv4(ip);
                        break;
                    case IPV6:
                        ipString = _buildIpv6(ip);
                        break;
                    default:
                        ipString = "Not available";
                        break;
                }
                NIC_DUMP_STRINGS.add(String.format("\n%s: %s", inetType, ipString));
            }
            NIC_DUMP_STRINGS.add(String.format("\nMAC Address: %s", _buildMac(nic.getHardwareAddress())));
            NIC_DUMP_STRINGS.add(String.format("\nMTU size: %s", nic.getMTU()));
            NIC_DUMP_STRINGS.add(String.format("\nIs up: %s", nic.isUp()));
            NIC_DUMP_STRINGS.add(String.format("\nSupports multicast: %s\n\n", nic.supportsMulticast()));
        } catch (SocketException e) {
            System.out.println("\nERROR: Socket exception occurred. Could not wrtie to file\n");
        }
        
    }

    /**
     * @param mac: Byte array of 48-bit MAC address
     * @return: String version of 48-bit MAC address
     */
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

    private String _buildIpv6(byte[] ip) {
        if(ip == null) {
            return "Not available";
        }
        StringBuilder ipv6 = new StringBuilder(32);
        int m = 0;
        for (byte b : ip) {
            if ((m % 2 == 0 && m != 0))
                ipv6.append(':');
            ipv6.append(String.format("%02X", b));
            m++;
        }
        return ipv6.toString().replaceAll("00",":").replaceAll("((:)\\2{2})\\2+", "::");
    }

    private String _buildIpv4(byte[] ip) {
        if(ip == null) {
            return "Not available";
        }
        StringBuilder ipv4 = new StringBuilder(10);
        for (byte b : ip) {
            if (ipv4.length() > 0)
                ipv4.append('.');
            ipv4.append(String.format("%s", b & 0xFF));
        }
        return ipv4.toString();
    }
}

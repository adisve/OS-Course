package commandshell;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.ListIterator;


/// Handles parsing of MAC addresses and
/// IPv4 and IPv6 addresses.
public class AddressParser {

    final static String ipv4 = "IPv4";
    final static String ipv6 = "IPv6";
    final static String nicDump = "NIC_DUMP.txt";
    final static ArrayList<String> nicDumpStrings = new ArrayList<>();

    public static void writeNic(NetworkInterface nic) {
        try (PrintWriter writer = new PrintWriter(nicDump)) {
            Enumeration<InetAddress> inetAddresses = nic.getInetAddresses();
            ListIterator<String> inetTypes = Arrays.asList("IPv6", "IPv4").listIterator();
    
            nicDumpStrings.add(String.format("\n\n\nSpecifications for Network Interface Card '%s'\n", nic.getName()));
            nicDumpStrings.add(String.format("\nDisplay name: %s", nic.getDisplayName()));
            while (inetAddresses.hasMoreElements()) {
                String inetType = inetTypes.next();
                byte[] ip = inetAddresses.nextElement().getAddress();
                String ipString = AddressParser._buildIp(ip, inetType);
                nicDumpStrings.add(String.format("\n%s: %s", inetType, ipString));
            }
            nicDumpStrings.add(String.format("\nMAC Address: %s", AddressParser._buildMac(nic.getHardwareAddress())));
            nicDumpStrings.add(String.format("\nMTU size: %s", nic.getMTU()));
            nicDumpStrings.add(String.format("\nIs up: %s", nic.isUp()));
            nicDumpStrings.add(String.format("\nSupports multicast: %s\n\n", nic.supportsMulticast()));

            nicDumpStrings.stream().forEach(writer::println);

        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("ERROR: File could not be created and written to");
            fileNotFoundException.printStackTrace();
        } catch (SocketException socketException) {
            System.out.println("ERROR: Socket exception occurred");
        }
    }

    private static String _buildMac(byte[] mac) {
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

    private static String _buildIp(byte[] ip, String protocolType) {
        if(ip == null) {
            return "Not available";
        }
        switch (protocolType) {
            case ipv4:
                StringBuilder ipv4 = new StringBuilder(10);
                for (byte b : ip) {
                    if (ipv4.length() > 0)
                        ipv4.append('.');
                    ipv4.append(String.format("%s", b & 0xFF));
                }
                return ipv4.toString();
        
            case ipv6:
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

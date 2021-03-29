import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Raye
 * @since 3/29/2021 14:19
 */
public class HostsReplace {

    private static Path HOSTS_PATH;

    private static final String END = String.format("%n");

    private static final String WINDOWS_FLUSH_DNS_CMD = "ipconfig/flushdns";

    /**
     * init system hosts local file path
     */
    public static void init() {
        // chosen system
        if ("linux".equalsIgnoreCase(System.getProperty("os.name"))) {
            HOSTS_PATH = Paths.get("/etc/hosts");
        } else {
            HOSTS_PATH = Paths.get("C://WINDOWS//system32//drivers//etc//hosts");
        }
    }

    /**
     * get exists dns
     *
     * @return hosts exists dns data
     * @throws IOException IOException
     */
    public static Set<String> read() throws IOException {
        init();
        return Arrays.stream(Files.readAllLines(HOSTS_PATH).toArray())
                .filter(it->!it.toString().trim().matches("(^#.*)|(\\s*)"))
                .map(it->it.toString().replaceAll("#.*", "").trim().replaceAll("\\s+", "\t"))
                .collect(Collectors.toSet());
    }

    /**
     * append hosts file dns ip
     *
     * @param in input dns ip
     * @throws IOException IOException
     */
    public static void append(String in) throws IOException {
        init();
        try {
            Files.writeString(HOSTS_PATH, END+in, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        flushDns();
    }

    /**
     * judge dns ip exists
     *
     * @param in input dns ip
     * @return boolean
     * @throws IOException IOException
     */
    public static boolean exists(String in) throws IOException {
        return read().contains(in);
    }

    /**
     * refresh DNS windows like win+R -> cmd -> ipconfig/flushdns
     *
     * @throws IOException IOException
     */
    public static void flushDns() throws IOException {
        try {
            Runtime.getRuntime().exec(WINDOWS_FLUSH_DNS_CMD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

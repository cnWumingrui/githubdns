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

    public static void init() {
        // 判断系统
        if ("linux".equalsIgnoreCase(System.getProperty("os.name"))) {
            HOSTS_PATH = Paths.get("/etc/hosts");
        } else {
            HOSTS_PATH = Paths.get("C://WINDOWS//system32//drivers//etc//hosts");
        }
    }

    public static Set<String> read() throws IOException {
        init();
        return Arrays.stream(Files.readAllLines(HOSTS_PATH).toArray())
                .filter(it->!it.toString().trim().matches("(^#.*)|(\\s*)"))
                .map(it->it.toString().replaceAll("#.*", "").trim().replaceAll("\\s+", "\t"))
                .collect(Collectors.toSet());
    }

    public static void append(String in) throws IOException {
        init();
        try {
            Files.writeString(HOSTS_PATH, END+in, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        flushDns();
    }

    public static boolean exists(String in) throws IOException {
        return read().contains(in);
    }

    public static void flushDns() throws IOException {
        try {
            Runtime.getRuntime().exec(WINDOWS_FLUSH_DNS_CMD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

import java.io.IOException;
import java.util.List;

/**
 * @author Raye
 */
public class GetIpProcess {

    /**
     * TODO: remove overdue dns url
     *
     * @param args args
     */
    public static void main(String[] args) {

        List<String> result = GithubProcess.getGithubDnsResult();

        result.forEach(data -> {
            try {
                if (!HostsReplace.exists(data)) {
                    HostsReplace.append(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

}

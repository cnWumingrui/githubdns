import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Raye
 * @since 3/29/2021 14:17
 */
public class GithubProcess {

    private static final List<String> GITHUB_URL_LIST = Arrays.asList(
            "github.githubassets.com",
            "camo.githubusercontent.com",
            "github.map.fastly.net",
            "github.global.ssl.fastly.net",
            "api.github.com",
            "raw.githubusercontent.com",
            "user-images.githubusercontent.com",
            "favicons.githubusercontent.com");

    /**
     * analysis github dns
     *
     * @return java.util.List
     */
    public static List<String> getGithubDnsResult() {
        ArrayList<String> result = new ArrayList<>();

        //remove same links
        Set<String> stringSet = new HashSet<>();

        // 1. 创建一个HTTPClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 2. 设置URL遍历
        GITHUB_URL_LIST.forEach(url -> {
            String domain = "";
            String[] splits = url.split("\\.");
            for (int i = 0; i < splits.length; i++) {
                if (i == splits.length - 2) {
                    domain += splits[i] + ".";
                }
                if (i == splits.length - 1) {
                    domain += splits[i];
                }
            }
            // 3. 生成get请求
            // 模板： https://githubusercontent.com.ipaddress.com/camo.githubusercontent.com
            String getUrl = "https://" + domain + ".ipaddress.com/" + url;
            HttpGet httpGet = new HttpGet(getUrl);
            try {
                // 4. 执行得到返回结果
                CloseableHttpResponse response = httpClient.execute(httpGet);
                // 5. 解析
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String html = EntityUtils.toString(response.getEntity(), "utf-8");
                    // 6. 使用jsoup解析
                    Document document = Jsoup.parse(html);
                    // 7. 获取链接
                    Elements linkes = document.select("a[href]");
                    for (Element link : linkes) {
                        if (link.toString().contains("https://www.ipaddress.com/ipv4/")) {
                            String res = String.format("%s\t%s", link.text(), url);
                            if (stringSet.add(res)) {
                                result.add(res);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return result;
    }

}

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
import java.util.List;

public class Test {

    public static void main(String[] args) {
        ArrayList<String> result = new ArrayList<>();
        // 1. 创建一个HTTPClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 2. 设置URL遍历
        List<String> urls = Arrays.asList(
                "github.githubassets.com",
                "camo.githubusercontent.com",
                "github.map.fastly.net",
                "github.global.ssl.fastly.net",
                "github.com",
                "api.github.com",
                "raw.githubusercontent.com",
                "user-images.githubusercontent.com",
                "favicons.githubusercontent.com");
        urls.forEach(url -> {
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
                            String res = link.text() + " " + url;
                            result.add(res);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        result.forEach(r -> System.out.println(r));
    }

}

package common;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String argv[]) throws IOException {
        System.out.println("This application take tuples(reference, weight(KB), height, width) from <img> in HTML");
        System.out.println("Input website reference");
        // test_link: "https://forum.awd.ru/viewtopic.php?f=1011&t=165935"
        Scanner in = new Scanner(System.in);

        String link = in.nextLine();

        Connection.Response response = null;
        try {

            response = Jsoup
                    .connect(link)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                    .followRedirects(true)
                    .timeout(10000)
                    .execute();
        }   catch (IllegalArgumentException | SocketTimeoutException | UnknownHostException | HttpStatusException e) {

            System.out.println(e);
            System.exit(0);
        }

        int statusCode = response.statusCode();

        System.out.printf("Response status: %d\n", statusCode);
        if (statusCode >= 200 && statusCode < 300) {

            System.out.println("Succesfull");
        } else {

            System.out.println("Uncatched Error");
            System.exit(0);
        }

        Document websiteDOM = response.parse();
        Elements imagesList = websiteDOM.select("img");
        var references = new ArrayList<String>();

        for (var img : imagesList) {

            if (!references.contains(img.attr("src"))) {

                references.add(img.attr("src"));
            }
        }

        var fileredReferences = Filter.filterReferences(references, link);
        if(fileredReferences.isEmpty()){
            System.out.println("Images not found");
            System.exit(0);
        }

        CloseableHttpClient httpClient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();

        String tmp_file_path = System.getProperty("user.dir").concat("/tmp/temp_file");
        for(var ref : fileredReferences) {
            try {

                HttpGet httpGet = new HttpGet(ref.ref);
                httpClient.execute(httpGet, new MyResponseHandler(new File(tmp_file_path.concat(ref.formatImage)), ref.ref));
            } catch (Exception e) {

                throw new IllegalStateException(e);
            }
        }

        IOUtils.closeQuietly(httpClient);
    }
}

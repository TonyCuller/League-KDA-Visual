import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class for Calling Riot's API using Apache CloseableHttp Client
 */

//Makes HTTP GET calls with given endpoint String and returns data in a String.
public class ApiCall {
    private static final String api_key = "?api_key=API-KEY-HERE";

    public static String getRiotJSON(String endpoint) throws IOException {
        CloseableHttpClient riotClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(endpoint + api_key);
        CloseableHttpResponse response = riotClient.execute(get);
        StringBuilder result = new StringBuilder();

        try {
            System.out.println(response.getStatusLine().getStatusCode());

            BufferedReader reader = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String line;


            while ((line = reader.readLine()) != null) {
                result.append(line);
            }


        } catch (IOException e) {
            System.err.println(e.getCause().getLocalizedMessage());
        } finally {
            riotClient.close();
            response.close();

        }
        return result.toString();
    }
}

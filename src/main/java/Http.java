import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.logging.Logger;


interface RequestSender {
    HttpResponse get(String url, Map<String, String> params) throws IOException, InterruptedException, URISyntaxException;
}

public final class Http implements RequestSender {
    private static final Logger LOGGER = Logger.getLogger(Http.class.getName());

    private HttpClient client;

    public Http() {
        this.client = HttpClient.newHttpClient();
    }

    private String buildUri(String url, Map<String, String> params) {
        String uri = url + "?";
        int i = 0;
        int mapSize = params.size();

        // Manually construct uri with query params
        for (var entry : params.entrySet()) {
            uri += (entry.getKey() + "=" + entry.getValue().replaceAll("\\s+", ""));
            if (i < mapSize - 1) {
                uri += "&";
            }
            i++;
        }

        return uri;
    }


    private HttpRequest buildRequest(String url, Map<String, String> params) throws URISyntaxException {
        String uri = this.buildUri(url, params);
        LOGGER.info(String.format("Making request to URI: %s", uri));

        HttpRequest request = HttpRequest.newBuilder().uri(new URI(uri)).GET().build();


        return request;
    }


    private HttpResponse sendRequest(HttpRequest request, HttpResponse.BodyHandler handler) throws IOException, InterruptedException {
        HttpResponse response = this.client.send(request, handler);
        return response;
    }


    public HttpResponse get(String url, Map<String, String> params) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = this.buildRequest(url, params);
        HttpResponse response = this.sendRequest(request,
                HttpResponse.BodyHandlers.ofInputStream());

        return response;
    }
}
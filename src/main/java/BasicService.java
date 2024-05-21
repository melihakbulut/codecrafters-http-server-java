import java.net.Socket;
import java.util.Objects;

public class BasicService extends AbstractService {

    private String body;

    public BasicService(String body) {
        this.body = body;
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest, Socket clientSocket) {
        HttpResponse httpResponse = new HttpResponse(HttpStatus.SUCCESS);
        httpResponse.getHeaders().put("Content-Type", "text/plain");
        String acceptEncoding = httpRequest.getHeaders().get("Accept-Encoding");

        if (Objects.nonNull(acceptEncoding) && acceptEncoding.contains("gzip")) {
            httpResponse.getHeaders().put("Content-Encoding", "gzip");
            httpResponse.setBody(compressResponse(body));
        } else {
            httpResponse.setBody(body);
        }
        httpResponse.getHeaders().put("Content-Length",
                                      String.valueOf(httpResponse.getBody().length));
        return httpResponse;
    }

}

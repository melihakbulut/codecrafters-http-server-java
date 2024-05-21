import java.net.Socket;
import java.util.Objects;

public class EchoService implements IService {

    @Override
    public HttpResponse process(HttpRequest httpRequest, Socket clientSocket) {
        HttpResponse httpResponse = new HttpResponse(HttpStatus.SUCCESS);
        httpResponse.getHeaders().put("Content-Type", "text/plain");
        String acceptEncoding = httpRequest.getHeaders().get("Accept-Encoding");

        String value = httpRequest.getEndpoint().replaceAll("echo/", "");
        if (Objects.nonNull(acceptEncoding) && acceptEncoding.contains("gzip")) {
            httpResponse.getHeaders().put("Content-Encoding", "gzip");
            httpResponse.setBody(compressResponse(value));
        } else {
            httpResponse.setBody(value);
        }
        httpResponse.getHeaders().put("Content-Length",
                                      String.valueOf(httpResponse.getBody().length));
        return httpResponse;
    }

}

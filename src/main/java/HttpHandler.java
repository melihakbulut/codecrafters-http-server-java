import java.io.IOException;
import java.net.Socket;

public class HttpHandler {

    public static final String NEW_LINE = "\r\n\r\n";
    public static final String SUCCESS = "HTTP/1.1 200 OK" + NEW_LINE;
    public static final String NOT_FOUND = "HTTP/1.1 404 Not Found" + NEW_LINE;

    public void handle(Socket clientSocket) throws IOException {
        HttpRequest httpRequest = HttpParser.parse(clientSocket);
        String answer = null;
        if (httpRequest.getEndpoint().isEmpty())
            answer = SUCCESS;
        else if (httpRequest.getEndpoint().startsWith("echo")) {
            String value = httpRequest.getEndpoint().replaceAll("echo/", "");
            answer = sendEcho(value);
        }

        else
            answer = NOT_FOUND;
        System.out.println(answer);
        clientSocket.getOutputStream().write(answer.getBytes());
    }

    public String sendEcho(String value) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SUCCESS);
        stringBuilder.append("Content-Type: text/plain\r\n");
        stringBuilder.append(String.format("Content-Length: %s\r\n\r\n%s", value.length(), value));
        return stringBuilder.toString();
    }
}

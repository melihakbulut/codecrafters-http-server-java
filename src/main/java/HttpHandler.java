import java.io.IOException;
import java.net.Socket;

public class HttpHandler implements Runnable {

    //    public static final String NEW_LINE = "\r\n\r\n";
    public static final String NEW_LINE = "\r\n";
    public static final String SUCCESS = "HTTP/1.1 200 OK" + NEW_LINE;
    public static final String NOT_FOUND = "HTTP/1.1 404 Not Found" + NEW_LINE;

    private Socket clientSocket;

    public HttpHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handle();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void handle() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpParser.parse(clientSocket);
        String answer = null;
        if (httpRequest.getEndpoint().isEmpty())
            answer = SUCCESS;
        else if (httpRequest.getEndpoint().startsWith("echo")) {
            String value = httpRequest.getEndpoint().replaceAll("echo/", "");
            answer = prepareSuccessResponse(value);
        } else if (httpRequest.getEndpoint().equals("user-agent")) {
            answer = prepareSuccessResponse(httpRequest.getHeaders().get("User-Agent"));
        } else
            answer = NOT_FOUND;
        answer += NEW_LINE;
        System.out.println(answer);
        System.out.println("sad");
        clientSocket.getOutputStream().write(answer.getBytes());
        Thread.sleep(10);
    }

    public String prepareSuccessResponse(String body) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SUCCESS);
        stringBuilder.append("Content-Type: text/plain" + NEW_LINE);
        stringBuilder.append(String.format("Content-Length: %s%s%s%s", body.length(), NEW_LINE,
                                           NEW_LINE, body));
        return stringBuilder.toString();
    }

}

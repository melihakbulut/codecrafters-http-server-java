import java.io.IOException;
import java.net.Socket;
import java.text.MessageFormat;

public class HttpHandler implements Runnable {

    private Socket clientSocket;
    private String baseDir;

    public HttpHandler(Socket clientSocket, String baseDir) {
        this.clientSocket = clientSocket;
        this.baseDir = baseDir;
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

        AbstractService service = null;
        if (httpRequest.getEndpoint().isEmpty())
            sendHttpResponse(new HttpResponse(HttpStatus.SUCCESS));
        else if (httpRequest.getEndpoint().startsWith("echo")) {
            service = new BasicService(httpRequest.getEndpoint().replaceAll("echo/", ""));
        } else if (httpRequest.getEndpoint().equals("user-agent")) {
            service = new BasicService(httpRequest.getHeaders().get("User-Agent"));
        } else if (httpRequest.getEndpoint().startsWith("files")) {
            service = new FileService(baseDir);
        } else
            sendHttpResponse(new HttpResponse(HttpStatus.NOT_FOUND));

        if (service != null)
            sendHttpResponse(service.process(httpRequest, clientSocket));

    }

    private void sendHttpResponse(HttpResponse httpResponse) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HttpParser.HTTP_PREFIX + httpResponse.getHttpStatus().getValue()
                             + HttpParser.NEW_LINE);
        httpResponse.getHeaders().forEach((k, v) -> stringBuilder
                        .append(MessageFormat.format("{0}:{1}{2}", k, v, HttpParser.NEW_LINE)));
        stringBuilder.append(HttpParser.NEW_LINE);
        sendResponse(stringBuilder.toString().getBytes(), httpResponse.getBody());
    }

    public void sendResponse(String response) throws IOException {
        if (response != null) {
            response += HttpParser.NEW_LINE;
            sendResponse(response.getBytes());
        }
    }

    public void sendResponse(byte[] message) throws IOException {
        clientSocket.getOutputStream().write(message);
        clientSocket.getOutputStream().flush();

    }

    public void sendResponse(byte[]... messages) throws IOException {
        for (byte[] message : messages) {
            clientSocket.getOutputStream().write(message);
        }
        clientSocket.getOutputStream().flush();

    }

}

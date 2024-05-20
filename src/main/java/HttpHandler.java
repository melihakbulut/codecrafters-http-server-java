import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class HttpHandler implements Runnable {

    //    public static final String NEW_LINE = "\r\n\r\n";
    public static final String NEW_LINE = "\r\n";
    public static final String SUCCESS = "HTTP/1.1 200 OK" + NEW_LINE;
    public static final String NOT_FOUND = "HTTP/1.1 404 Not Found" + NEW_LINE;

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
        String answer = null;
        if (httpRequest.getEndpoint().isEmpty())
            answer = SUCCESS;
        else if (httpRequest.getEndpoint().startsWith("echo")) {
            String value = httpRequest.getEndpoint().replaceAll("echo/", "");
            answer = sendSuccessResponse(value);
        } else if (httpRequest.getEndpoint().equals("user-agent")) {
            answer = sendSuccessResponse(httpRequest.getHeaders().get("User-Agent"));
        } else if (httpRequest.getEndpoint().startsWith("files")) {
            String fileName = httpRequest.getEndpoint().replaceAll("files/", "");
            if (httpRequest.getHttpMethod().equals("GET")) {
                sendFile(fileName);
                return;
            } else if (httpRequest.getHttpMethod().equals("POST")) {
                saveFile(httpRequest, fileName);
                return;
            }
        } else
            answer = NOT_FOUND;
        answer += NEW_LINE;
        System.out.println(answer);
        System.out.println("sad");
        clientSocket.getOutputStream().write(answer.getBytes());
    }

    private void saveFile(HttpRequest httpRequest, String fileName) throws IOException {
        Path.of(baseDir + "/" + fileName).toFile().createNewFile();
        Files.write(Path.of(baseDir + "/" + fileName), httpRequest.getBody().getBytes(),
                    StandardOpenOption.WRITE);
    }

    public String sendSuccessResponse(String body) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SUCCESS);
        stringBuilder.append("Content-Type: text/plain" + NEW_LINE);
        stringBuilder.append(String.format("Content-Length: %s%s%s%s", body.length(), NEW_LINE,
                                           NEW_LINE, body));
        return stringBuilder.toString();
    }

    public void sendFile(String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SUCCESS);
        stringBuilder.append("Content-Type: application/octet-stream" + NEW_LINE);
        byte[] content = null;
        try {
            content = Files.readAllBytes(Path.of(baseDir + "/" + fileName));
        } catch (NoSuchFileException e) {
            clientSocket.getOutputStream().write((NOT_FOUND + NEW_LINE).getBytes());
            return;
        }

        stringBuilder.append(String.format("Content-Length: %s%s%s", content.length, NEW_LINE,
                                           NEW_LINE));
        stringBuilder.toString();

        clientSocket.getOutputStream().write(stringBuilder.toString().getBytes());
        clientSocket.getOutputStream().write(content);
    }

}

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.zip.GZIPOutputStream;

public class HttpHandler implements Runnable {

    //    public static final String NEW_LINE = "\r\n\r\n";
    public static final String NEW_LINE = "\r\n";
    public static final String SUCCESS = "HTTP/1.1 200 OK" + NEW_LINE;
    public static final String CREATED = "HTTP/1.1 201 Created" + NEW_LINE;
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

        AbstractService service = null;
        if (httpRequest.getEndpoint().isEmpty())
            sendHttpResponse(new HttpResponse(HttpStatus.SUCCESS));
        else if (httpRequest.getEndpoint().startsWith("echo")) {
            service = new BasicService(httpRequest.getEndpoint().replaceAll("echo/", ""));
        } else if (httpRequest.getEndpoint().equals("user-agent")) {
            service = new BasicService(httpRequest.getHeaders().get("User-Agent"));
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
            sendHttpResponse(new HttpResponse(HttpStatus.NOT_FOUND));

        sendHttpResponse(service.process(httpRequest, clientSocket));

    }

    private void sendBasicHttpResponse(HttpResponse httpResponse) throws IOException {

    }

    private void sendHttpResponse(HttpResponse httpResponse) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HttpResponse.HTTP_PREFIX + httpResponse.getHttpStatus().getValue()
                             + NEW_LINE);
        httpResponse.getHeaders().forEach((k, v) -> stringBuilder
                        .append(MessageFormat.format("{0}:{1}{2}", k, v, NEW_LINE)));
        stringBuilder.append(NEW_LINE);
        sendResponse(stringBuilder.toString().getBytes(), httpResponse.getBody());
    }

    private void saveFile(HttpRequest httpRequest, String fileName) throws IOException,
                                                                    InterruptedException {
        Path.of(baseDir + "/" + fileName).toFile().createNewFile();
        Files.write(Path.of(baseDir + "/" + fileName), httpRequest.getBody().getBytes(),
                    StandardOpenOption.WRITE);
        sendResponse(CREATED);
    }

    public String sendSuccessResponse(String body, HttpRequest httpRequest) throws IOException {
        System.out.println(body);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SUCCESS);
        stringBuilder.append("Content-Type: text/plain" + NEW_LINE);
        if (httpRequest.getHeaders().get("Accept-Encoding") != null
            && httpRequest.getHeaders().get("Accept-Encoding").contains("gzip")) {
            stringBuilder.append("Content-Encoding: gzip" + NEW_LINE);
            byte[] bodyArr = compressResponse(body.getBytes());
            stringBuilder.append(String.format("Content-Length: %s%s", bodyArr.length, NEW_LINE));

            sendResponse(stringBuilder.toString());
            sendResponse(bodyArr);
            return null;

        } else {
            stringBuilder.append(String.format("Content-Length: %s%s%s%s", body.length(), NEW_LINE,
                                               NEW_LINE, body));
        }
        return stringBuilder.toString();
    }

    private byte[] compressResponse(byte[] fileContents) {
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(arrayOutputStream);
            gzip.write(fileContents);
            gzip.finish();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.print("IOException: " + e.getMessage());
            return fileContents;
        }
    }

    public void sendFile(String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SUCCESS);
        stringBuilder.append("Content-Type: application/octet-stream" + NEW_LINE);
        byte[] content = null;
        try {
            content = Files.readAllBytes(Path.of(baseDir + "/" + fileName));
        } catch (NoSuchFileException e) {
            sendResponse(NOT_FOUND);
            return;
        }

        stringBuilder.append(String.format("Content-Length: %s%s", content.length, NEW_LINE));
        stringBuilder.toString();

        sendResponse(stringBuilder.toString());
        sendResponse(content);
    }

    public void sendResponse(String response) throws IOException {
        if (response != null) {
            response += NEW_LINE;
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

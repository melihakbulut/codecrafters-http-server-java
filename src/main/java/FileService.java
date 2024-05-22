import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileService extends AbstractService {
    private String baseDir;

    public FileService(String baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest, Socket clientSocket) throws IOException,
                                                                              InterruptedException {
        HttpResponse httpResponse = null;
        String fileName = httpRequest.getEndpoint().replaceAll("files/", "");
        if (httpRequest.getHttpMethod().equals("GET")) {
            httpResponse = sendFile(fileName);
        } else if (httpRequest.getHttpMethod().equals("POST")) {
            httpResponse = saveFile(httpRequest, fileName);
        }
        return httpResponse;

    }

    private HttpResponse saveFile(HttpRequest httpRequest, String fileName) throws IOException,
                                                                            InterruptedException {
        Path.of(baseDir + "/" + fileName).toFile().createNewFile();
        Files.write(Path.of(baseDir + "/" + fileName), httpRequest.getBody().getBytes(),
                    StandardOpenOption.WRITE);
        return new HttpResponse(HttpStatus.CREATED);
    }

    public HttpResponse sendFile(String fileName) throws IOException {

        HttpResponse httpResponse = new HttpResponse(HttpStatus.SUCCESS);
        httpResponse.getHeaders().put("Content-Type", "application/octet-stream");

        try {
            httpResponse.setBody(Files.readAllBytes(Path.of(baseDir + "/" + fileName)));
        } catch (NoSuchFileException e) {
            return new HttpResponse(HttpStatus.NOT_FOUND);
        }

        httpResponse.getHeaders().put("Content-Length",
                                      String.valueOf(httpResponse.getBody().length));

        return httpResponse;
    }

}

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.zip.GZIPOutputStream;

public interface IService {

    HttpResponse process(HttpRequest httpRequest, Socket clientSocket);

    default public byte[] compressResponse(String body) {
        return compressResponse(body.getBytes());
    }

    default public byte[] compressResponse(byte[] fileContents) {
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
}

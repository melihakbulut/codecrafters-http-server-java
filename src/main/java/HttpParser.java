import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpParser {

    public static final String NEW_LINE = "\r\n";
    public static final String HTTP_PREFIX = "HTTP/1.1 ";

    public static HttpRequest parse(Socket clientSocket) throws IOException {
        final int bufLen = 128 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int index = 0;
        while (true) {
            byte b = (byte) clientSocket.getInputStream().read();
            if (b == -1)
                break;
            buf[index] = b;
            try {

                if (buf[index - 3] == 13 && buf[index - 2] == 10 && buf[index - 1] == 13
                    && buf[index] == 10) {
                    break;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            index++;
        }
        byte[] shrinkedBuffer = new byte[index];
        System.arraycopy(buf, 0, shrinkedBuffer, 0, index);
        String payload = new String(shrinkedBuffer, StandardCharsets.UTF_8);
        if (payload.isEmpty())
            return null;
        return parseHttpRequest(payload, clientSocket);
    }

    private static HttpRequest parseHttpRequest(String payload,
                                                Socket clientSocket) throws NumberFormatException,
                                                                     IOException {
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setEndpoint(parseEndpoint(payload));
        httpRequest.setHttpMethod(parseHttpMethod(payload));
        String[] payloadArr = payload.split(NEW_LINE);
        for (String payloadItem : payloadArr) {
            String[] payloadItemArr = payloadItem.split(":");
            if (payloadItemArr.length == 2 && !payloadItemArr[0].isEmpty())
                httpRequest.getHeaders().put(payloadItemArr[0].trim(), payloadItemArr[1].trim());

        }
        String contentLength = httpRequest.getHeaders().get("Content-Length");
        if (contentLength != null) {
            httpRequest.setBody(new String(clientSocket.getInputStream()
                            .readNBytes(Integer.parseInt(contentLength))));
        }
        return httpRequest;
    }

    private static String parseEndpoint(String payload) {
        int urlIndex = payload.indexOf("/");
        int httpIndex = payload.indexOf("HTTP");
        return payload.substring(urlIndex + 1, httpIndex - 1);
    }

    private static String parseHttpMethod(String payload) {
        int firstSpaceIndex = payload.indexOf(" ");
        return payload.substring(0, firstSpaceIndex);
    }
}

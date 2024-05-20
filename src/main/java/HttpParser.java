import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpParser {

    public static HttpRequest parse(Socket clientSocket) throws IOException {
        final int bufLen = 4 * 0x400; // 4KB
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
        String payload = new String(buf, StandardCharsets.UTF_8);
        String endpoint = parseEndpoint(payload);
        String body = parseBody(payload);
        return new HttpRequest(endpoint, null, body);
    }

    private static String parseBody(String payload) {
        Integer contentSize = null;
        String[] payloadArr = payload.split("\r\n");
        for (String payloadItem : payloadArr) {
            if (payloadItem.startsWith("Content-Length")) {
                contentSize = Integer.parseInt(payloadItem.split(":")[1].trim());
            }
        }

        payloadArr[payloadArr.length - 1].length();

        return null;
    }

    private static String parseEndpoint(String payload) {
        int urlIndex = payload.indexOf("/");
        int httpIndex = payload.indexOf("HTTP");
        return payload.substring(urlIndex + 1, httpIndex - 1);
    }
}

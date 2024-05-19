import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {

    public static final String NEW_LINE = "\r\n\r\n";
    public static final String SUCCESS = "HTTP/1.1 200 OK" + NEW_LINE;
    public static final String NOT_FOUND = "HTTP/1.1 404 Not Found" + NEW_LINE;

    public static void main(String[] args) throws InterruptedException {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        //     Uncomment this block to pass the first stage

        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);
            clientSocket = serverSocket.accept(); // Wait for connection from client.
            System.out.println("accepted new connection");
            String url = parseUrl(clientSocket);
            String answer = null;
            if (url.isEmpty())
                answer = SUCCESS;
            else
                answer = NOT_FOUND;
            System.out.println(answer);

            clientSocket.getOutputStream().write(answer.getBytes());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static String parseUrl(Socket clientSocket) throws IOException {
        //        String s = new String(readAllBytes(clientSocket.getInputStream()));
        final int bufLen = 4 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int index = 0;
        while (true) {
            byte b = (byte) clientSocket.getInputStream().read();
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
        String s = new String(buf, StandardCharsets.UTF_8);
        int urlIndex = s.indexOf("/");
        int httpIndex = s.indexOf("HTTP");
        return s.substring(urlIndex + 1, httpIndex - 1);
    }

    //    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
    //        final int bufLen = 4 * 0x400; // 4KB
    //        byte[] buf = new byte[bufLen];
    //        int readLen;
    //        IOException exception = null;
    //
    //        try {
    //            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
    //                while ((inputStream.read(buf, 0, bufLen)) != -1)
    //                    outputStream.write(buf, 0, readLen);
    //
    //                return outputStream.toByteArray();
    //            }
    //        } catch (IOException e) {
    //            exception = e;
    //            throw e;
    //        } finally {
    //            if (exception == null)
    //                inputStream.close();
    //            else
    //                try {
    //                    inputStream.close();
    //                } catch (IOException e) {
    //                    exception.addSuppressed(e);
    //                }
    //        }
    //    }
}

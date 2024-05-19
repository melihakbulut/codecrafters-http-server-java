import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;

public class Main {

    public static final String NEW_LINE = "\r\n\r\n";
    public static final String SUCCESS = "HTTP/1.1 200 OK" + NEW_LINE;
    public static final String NOT_FOUND = "HTTP/1.1 404 Not Found" + NEW_LINE;

    public static void main(String[] args) {
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
            if (url.equals("index.html"))
                answer = SUCCESS;
            else
                answer = NOT_FOUND;

            clientSocket.getOutputStream().write(answer.getBytes());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static String parseUrl(Socket clientSocket) throws IOException {
        String s = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).lines()
                        .collect(Collectors.joining("\n"));
        int urlIndex = s.indexOf("/");
        int httpIndex = s.indexOf("HTTP");
        return s.substring(urlIndex + 1, httpIndex - 1);
    }
}

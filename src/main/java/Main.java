import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");
        String baseDir = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--directory")) {
                baseDir = args[i + 1];
                break;
            }

        }
        //     Uncomment this block to pass the first stage

        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);
            while (true) {
                clientSocket = serverSocket.accept(); // Wait for connection from client.
                System.out.println("accepted new connection");
                new Thread(new HttpHandler(clientSocket, baseDir)).start();
            }

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

}

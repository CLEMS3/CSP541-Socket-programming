import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatServer {
    public static void main(String[] args) {
        int port = 5000;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("Starting Socket Server...");
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(dtf.format(LocalDateTime.now()) + " - Server is listening on port " + port);

            // Wait for a client to connect
            Socket socket = serverSocket.accept();
            System.out.println(dtf.format(LocalDateTime.now()) + " - Client connected from: " + socket.getInetAddress().getHostAddress());

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            String text;

            // Thread to read messages from the client asynchronously
            Thread readThread = new Thread(() -> {
                try {
                    String clientMessage;
                    while ((clientMessage = input.readLine()) != null) {
                        System.out.println("\n[" + dtf.format(LocalDateTime.now()) + "] Client: " + clientMessage);
                        System.out.print("Server: ");
                    }
                } catch (IOException e) {
                    System.out.println("\n" + dtf.format(LocalDateTime.now()) + " - Client disconnected.");
                    System.exit(0);
                }
            });
            readThread.start();

            // Main thread to read from console and send messages to the client
            System.out.print("Server: ");
            while ((text = consoleInput.readLine()) != null) {
                output.println(text);
                System.out.print("Server: ");
            }

            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

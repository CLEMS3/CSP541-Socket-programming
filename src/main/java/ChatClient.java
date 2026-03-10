import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatClient {
    public static void main(String[] args) {
        // default, when running on the same machine
        String hostname = "127.0.0.1";
        int port = 5000;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (args.length > 0) {
            hostname = args[0]; // ip is a parameter, when running on two different machine
        }

        System.out.println("Connecting to chat server at " + hostname + ":" + port);

        try (Socket socket = new Socket(hostname, port)) {
            System.out.println(dtf.format(LocalDateTime.now()) + " - Connected to the chat server");

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            // Thread to read messages from the server asynchronously
            Thread readThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = input.readLine()) != null) {
                        System.out.println("\n[" + dtf.format(LocalDateTime.now()) + "] Server: " + serverMessage);
                        System.out.print("Client: ");
                    }
                } catch (IOException e) {
                    System.out.println("\n" + dtf.format(LocalDateTime.now()) + " - Server connection closed.");
                    System.exit(0);
                }
            });
            readThread.start();

            // Main thread to read from console and send messages to the server
            String text;
            System.out.print("Client: ");
            while ((text = consoleInput.readLine()) != null) {
                output.println(text);
                System.out.print("Client: ");
            }

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}

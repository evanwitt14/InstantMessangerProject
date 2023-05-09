import java.io.*;
import java.net.*;
import java.util.*;

public class InstantMessengerServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Server started.");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected from " + clientSocket.getInetAddress().getHostName());

            Thread clientThread = new Thread(new ClientHandler(clientSocket));
            clientThread.start();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Authentication
            while (true) {
                out.println("Enter username:");
                username = in.readLine();
                out.println("Enter password:");
                String password = in.readLine();
                if (authenticate(username, password)) {
                    out.println("Authentication successful. Welcome, " + username + "!");
                    break;
                } else {
                    out.println("Authentication failed. Try again.");
                }
            }

            // Display client status
            System.out.println(username + " connected from " + clientSocket.getInetAddress().getHostName());

            // Communication
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("bye")) {
                    break;
                } else if (inputLine.startsWith("file:")) {
                    String[] parts = inputLine.split(":");
                    String filename = parts[1];
                    receiveFile(filename);
                } else {
                    System.out.println(username + ": " + inputLine);
                    out.println(username + ": " + inputLine);
                }
            }

            // Disconnect
            out.println("bye");
            clientSocket.close();
            System.out.println(username + " disconnected.");

        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }

    private boolean authenticate(String username, String password) {
        // TODO: Implement authentication logic here
        return true;
    }

    private void receiveFile(String filename) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        String path = "received_files/" + filename;
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
        fileOutputStream.close();
        System.out.println(username + " received file " + filename);
    }
}
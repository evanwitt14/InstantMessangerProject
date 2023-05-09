import java.io.*;
import java.net.*;

public class InstantMessengerClient {
    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        int portNumber = 1234;

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            // Authentication
            while (true) {
                String userInput;
                System.out.println(in.readLine());
                userInput = stdIn.readLine();
                out.println(userInput);
                System.out.println(in.readLine());
                userInput = stdIn.readLine();
                out.println(userInput);
                String response = in.readLine();
                System.out.println(response);
                if (response.startsWith("Authentication successful")) {
                    break;
                }
            }

            // Communication
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                if (userInput.startsWith("file:")) {
                    String[] parts = userInput.split(":");
                    String filename = parts[1];
                    File file = new File(filename);
                    if (!file.exists()) {
                        System.out.println("File does not exist.");
                        continue;
                    }
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        socket.getOutputStream().write(buffer, 0, bytesRead);
                    }
                    fileInputStream.close();
                    System.out.println("File sent: " + filename);
                } else {
                    out.println(userInput);
                    System.out.println(in.readLine());
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}
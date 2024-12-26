import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");
    if (args.length != 1) {
      System.out.println("Usage: java Main <port>");
    }

    try {
      ExecutorService executor = Executors.newFixedThreadPool(10);
      ServerSocket serverSocket = new ServerSocket(4221);

      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);
      while (true) {
        Socket client = serverSocket.accept(); // Wait for connection from client.
        System.out.println("accepted new connection");
        executor.submit(() -> {
          handleConnection(client);
        });
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

  private static void handleConnection(Socket client) {
    ArrayList<String> list = new ArrayList<>();
    String line;
    String response = "HTTP/1.1 200 OK\r\n\r\n";
    try {
      OutputStream outputStream = client.getOutputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
      PrintWriter writer = new PrintWriter(outputStream, true);
      String requestLine = reader.readLine();
      while ((line = reader.readLine()) != null && !line.isEmpty()) {
        list.add(line);
        if (line.trim().isEmpty()) {
          break;
        }
      }
      String[] parts = requestLine.split(" ");

      if (parts[1].equals("/user-agent")) {
        String header = list.get(1);
        String[] userParts = header.split(":");
        writer.print("HTTP/1.1 200 OK\r\n");
        writer.print("Content-Type: text/plain\r\n");
        writer.print("Content-Length: " + userParts[1].trim().length() + "\r\n\r\n");
        writer.print(userParts[1].trim() + "\r\n");
        writer.flush();
        return;
      }
      if (parts[1].equals("/")) {
        writer.print(response);
        writer.flush();
        return;
      }
      String[] urlParts = parts[1].split("/");
      if (!urlParts[1].equals("echo")) {
        writer.print("HTTP/1.1 404 Not Found\r\n\r\n");
        writer.flush();
      } else {
       String word = urlParts[2];
       writer.print("HTTP/1.1 200 OK\r\n");
       writer.print("Content-Type: text/plain\r\n");
       writer.print("Content-Length: " + word.length() + "\r\n");
       writer.print("\r\n");
       writer.print(word);
       writer.print("\r\n");
       writer.flush();
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
      return;
    }
  }
}
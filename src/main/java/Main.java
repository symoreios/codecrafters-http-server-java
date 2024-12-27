import java.io.*;
import java.nio.file.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");
    Path directory;
    if (args.length > 0) {
      directory = Paths.get(args[1]);
    } else {
      directory = null;
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
          handleConnection(client, directory);
        });
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

  private static void handleConnection(Socket client, Path directory) {
    ArrayList<String> list = new ArrayList<>();
    String line;
    boolean isBody = false;
    List<String> body = new ArrayList<>();
    try {
    OutputStream outputStream = client.getOutputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
    PrintWriter writer = new PrintWriter(outputStream, true);

      while ((line = reader.readLine()) != null && !line.isEmpty()) {
        if (line.trim().isEmpty()) {
          isBody = true;
        } else if (isBody) {
          body.add(line);
        } else {
          list.add(line);
        }
      }

      if (!list.isEmpty()) {
        HttpRequest httpRequest;
        if (isBody) {
          httpRequest = new HttpRequest(list, body);
        } else {
          httpRequest = new HttpRequest(list);
        }
        HttpResponse httpResponse = new HttpResponse(httpRequest, directory);
        if (directory == null) {
          httpResponse.buildResponse();
        } else {
          httpResponse.directoryResponse();
        }
        writer.print(httpResponse);
        writer.flush();
      }

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
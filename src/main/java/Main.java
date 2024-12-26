import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");
    String response = "HTTP/1.1 200 OK\r\n\r\n";

    // Uncomment this block to pass the first stage

     try {
       ServerSocket serverSocket = new ServerSocket(4221);

       // Since the tester restarts your program quite often, setting SO_REUSEADDR
       // ensures that we don't run into 'Address already in use' errors
       serverSocket.setReuseAddress(true);

       Socket client = serverSocket.accept(); // Wait for connection from client.
       System.out.println("accepted new connection");
       OutputStream outputStream = client.getOutputStream();
       PrintWriter writer = new PrintWriter(outputStream, true);

       writer.println(response);
       writer.flush();
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}

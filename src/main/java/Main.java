import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");
    String response = "HTTP/1.1 200 OK\r\n\r\n";
    PrintWriter writer;
    BufferedReader reader;
    String in;
    String word;
    ArrayList<String> list = new ArrayList<String>();

    // Uncomment this block to pass the first stage

     try {
       ServerSocket serverSocket = new ServerSocket(4221);

       // Since the tester restarts your program quite often, setting SO_REUSEADDR
       // ensures that we don't run into 'Address already in use' errors
       serverSocket.setReuseAddress(true);

       Socket client = serverSocket.accept(); // Wait for connection from client.
       System.out.println("accepted new connection");
       OutputStream outputStream = client.getOutputStream();
       reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
       writer = new PrintWriter(outputStream, true);
       in = reader.readLine();
       String headerLine;
       while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
         list.add(headerLine);
       }
       String[] parts = in.split(" ");
       if(parts[1].equals("/user-agent")) {
         String header = list.get(1);
         String[] userParts = header.split(":");
         writer.print("HTTP/1.1 200 OK\r\n");
         writer.print("Content-Type: text/plain\r\n");
         writer.print("Content-Length: " + userParts[1].trim().length() + "\r\n\r\n");
         writer.print(userParts[1].trim() + "\r\n");
         //System.out.println(userResponse);
       }
       if (parts[1].equals("/")) {
         writer.print("HTTP/1.1 200 OK\r\n\r\n");
         writer.flush();
         return;
       }

       String[] urlParts = parts[1].split("/");
       System.out.println(urlParts[0]);
       if (!urlParts[1].equals("echo")) {
         writer.print("HTTP/1.1 404 Not Found\r\n\r\n");
         writer.flush();
         return;
       }

       word = urlParts[2];
       writer.print("HTTP/1.1 200 OK\r\n");
       writer.print("Content-Type: text/plain\r\n");
       writer.print("Content-Length: " + word.length() + "\r\n");
       writer.print("\r\n");
       writer.print(word);
       writer.print("\r\n");
       writer.flush();

     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

public class HttpResponse {
    private int statusCode;
    private String statusMessage;
    private String body;
    private String contentType;
    private HttpRequest request;
    private Path directory;
    private byte[] bodyBytes;
    private int contentLength;
    private String contentEncoding;

    public byte[] getContentEncodingBytes() {
        return contentEncodingBytes;
    }

    private byte[] contentEncodingBytes;

    public HttpResponse(HttpRequest request, Path directory) {
        this.request = request;
        this.directory = directory;
        if (request.getEncoding() != null) {
            this.contentEncoding = request.getEncoding();
        }
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void buildResponse() throws IOException {
        String url = request.getUrl();
        String method = request.getMethod();

        if (url.equals("/user-agent")) {
            handleUserAgent();
        } else if (url.equals("/")) {
            handleRoot();
        } else if (url.startsWith("/echo/")) {
            handleEcho();
        } else {
            handleNotFound();
        }
    }

    public void directoryResponse(){

        if ("GET".equals(request.getMethod())) {
            handleGet();
        } else if ("POST".equals(request.getMethod())) {
            handlePost();
        }
    }

    private void handleGet() {
        if ("GET".equals(request.getMethod()) && request.getUrl().startsWith("/files") && directory != null) {
            String fileName = request.getUrl().substring("/files/".length());
            Path filePath = directory.resolve(fileName);
            try {
                if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                    setStatusCode(200);
                    setStatusMessage("OK");
                    setContentType("application/octet-stream");
                    this.bodyBytes = Files.readAllBytes(filePath);
                } else {
                    setStatusCode(404);
                    setStatusMessage("Not Found");
                    this.bodyBytes = null;
                }
            } catch (IOException e) {
                this.statusCode = 500;
                this.statusMessage = "Internal Server Error";
            }
        }
    }

    private void handlePost() {
        if ("POST".equals(request.getMethod()) && request.getUrl().startsWith("/files") && directory != null) {
            String fileName = request.getUrl().substring("/files/".length());
            Path filePath = directory.resolve(fileName);
            String body = request.getBody().getFirst();
            System.out.println("body: " + body);
            try {
                    Files.writeString(filePath, body);
                    setStatusCode(201);
                    setStatusMessage("Created");
                System.out.println("past created");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleUserAgent() {
        String userAgent = "Unknown";
        for (String header : request.getHeaders()) {
            if (header.toLowerCase().startsWith("user-agent:")) {
                userAgent = header.substring("user-agent:".length()).trim();
                break;
            }
        }
        setStatusCode(200);
        setStatusMessage("OK");
        setContentType("text/plain");
        setContentLength(userAgent.length());
        setBody(userAgent);
    }

    private void handleRoot() {
        setStatusCode(200);
        setStatusMessage("OK");
        setContentType("text/plain");
        setBody("Hello from the root!");
    }

    private void handleEcho() throws IOException {
        String url = request.getUrl();
        String[] parts = url.split("/", 3);

        if (parts.length == 3) {
            String word = parts[2];
            setStatusCode(200);
            setStatusMessage("OK");
            setContentType("text/plain");
            if (this.contentType == null) {
                setContentLength(word.length());
                setBody(word);
            } else {
                compressString(word);
            }
        } else {
            setStatusCode(404);
            setStatusMessage("Not Found");
            setContentType("text/plain");
            setBody("Invalid echo request");
        }
    }

    private void handleNotFound() {
        setStatusCode(404);
        setStatusMessage("Not Found");
        setContentType("text/plain");
        setBody("The requested URL was not found on this server.");
    }

    private void compressString(String word) throws IOException {
        if (word == null || word.isEmpty()) {
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            byte[] input = word.getBytes(StandardCharsets.UTF_8);
            gzipOutputStream.write(input);
        }
        this.contentEncodingBytes = byteArrayOutputStream.toByteArray();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(statusMessage)
                .append("\r\n");
        if (contentEncoding != null) {
            sb.append("Content-Encoding: ").append(contentEncoding).append("\r\n");
        }

        if (contentType != null) {
            sb.append("Content-Type: ").append(contentType).append("\r\n");
        }
        if (bodyBytes != null) {
            sb.append("Content-Length: ").append(bodyBytes.length).append("\r\n");
        }
        if (body != null) {
            sb.append("Content-Length: ").append(contentLength).append("\r\n");
        }

        sb.append("\r\n");

        if (bodyBytes != null) {
            String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        }
        if (body != null) {
            sb.append(body).append("\r\n");
        }

        return sb.toString();
    }
}
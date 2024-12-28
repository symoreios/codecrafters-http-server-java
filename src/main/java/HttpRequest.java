import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private String url;
    private String method;
    private List<String> body;
    private List<String> headers;
    private ArrayList<String> request = new ArrayList<>();
    private String encoding;
    private ArrayList<String> validEncodings = new ArrayList<>(List.of("gzip"));

    public HttpRequest(ArrayList<String> request) {
        this.request = request;
        splitMethod(request.getFirst());
        List<String> requestHeaders = request.subList(1, request.size());
        setHeaders(requestHeaders);
        for (String header : requestHeaders) {
            if (header.contains("Accept-Encoding")) {
                setEncoding(header);
            }
        }

    }

    public HttpRequest(ArrayList<String> request, List<String> body) {
        this.request = request;
        splitMethod(request.getFirst());
        List<String> requestHeaders = request.subList(1, request.size());
        setHeaders(requestHeaders);
        setBody(body);
        for (String header : requestHeaders) {
            if (header.contains("Accept-Encoding")) {
                setEncoding(header);
            }
        }

    }


    private void splitMethod(String method) {
        String[] tmp = method.split(" ");
        setUrl(tmp[1]);
        this.method = tmp[0];
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    private void setUrl(String url) {
        this.url = url;
    }

    private void setBody(List<String> body) {
        this.body = body;
    }

    private void setEncoding(String encoding) {
        String[] tmp = encoding.split(" ");
        for (String s : tmp) {
            if (validEncodings.contains(s.trim().replace(",",""))) {
                this.encoding = s.trim().replace(",","");
                break;
            }
        }
    }

    public String getUrl() {
        return this.url;
    }

    public String getMethod() {
        return this.method;
    }

    public List<String> getBody() {
        return this.body;
    }

    public List<String> getHeaders() {
        return this.headers;
    }

    public String getEncoding() {
        return this.encoding;
    }

}

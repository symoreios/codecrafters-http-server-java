import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private String url;
    private String method;
    private List<String> body;
    private List<String> headers;
    private ArrayList<String> request = new ArrayList<>();

    public HttpRequest(ArrayList<String> request) {
        this.request = request;
        splitMethod(request.getFirst());
        List<String> requestHeaders = request.subList(1, request.size());
        setHeaders(requestHeaders);

    }

    public HttpRequest(ArrayList<String> request, List<String> body) {
        this.request = request;
        splitMethod(request.getFirst());
        List<String> requestHeaders = request.subList(1, request.size());
        setHeaders(requestHeaders);
        setBody(body);

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

}

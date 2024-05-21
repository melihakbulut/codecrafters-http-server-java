import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HttpResponse {

    public static final String NEW_LINE = "\r\n";
    public static final String HTTP_PREFIX = "HTTP/1.1 ";

    private HttpStatus httpStatus;
    private Map<String, String> headers = new HashMap<String, String>();
    private byte[] body;

    public HttpResponse(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void setBody(String body) {
        this.body = body.getBytes();
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

}

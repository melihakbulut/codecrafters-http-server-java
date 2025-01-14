import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HttpResponse {

    private HttpStatus httpStatus;
    private Map<String, String> headers = new HashMap<String, String>();
    private byte[] body;

    public HttpResponse(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setBody(String body) {
        this.body = body.getBytes();
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

}

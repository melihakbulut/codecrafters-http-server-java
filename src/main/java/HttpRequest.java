import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpRequest {

    private String endpoint;
    private Map<String, String> headers;
    private String body;
}

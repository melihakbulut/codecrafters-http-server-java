import lombok.Getter;

@Getter
public enum HttpStatus {

                        SUCCESS("200 OK"), CREATED("201 Created\""), NOT_FOUND("404 Not Found");

    private final String value;

    private HttpStatus(String value) {
        this.value = value;
    }

    public static HttpStatus fromValue(String value) {
        for (HttpStatus httpValue : HttpStatus.values()) {
            if (httpValue.value.equals(value)) {
                return httpValue;
            }
        }
        return null;
    }
}

package tk.dnstk.imgate.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidAccessException extends RuntimeException {

    public InvalidAccessException(String msg) {
        super(msg);
    }

}

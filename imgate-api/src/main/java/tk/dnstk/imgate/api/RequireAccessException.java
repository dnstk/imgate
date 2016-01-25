package tk.dnstk.imgate.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RequireAccessException extends RuntimeException {

    public RequireAccessException(String msg) {
        super(msg);
    }

}

package tk.dnstk.imgate.api.service;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tk.dnstk.imgate.api.InvalidAccessException;
import tk.dnstk.imgate.api.InvalidArgumentException;
import tk.dnstk.imgate.api.ObjectNotFoundException;
import tk.dnstk.imgate.api.RequireAccessException;

@ControllerAdvice
public class ImgateExceptionAdvice {


    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    VndErrors invalidAccessExceptionHandler(InvalidAccessException ex) {
        return new VndErrors("error", ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    VndErrors methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex) {
        Throwable th = ex;
        while (th != null) {
            if (th instanceof InvalidAccessException) {
                return invalidAccessExceptionHandler((InvalidAccessException) th);
            } else {
                th = th.getCause();
            }
        }
        throw ex;
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors invalidArgumentExceptionHandler(InvalidArgumentException ex) {
        return new VndErrors("error", ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors objectNotFoundExceptionHandler(ObjectNotFoundException ex) {
        return new VndErrors("error", ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    VndErrors objectNotFoundExceptionHandler(RequireAccessException ex) {
        return new VndErrors("error", ex.getMessage());
    }

}

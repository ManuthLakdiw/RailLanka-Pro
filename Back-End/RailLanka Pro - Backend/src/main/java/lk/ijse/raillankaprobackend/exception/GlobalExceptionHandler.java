package lk.ijse.raillankaprobackend.exception;

import lk.ijse.raillankaprobackend.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<String> userNameAlreadyExistsExceptionHandler(UserNameAlreadyExistsException ex){
        return new ApiResponse<>(
                409,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(IdGenerateLimitReachedException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ApiResponse<String> idGenerateLimitReachedExceptionHandler(IdGenerateLimitReachedException ex){
        return new ApiResponse<>(
                429,
                ex.getMessage(),
                null
        );
    }
}

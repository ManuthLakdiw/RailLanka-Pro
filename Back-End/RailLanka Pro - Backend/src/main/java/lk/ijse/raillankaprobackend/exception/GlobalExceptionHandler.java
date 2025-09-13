package lk.ijse.raillankaprobackend.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lk.ijse.raillankaprobackend.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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

    @ExceptionHandler({
            StationNameAlreadyExistsException.class,
            TrainNameAlreadyExistsException.class,
            UserNameAlreadyExistsException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<String> alreadyExistsExceptionHandler(Exception ex){
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

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<String> badCredentialExceptionHandler(BadCredentialsException ex){
        return new ApiResponse<>(
                401,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<String> tokenExpiredExceptionHandler(TokenExpiredException ex){
        return new ApiResponse<>(
                401,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> runtimeExceptionHandler(RuntimeException ex){
        return new ApiResponse<>(
                500,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> expiredJwtExceptionHandler(ExpiredJwtException ex){
        return new ApiResponse<>(
                500,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<String> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex){
        return new ApiResponse<>(
                405,
                ex.getMessage(),
                null

        );
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> illegalArgumentExceptionHandler(IllegalArgumentException ex){
        return new ApiResponse<>(
                400,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(ScheduleConflictException.class)
    public ApiResponse<String> scheduleConflictExceptionHandler(ScheduleConflictException ex){
        return new ApiResponse<>(
                409,
                ex.getMessage(),
                null
        );
    }

}

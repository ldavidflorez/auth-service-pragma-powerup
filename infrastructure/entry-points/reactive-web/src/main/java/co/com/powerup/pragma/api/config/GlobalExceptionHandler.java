package co.com.powerup.pragma.api.config;

import co.com.powerup.pragma.api.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<UserController.ErrorResponse>> handleResponseStatusException(ResponseStatusException ex) {
        logger.warn("ResponseStatusException caught: {} - {}", ex.getStatusCode(), ex.getReason());
        
                       UserController.ErrorResponse errorResponse = UserController.ErrorResponse.builder()
                       .message(ex.getReason())
                       .code(getErrorCode(HttpStatus.valueOf(ex.getStatusCode().value())))
                       .build();
        
        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(errorResponse));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<UserController.ErrorResponse>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("IllegalArgumentException caught: {}", ex.getMessage());
        
                       UserController.ErrorResponse errorResponse = UserController.ErrorResponse.builder()
                       .message(ex.getMessage())
                       .code("VALIDATION_ERROR")
                       .build();
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }
    
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<UserController.ErrorResponse>> handleGenericException(Exception ex) {
        logger.error("Unexpected exception caught: {}", ex.getMessage(), ex);
        
                       UserController.ErrorResponse errorResponse = UserController.ErrorResponse.builder()
                       .message("Internal server error")
                       .code("INTERNAL_ERROR")
                       .build();
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
    
    private String getErrorCode(HttpStatus status) {
        if (status.is4xxClientError()) {
            return "CLIENT_ERROR";
        } else if (status.is5xxServerError()) {
            return "SERVER_ERROR";
        }
        return "UNKNOWN_ERROR";
    }
}

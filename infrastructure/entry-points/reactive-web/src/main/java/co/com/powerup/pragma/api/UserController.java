package co.com.powerup.pragma.api;

import co.com.powerup.pragma.model.user.User;
import co.com.powerup.pragma.usecase.saveuser.SaveUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user management")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final SaveUserUseCase saveUserUseCase;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Register new user",
        description = "Creates a new user in the system with unique email validation"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public Mono<UserResponse> registerUser(@RequestBody User user) {
        logger.info("Received user registration request");
        return saveUserUseCase.saveUserWithEmailValidation(user)
                .map(savedUser -> {
                    logger.info("User registered successfully - ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());
                    return UserResponse.builder()
                            .id(savedUser.getId())
                            .firstName(savedUser.getFirstName())
                            .lastName(savedUser.getLastName())
                            .email(savedUser.getEmail())
                            .registrationDate(savedUser.getRegistrationDate() != null ? savedUser.getRegistrationDate().toString() : null)
                            .build();
                })
                .doOnError(error -> logger.error("Error in controller when registering user: {}", error.getMessage()));
    }
    
    /**
     * Response DTO for user operations
     */
    @Schema(description = "User response")
    public static class UserResponse {
        @Schema(description = "Unique user ID", example = "1")
        private Long id;
        
        @Schema(description = "User first name", example = "John")
        private String firstName;
        
        @Schema(description = "User last name", example = "Doe")
        private String lastName;
        
        @Schema(description = "User email address", example = "john.doe@email.com")
        private String email;
        
        @Schema(description = "User registration date", example = "2025-08-24")
        private String registrationDate;
        
        // Builder pattern
        public static UserResponseBuilder builder() {
            return new UserResponseBuilder();
        }
        
        public static class UserResponseBuilder {
            private UserResponse response = new UserResponse();
            
            public UserResponseBuilder id(Long id) {
                response.id = id;
                return this;
            }
            
            public UserResponseBuilder firstName(String firstName) {
                response.firstName = firstName;
                return this;
            }
            
            public UserResponseBuilder lastName(String lastName) {
                response.lastName = lastName;
                return this;
            }
            
            public UserResponseBuilder email(String email) {
                response.email = email;
                return this;
            }
            
            public UserResponseBuilder registrationDate(String registrationDate) {
                response.registrationDate = registrationDate;
                return this;
            }
            
            public UserResponse build() {
                return response;
            }
        }
        
        // Getters
        public Long getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
        public String getRegistrationDate() { return registrationDate; }
    }
    
    /**
     * Error response DTO
     */
    @Schema(description = "Error response")
    public static class ErrorResponse {
        @Schema(description = "Error message", example = "Validation error: Email already exists")
        private String message;
        
        @Schema(description = "Error code", example = "VALIDATION_ERROR")
        private String code;
        
        // Builder pattern
        public static ErrorResponseBuilder builder() {
            return new ErrorResponseBuilder();
        }
        
        public static class ErrorResponseBuilder {
            private ErrorResponse response = new ErrorResponse();
            
            public ErrorResponseBuilder message(String message) {
                response.message = message;
                return this;
            }
            
            public ErrorResponseBuilder code(String code) {
                response.code = code;
                return this;
            }
            
            public ErrorResponse build() {
                return response;
            }
        }
        
        // Getters
        public String getMessage() { return message; }
        public String getCode() { return code; }
    }
}

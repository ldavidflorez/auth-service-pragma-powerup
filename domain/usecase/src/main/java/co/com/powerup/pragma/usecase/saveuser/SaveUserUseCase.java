package co.com.powerup.pragma.usecase.saveuser;

import co.com.powerup.pragma.model.user.User;
import co.com.powerup.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SaveUserUseCase {
    
    private final UserRepository userRepository;
    
    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{7,15}$");
    
    /**
     * Saves a new user in the system
     * @param user the user to save
     * @return Mono<User> the saved user with generated ID and registration date
     */
    public Mono<User> saveUser(User user) {
        return Mono.just(user)
                .doOnNext(this::validateUser)
                .doOnNext(User::setRegistrationDate)
                .flatMap(userRepository::save);
    }
    
    /**
     * Saves a new user with email validation in a transactional operation
     * This method demonstrates atomic operations as required by the user story
     * @param user the user to save
     * @return Mono<User> the saved user with generated ID and registration date
     */
    public Mono<User> saveUserWithEmailValidation(User user) {
        return Mono.just(user)
                .doOnNext(this::validateUser)
                .doOnNext(User::setRegistrationDate)
                .flatMap(u -> userRepository.existsByEmail(u.getEmail())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new IllegalArgumentException("Email already exists: " + u.getEmail()));
                            }
                            return userRepository.save(u);
                        }));
    }
    
    /**
     * Validates the user data before saving
     * @param user the user to validate
     */
    private void validateUser(User user) {
        validateNames(user.getFirstName(), "First name");
        validateNames(user.getLastName(), "Last name");
        validateEmail(user.getEmail());
        validateSalary(user.getBaseSalary());
        validateDateOfBirth(user.getDateOfBirth());
        validatePhone(user.getPhone());
        validateAddress(user.getAddress());
    }
    
    /**
     * Validates names and surnames
     */
    private void validateNames(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException(fieldName + " must have at least 2 characters");
        }
        if (name.trim().length() > 50) {
            throw new IllegalArgumentException(fieldName + " cannot exceed 50 characters");
        }
        if (!name.matches("^[a-zA-Z\\s]+$")) {
            throw new IllegalArgumentException(fieldName + " can only contain letters and spaces");
        }
    }
    
    /**
     * Validates email format and length
     */
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (email.trim().length() > 100) {
            throw new IllegalArgumentException("Email cannot exceed 100 characters");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Email must have a valid format (e.g., user@domain.com)");
        }
    }
    
    /**
     * Validates salary range
     */
    private void validateSalary(BigDecimal salary) {
        if (salary == null) {
            throw new IllegalArgumentException("Base salary cannot be null");
        }
        if (salary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Base salary cannot be negative");
        }
        if (salary.compareTo(new BigDecimal("15000000")) > 0) {
            throw new IllegalArgumentException("Base salary cannot exceed 15,000,000");
        }
    }
    
    /**
     * Validates date of birth
     */
    private void validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
        if (dateOfBirth.isBefore(LocalDate.now().minusYears(120))) {
            throw new IllegalArgumentException("Date of birth seems invalid (more than 120 years ago)");
        }
    }
    
    /**
     * Validates phone number (optional field)
     */
    private void validatePhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            if (phone.trim().length() > 15) {
                throw new IllegalArgumentException("Phone number cannot exceed 15 characters");
            }
            if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                throw new IllegalArgumentException("Phone number must contain only digits and be between 7 and 15 characters");
            }
        }
    }
    
    /**
     * Validates address (optional field)
     */
    private void validateAddress(String address) {
        if (address != null && address.trim().length() > 200) {
            throw new IllegalArgumentException("Address cannot exceed 200 characters");
        }
    }
}

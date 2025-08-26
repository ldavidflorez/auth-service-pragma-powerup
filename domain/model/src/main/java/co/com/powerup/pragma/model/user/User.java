package co.com.powerup.pragma.model.user;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private String phone;
    private String email;
    private BigDecimal baseSalary;
    private LocalDate registrationDate;
    
    public void setRegistrationDate() {
        this.registrationDate = LocalDate.now();
    }
}

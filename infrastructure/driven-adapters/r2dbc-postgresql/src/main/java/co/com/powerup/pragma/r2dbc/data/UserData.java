package co.com.powerup.pragma.r2dbc.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("usuarios")
public class UserData {
    
    @Id
    @Column("id")
    private Long id;
    
    @Column("nombres")
    private String firstName;
    
    @Column("apellidos")
    private String lastName;
    
    @Column("fecha_nacimiento")
    private LocalDate dateOfBirth;
    
    @Column("direccion")
    private String address;
    
    @Column("telefono")
    private String phone;
    
    @Column("correo_electronico")
    private String email;
    
    @Column("salario_base")
    private BigDecimal baseSalary;
    
    @Column("fecha_registro")
    private LocalDate registrationDate;
}

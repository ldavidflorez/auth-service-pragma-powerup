package co.com.powerup.pragma.r2dbc.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
    private String nombres;
    
    @Column("apellidos")
    private String apellidos;
    
    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;
    
    @Column("direccion")
    private String direccion;
    
    @Column("telefono")
    private String telefono;
    
    @Column("correo_electronico")
    private String correoElectronico;
    
    @Column("salario_base")
    private Double salarioBase;
    
    @Column("fecha_registro")
    private LocalDate fechaRegistro;
}

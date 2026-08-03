package mx.edu.utez.JuventudxTemixco.models.donations;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.edu.utez.JuventudxTemixco.models.users.Gender;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name= "donation")
public class BeanDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank (message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank (message = "El apellido Paterno es obligatorio")
    private String apellidoP;
    @NotBlank (message = "El apellido materno es obligatorio")
    private String apellidoM;
    @Email(message = "el correo debe tener un formato válido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;
    @NotNull (message = "El monto es obligatorio")
    @DecimalMin(value = "50.00", message = "EL monto no debe ser menor a 50")
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status estado;

    @Column(nullable = false, updatable = false)
    @PastOrPresent
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
    }
    private String paypal_order_id;
    private String paypal_capture_id;
}

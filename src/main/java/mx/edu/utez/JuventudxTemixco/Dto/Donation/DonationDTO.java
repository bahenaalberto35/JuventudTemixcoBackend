package mx.edu.utez.JuventudxTemixco.Dto.Donation;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.edu.utez.JuventudxTemixco.models.donations.BeanDonation;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationDTO {

        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;
        @NotBlank (message = "El nombre es obligatorio")
        private String apellidoP;
        @NotBlank (message = "El nombre es obligatorio")
        private String apellidoM;
        @Email(message = "El correo debe tener un formato válido")
        @NotBlank(message = "El correo es obligatorio")
        private String correo;
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "50.00", message = "EL monto no debe ser menor a 50")
        private BigDecimal monto;

        private String paypalOrderId;
        private String paypalCaptureId;

        public BeanDonation toEntity() {
                BeanDonation donation = new BeanDonation();

                donation.setNombre(nombre);
                donation.setApellidoP(apellidoP);
                donation.setApellidoM(apellidoM);
                donation.setCorreo(correo);
                donation.setMonto(monto);

                donation.setPaypal_order_id(paypalOrderId);
                donation.setPaypal_capture_id(paypalCaptureId);

                return donation;
        }
}

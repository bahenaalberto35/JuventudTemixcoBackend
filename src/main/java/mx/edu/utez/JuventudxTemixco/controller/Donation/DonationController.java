package mx.edu.utez.JuventudxTemixco.controller.Donation;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.JuventudxTemixco.Dto.Donation.DonationDTO;
import mx.edu.utez.JuventudxTemixco.models.donations.BeanDonation;
import mx.edu.utez.JuventudxTemixco.models.donations.Status;
import mx.edu.utez.JuventudxTemixco.service.Donation.DonationService;
import mx.edu.utez.JuventudxTemixco.service.Donation.EmailService;
import mx.edu.utez.JuventudxTemixco.service.Donation.PayPalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/Donacion")
@CrossOrigin(origins = "https://juventudxtemixco.org/", allowCredentials = "true")
@RequiredArgsConstructor
public class DonationController {

    private final PayPalService payPalService;
    private final DonationService donationService;
    private final EmailService emailService;

    @PostMapping("/")
    public ResponseEntity<?> save(@RequestBody BeanDonation donacion){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(donationService.save(donacion));
    }

    @GetMapping("/")
    public ResponseEntity<List<BeanDonation>> getAll() {
        return ResponseEntity.ok(donationService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        Optional<BeanDonation> donacion = donationService.findById(id);

        if (donacion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(donacion);
    }

    // Crear orden
    @PostMapping("/paypal/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, BigDecimal> request) {

        String orderId = payPalService.createOrder(request.get("monto"));

        return ResponseEntity.ok(
                Map.of("orderId", orderId)
        );
    }

    // Capturar orden
    @PostMapping("/paypal/capture-order")
    public ResponseEntity<?> captureOrder(@RequestBody DonationDTO request) {

        Map response = payPalService.captureOrder(request.getPaypalOrderId());

        // Verificar que PayPal realmente completó el pago
        String paypalStatus = response.get("status").toString();

        if (!"COMPLETED".equals(paypalStatus)) {
            throw new IllegalArgumentException("El pago no fue completado en la plataforma de PayPal.");
        }

        String captureId = payPalService.getCaptureId(response);

        if (captureId == null) {
            throw new IllegalArgumentException("No se pudo obtener el ID de la captura de PayPal.");
        }

        BeanDonation donation = request.toEntity();

        donation.setEstado(Status.COMPLETADA);
        donation.setPaypal_capture_id(captureId);

        donation = donationService.save(donation);

        try {
            emailService.enviarCorreoAgradecimiento(donation);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al enviar correo de agradecimiento");
        }

        return ResponseEntity.ok(donation);
    }
}

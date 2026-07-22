package mx.edu.utez.JuventudxTemixco.controller.CredencialesController;

import mx.edu.utez.JuventudxTemixco.models.users.BeanUser;
import mx.edu.utez.JuventudxTemixco.models.users.UserRepository;
import mx.edu.utez.JuventudxTemixco.models.users.UserType;
import mx.edu.utez.JuventudxTemixco.service.CredencializacionService.Credencialización;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "https://juventudxtemixco.org/") // Asegura el puerto de tu React
public class CredencialesController {


    @Autowired
    private Credencialización reporteService;

    @Autowired
    private UserRepository userRepository;


    //BENEFICIARIO
    @GetMapping("/beneficiarios/pdf")
    public ResponseEntity<byte[]> exportarReporte(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fin,

            @RequestParam(required = false)
            String busqueda) {

        try {

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("busqueda", busqueda);
            parametros.put("inicio", inicio);
            parametros.put("fin", fin);
            parametros.put("rol", "BENEFICIARIO");

            byte[] pdf = reporteService.generarReporteSQL(
                    "JuventudPorTemixco",
                    parametros);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=Reporte_Beneficiarios.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/beneficiario/{id}/pdf")
    public ResponseEntity<byte[]> exportarIndividual(@PathVariable Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("idUsuario", id);


            byte[] pdf = reporteService.generarReporteSQL(
                    "JuventudPorTemixco",
                    parametros);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Credencial_Beneficiario.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


    //AFILIADO
    @GetMapping("/afiliado/pdf")
    public ResponseEntity<byte[]> exportarReporteAfiliado(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fin,

            @RequestParam(required = false)
            String busqueda) {

        try {

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("busqueda", busqueda);
            parametros.put("inicio", inicio);
            parametros.put("fin", fin);
            parametros.put("rol", "AFILIADO");

            byte[] pdf = reporteService.generarReporteSQL(
                    "JuventudTemixcoAfiliados",
                    parametros);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=Reporte_Afiliados.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/afiliado/{id}/pdf")
    public ResponseEntity<byte[]> exportarIndividualAfiliado(@PathVariable Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("idUsuario", id);


            byte[] pdf = reporteService.generarReporteSQL(
                    "JuventudTemixcoAfiliados",
                    parametros);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Credencial_Voluntariado.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
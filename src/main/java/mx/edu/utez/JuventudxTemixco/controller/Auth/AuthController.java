package mx.edu.utez.JuventudxTemixco.controller.Auth;

import jakarta.validation.Valid;
import mx.edu.utez.JuventudxTemixco.Dto.Login.LoginDTO;
import mx.edu.utez.JuventudxTemixco.Dto.Login.LoginRequestDTO;
import mx.edu.utez.JuventudxTemixco.Dto.Login.LoginResponseDTO;
import mx.edu.utez.JuventudxTemixco.Dto.NewPassword.EmailDTO;
import mx.edu.utez.JuventudxTemixco.Dto.NewPassword.PasswordResetDTO;
import mx.edu.utez.JuventudxTemixco.Security.JwtService;
import mx.edu.utez.JuventudxTemixco.service.Auth.AuthService;
import mx.edu.utez.JuventudxTemixco.service.UsersService.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin({"https://juventudxtemixco.org/"})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO body) {

        LoginResponseDTO response = authService.login(body);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/recuperar-password")
    public ResponseEntity<String> solicitarRecuperacion(@RequestBody EmailDTO correo) {
        authService.generarYEnviarCodigo(correo.getCorreo());
        return ResponseEntity.ok("Se ha enviado un código de verificación a tu correo.");
    }

    @PostMapping("/validarCodigo")
    public ResponseEntity<Boolean> validarCodigo(@RequestBody PasswordResetDTO dto) {
        boolean esValido = authService.validarCodigo(dto.getCorreo(), dto.getCodigo());
        if (esValido) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    @PostMapping("/actualizarPassword")
    public ResponseEntity<String> actualizarPassword(@RequestBody PasswordResetDTO dto ){

        System.out.println("Correo: " + dto.getCorreo());
        System.out.println("Codigo: " + dto.getCodigo());
        System.out.println("Contraseña: " + dto.getContrasena());

        if (!authService.validarCodigo(dto.getCorreo(), dto.getCodigo())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código inválido o expirado");
        }

        authService.actualizarPassword(dto.getCorreo(), dto.getContrasena());
        return ResponseEntity.ok("Contraseña actualizada exitosamente.");
    }
}

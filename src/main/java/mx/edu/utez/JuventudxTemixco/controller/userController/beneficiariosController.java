package mx.edu.utez.JuventudxTemixco.controller.userController;

import jakarta.validation.Valid;
import mx.edu.utez.JuventudxTemixco.Dto.usersDto.AdministradorDTO;
import mx.edu.utez.JuventudxTemixco.Dto.usersDto.AfiliadoDTO;
import mx.edu.utez.JuventudxTemixco.Dto.usersDto.BeneficiarioDTO;
import mx.edu.utez.JuventudxTemixco.models.municipalities.BeanMunicipality;
import mx.edu.utez.JuventudxTemixco.models.users.BeanUser;
import mx.edu.utez.JuventudxTemixco.models.users.UserRepository;
import mx.edu.utez.JuventudxTemixco.models.users.UserType;
import mx.edu.utez.JuventudxTemixco.service.UsersService.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/beneficiarios")
@CrossOrigin(origins = "https://juventudxtemixco.org/", allowCredentials = "true")
public class beneficiariosController {

    private UserRepository userRepository;
    private UserService userService;

    public beneficiariosController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/Municipios")
    public List<BeanMunicipality> listarMunicipios(){
        return userService.listarMunicipios();
    }


    @GetMapping
    public List<BeneficiarioDTO> listaBeneficiario() {
        return userService.listarBeneficiarios();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity eliminarBeneficiario(@PathVariable Long id) {
        return userService.eliminarUsuario(id);
    }

    @PostMapping
    public BeanUser agregaBeneficiario(@Valid @RequestBody BeneficiarioDTO user) {
        return userService.createUserBeneficiario(user);
    }

    @GetMapping("/verificarCorreo")
    public ResponseEntity<Boolean> verificarCorreo(@RequestParam String email) {
        boolean existe = userRepository.existsByCorreo(email);
        return ResponseEntity.ok(existe);
    }

    @PutMapping("/{id}")
    public BeanUser actualizaBeneficiario(@Valid @RequestBody BeneficiarioDTO user, @PathVariable Long id) {
        return userService.editarBeneficiario(user, id);
    }



    //Filtros
    @GetMapping("/filtro")
    public List<BeneficiarioDTO> rangoFechaBeneficiario(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {

        return userService.buscarConFiltrosBeneficiario(fechaInicio, fechaFin);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<BeneficiarioDTO>> buscarPorNombreYApellidos(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "apellidoP", required = false) String apellidoP,
            @RequestParam(value = "apellidoM", required = false) String apellidoM) {

        List<BeneficiarioDTO> usuarios =
                userService.buscarPorNombreYApellidosBeneficiario(
                        nombre,
                        apellidoP,
                        apellidoM
                );

        return ResponseEntity.ok(usuarios);
    }


}

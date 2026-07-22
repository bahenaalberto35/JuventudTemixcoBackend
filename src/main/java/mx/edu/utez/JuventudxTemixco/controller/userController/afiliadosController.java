package mx.edu.utez.JuventudxTemixco.controller.userController;


import jakarta.validation.Valid;
import mx.edu.utez.JuventudxTemixco.Dto.usersDto.AfiliadoDTO;
import mx.edu.utez.JuventudxTemixco.Dto.usersDto.BeneficiarioDTO;
import mx.edu.utez.JuventudxTemixco.models.users.BeanUser;
import mx.edu.utez.JuventudxTemixco.service.UsersService.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/afiliados")
@CrossOrigin(origins = "https://juventudxtemixco.org/", allowCredentials = "true")
public class afiliadosController {


    private UserService userService;

    public afiliadosController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public List<AfiliadoDTO> listaAfiliados() {
        return userService.listarAfiliados();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity eliminaAfiliado(@PathVariable Long id) {
        return userService.eliminarUsuario(id);
    }

    @PostMapping
    public BeanUser agregaAfiliado(@Valid @RequestBody AfiliadoDTO user) throws IOException {
        return userService.createUserAfiliado(user);
    }

    @PutMapping("/{id}")
    public BeanUser actualizaAfiliado(@Valid @RequestBody AfiliadoDTO user, @PathVariable Long id) throws IOException {
        return userService.editarAfiliado(user, id);
    }



    //Filtros
    @GetMapping("/buscar")
    public ResponseEntity<List<AfiliadoDTO>> buscarPorNombreYApellidos(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "apellidoP", required = false) String apellidoP,
            @RequestParam(value = "apellidoM", required = false) String apellidoM) {

        List<AfiliadoDTO> usuarios =
                userService.buscarPorNombreYApellidosAfiliado(
                        nombre,
                        apellidoP,
                        apellidoM
                );

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/filtro") //
    public List<AfiliadoDTO> rangoFechaAfiliado( @RequestParam LocalDate fechaInicio,
                                              @RequestParam LocalDate fechaFin) {

        return userService.buscarConFiltrosAfiliado(fechaInicio, fechaFin);
    }

}

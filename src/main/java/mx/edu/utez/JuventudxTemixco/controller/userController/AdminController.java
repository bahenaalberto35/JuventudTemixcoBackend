package mx.edu.utez.JuventudxTemixco.controller.userController;


import jakarta.validation.Valid;
import mx.edu.utez.JuventudxTemixco.Dto.usersDto.AdministradorDTO;
import mx.edu.utez.JuventudxTemixco.models.users.BeanUser;
import mx.edu.utez.JuventudxTemixco.service.UsersService.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("https://juventudxtemixco.org/")
public class AdminController {

    private AdminService adminService;


    public AdminController(AdminService adminService) {
        this.adminService = adminService;

    }


    @GetMapping
    public List<AdministradorDTO> Administradores() {
        try {
            return adminService.listarAdministradores();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping
    public BeanUser createUser(@Valid @RequestBody AdministradorDTO datos) {
        try {
            return adminService.createUserAdmin(datos);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping("/{id}") //
    public BeanUser updateUser(@Valid @RequestBody AdministradorDTO datos, @PathVariable long id) {
        return adminService.editarAdmin(datos, id);
    }


    @DeleteMapping("/{id}") //
    public ResponseEntity deleteUser(@PathVariable long id) {
        return adminService.eliminarUsuario(id);
    }


    // filtro de busqueda
    @GetMapping("/buscar")
    public ResponseEntity<List<AdministradorDTO>> buscarPorNombreYApellidos(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "apellidoP", required = false) String apellidoP,
            @RequestParam(value = "apellidoM", required = false) String apellidoM) {

        List<AdministradorDTO> usuarios =
                adminService.buscarPorNombreYApellidos(
                        nombre,
                        apellidoP,
                        apellidoM
                );

        return ResponseEntity.ok(usuarios);
    }





}

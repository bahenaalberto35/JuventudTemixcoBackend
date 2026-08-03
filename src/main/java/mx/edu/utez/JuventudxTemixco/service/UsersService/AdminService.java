package mx.edu.utez.JuventudxTemixco.service.UsersService;

import mx.edu.utez.JuventudxTemixco.Dto.usersDto.AdministradorDTO;
import mx.edu.utez.JuventudxTemixco.Dto.usersDto.AfiliadoDTO;
import mx.edu.utez.JuventudxTemixco.Dto.usersDto.BeneficiarioDTO;
import mx.edu.utez.JuventudxTemixco.models.municipalities.BeanMunicipality;
import mx.edu.utez.JuventudxTemixco.models.municipalities.MunicipalityRepository;
import mx.edu.utez.JuventudxTemixco.models.users.BeanUser;
import mx.edu.utez.JuventudxTemixco.models.users.UserRepository;
import mx.edu.utez.JuventudxTemixco.models.users.UserType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class AdminService {

    private MunicipalityRepository municipalityRepository;

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public AdminService(MunicipalityRepository municipalityRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.municipalityRepository = municipalityRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public BeanUser createUserAdmin(AdministradorDTO datos) {

        BeanUser user = new BeanUser();

        if (userRepository.existsByCorreo(datos.getCorreo())) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado, intenta con otro correo");
        }

        user.setNombre(datos.getNombre());
        user.setApellidoP(datos.getApellidoP());
        user.setApellidoM(datos.getApellidoM());
        user.setCorreo(datos.getCorreo());
        user.setContrasena(passwordEncoder.encode(datos.getContrasena()));
        user.setRol(UserType.ADMIN);

        return userRepository.save(user);

    }

    public List<AdministradorDTO> buscarPorNombreYApellidos (String nombre, String apellidoP, String apellidoM) {

        String buscarNombre = (nombre != null && !nombre.isBlank()) ? nombre : null;
        String buscarApellidoP = (apellidoP != null && !apellidoP.isBlank()) ? apellidoP : null;
        String buscarApellidoM = (apellidoM != null && !apellidoM.isBlank()) ? apellidoM : null;

        List<BeanUser> usuarios = userRepository.BusquedaNombre(
                buscarNombre,
                buscarApellidoP,
                buscarApellidoM,
                UserType.ADMIN
        );

        return usuarios.stream()
                .map(user -> {
                    AdministradorDTO dto = new AdministradorDTO();

                    dto.setId(user.getId());
                    dto.setNombre(user.getNombre());
                    dto.setApellidoP(user.getApellidoP());
                    dto.setApellidoM(user.getApellidoM());
                    dto.setCorreo(user.getCorreo());

                    return dto;
                })
                .toList();
    }

    public ResponseEntity eliminarUsuario(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("El usuario que intentas eliminar no existe");
        }
        userRepository.deleteById(id);
        return null;
    }

    public BeanUser editarAdmin(AdministradorDTO datos, Long id) {

        BeanUser existente = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        existente.setId(datos.getId());
        existente.setNombre(datos.getNombre());
        existente.setApellidoP(datos.getApellidoP());
        existente.setApellidoM(datos.getApellidoM());

        if (datos.getContrasena() != null && !datos.getContrasena().trim().isEmpty()) {
            existente.setContrasena(passwordEncoder.encode(datos.getContrasena()));
        }

        return userRepository.save(existente);


    }


    public List<AdministradorDTO> listarAdministradores() {
        List<BeanUser> administradorEntidad = userRepository.findByRol(UserType.ADMIN);

        return administradorEntidad.stream()
                .map(user -> {
                    AdministradorDTO dto = new AdministradorDTO();

                    dto.setId(user.getId());
                    dto.setNombre(user.getNombre());
                    dto.setApellidoP(user.getApellidoP());
                    dto.setApellidoM(user.getApellidoM());
                    dto.setCorreo(user.getCorreo());



                    return dto;
                })
                .toList();
    }







}

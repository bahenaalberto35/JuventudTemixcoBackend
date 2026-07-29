package mx.edu.utez.JuventudxTemixco.service.UsersService;

import jakarta.validation.constraints.Size;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@Service
public class UserService {

    private MunicipalityRepository municipalityRepository;

    private UserRepository userRepository;


    public UserService(MunicipalityRepository municipalityRepository, UserRepository userRepository) {
        this.municipalityRepository = municipalityRepository;
        this.userRepository = userRepository;

    }

    // Beneficiario
    public BeanUser createUserBeneficiario(BeneficiarioDTO datos){

        BeanUser user = new BeanUser();
        BeanMunicipality municipio = municipalityRepository.getReferenceById(datos.getId_Municipio());

        if(userRepository.existsByCorreo(datos.getCorreo())){
            throw new IllegalArgumentException("El correo ya se encuentra registrado, intenta con otro correo");
        }

        user.setNombre(datos.getNombre());
        user.setApellidoP(datos.getApellidoP());
        user.setApellidoM(datos.getApellidoM());
        user.setGenero(datos.getGenero());
        user.setEdad(datos.getEdad());
        user.setTelefono(datos.getTelefono());
        user.setMunicipio(municipio);
        user.setColonia(datos.getColonia());
        user.setCorreo(datos.getCorreo());
        user.setRol(UserType.BENEFICIARIO);
        if (datos.getFoto() != null && !datos.getFoto().isEmpty()) {
            try {
                byte[] imagenEnBytes =
                        java.util.Base64.getMimeDecoder()
                                .decode(datos.getFoto().trim());

                user.setFoto(
                        convertirImagen(imagenEnBytes)
                );
            } catch (IllegalArgumentException e) {
                System.err.println("Error al decodificar la foto del afiliado: " + e.getMessage());
                user.setFoto(null);
            }
        }


        return userRepository.save(user);

    }

    public BeanUser editarBeneficiario(BeneficiarioDTO datos, Long id) {

        BeanUser existente = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        BeanMunicipality municipio = municipalityRepository.getReferenceById(datos.getId_Municipio());


        existente.setNombre(datos.getNombre());
        existente.setApellidoP(datos.getApellidoP());
        existente.setApellidoM(datos.getApellidoM());
        existente.setGenero(datos.getGenero());
        existente.setMunicipio(municipio);
        existente.setColonia(datos.getColonia());
        existente.setCorreo(datos.getCorreo());
        existente.setTelefono(datos.getTelefono());
        if (datos.getFoto() != null && !datos.getFoto().isEmpty()) {
            try {
                byte[] imagenEnBytes = java.util.Base64.getMimeDecoder().decode(datos.getFoto().trim());
                byte[] fotoConvertida = convertirImagen(imagenEnBytes);


                if (fotoConvertida != null) {
                    existente.setFoto(fotoConvertida);
                } else {
                    System.err.println("No se pudo convertir la nueva imagen, se conservará la anterior.");
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Error al decodificar la foto del beneficiario: " + e.getMessage());

            }
        }

        return userRepository.save(existente);


    }

    public List<BeneficiarioDTO> listarBeneficiarios() {
        List<BeanUser> beneficiariosEntidad = userRepository.findByRol(UserType.BENEFICIARIO);

        return beneficiariosEntidad.stream()
                .map(user -> {
                    BeneficiarioDTO dto = new BeneficiarioDTO();

                    dto.setId(user.getId());
                    dto.setNombre(user.getNombre());
                    dto.setApellidoP(user.getApellidoP());
                    dto.setApellidoM(user.getApellidoM());
                    dto.setGenero(user.getGenero());
                    dto.setEdad(user.getEdad());
                    dto.setTelefono(user.getTelefono());
                    dto.setId_Municipio(user.getMunicipio().getId());
                    dto.setMunicipio(user.getMunicipio().getNombre());
                    dto.setColonia(user.getColonia());
                    dto.setCorreo(user.getCorreo());
                    if (user.getFoto() != null) {
                        String base64String = java.util.Base64.getEncoder().encodeToString(user.getFoto());
                        dto.setFoto(base64String);
                    }


                    return dto;
                })
                .toList();
    }

    public List<BeanMunicipality> listarMunicipios() {
        return municipalityRepository.findAll();

    }



    // Afiliado
    public BeanUser createUserAfiliado(AfiliadoDTO datos) throws IOException {

        BeanUser user = new BeanUser();

        user.setNombre(datos.getNombre());
        user.setApellidoP(datos.getApellidoP());
        user.setApellidoM(datos.getApellidoM());
        user.setGenero(datos.getGenero());
        user.setEdad(datos.getEdad());
        user.setTelefono(datos.getTelefono());
        user.setRol(UserType.AFILIADO);
        user.setFechaRegistro(LocalDate.now());

        if (datos.getFoto() != null && !datos.getFoto().isEmpty()) {
            try {
                byte[] imagenEnBytes =
                        java.util.Base64.getMimeDecoder()
                                .decode(datos.getFoto().trim());

                user.setFoto(
                        convertirImagen(imagenEnBytes)
                );
            } catch (IllegalArgumentException e) {
                System.err.println("Error al decodificar la foto del afiliado: " + e.getMessage());
                user.setFoto(null);
            }
        }
        return userRepository.save(user);

    }





    public BeanUser editarAfiliado(AfiliadoDTO datos, Long id) throws IOException {

        BeanUser existente = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        existente.setId(existente.getId());
        existente.setNombre(datos.getNombre());
        existente.setApellidoP(datos.getApellidoP());
        existente.setApellidoM(datos.getApellidoM());
        existente.setEdad(datos.getEdad());
        existente.setTelefono(datos.getTelefono());
        existente.setGenero(datos.getGenero());


        if (datos.getFoto() != null && !datos.getFoto().isEmpty()) {
            try {
                byte[] imagenEnBytes = java.util.Base64.getMimeDecoder().decode(datos.getFoto().trim());
                byte[] fotoConvertida = convertirImagen(imagenEnBytes);

                if (fotoConvertida != null) {
                    existente.setFoto(fotoConvertida);
                } else {
                    System.err.println("No se pudo convertir la nueva imagen, se conservará la anterior.");
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Error al decodificar la foto del beneficiario: " + e.getMessage());


            }
        }

            return userRepository.save(existente);

    }


    public List<AfiliadoDTO> listarAfiliados() {
        List<BeanUser> afiliadosEntidad = userRepository.findByRol(UserType.AFILIADO);

        return afiliadosEntidad.stream()
                .map(user -> {
                    AfiliadoDTO dto = new AfiliadoDTO();

                    dto.setId(user.getId());
                    dto.setNombre(user.getNombre());
                    dto.setApellidoP(user.getApellidoP());
                    dto.setApellidoM(user.getApellidoM());
                    dto.setGenero(user.getGenero());
                    dto.setEdad(user.getEdad());
                    dto.setTelefono(user.getTelefono());
                    if (user.getFoto() != null) {
                        String base64String = java.util.Base64.getEncoder().encodeToString(user.getFoto());
                        dto.setFoto("data:image/jpeg;base64," + base64String);
                    }


                    return dto;
                })
                .toList();
    }


    // eliminacion
    public ResponseEntity eliminarUsuario(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("El usuario que intentas eliminar no existe");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }




    // Filtros
    public List<BeneficiarioDTO> buscarConFiltrosBeneficiario(LocalDate inicio, LocalDate fin) {


        if (inicio == null) inicio = LocalDate.now().minusMonths(1); // Hace un mes
        if (fin == null) fin = LocalDate.now().plusMonths(1);       // En un mes

        List<BeanUser> usuarios =
                userRepository.filtrarDinamico(inicio, fin, UserType.BENEFICIARIO);


        return usuarios.stream()
                .map(this::convertirABeneficiarioDTO)
                .toList();
    }




    public List<AfiliadoDTO> buscarConFiltrosAfiliado(LocalDate inicio, LocalDate fin) {


        if (inicio == null) inicio = LocalDate.now().minusMonths(1); // Hace un mes
        if (fin == null) fin = LocalDate.now().plusMonths(1);       // En un mes

        List<BeanUser> usuarios =
                userRepository.filtrarDinamico(inicio, fin, UserType.AFILIADO);


        return usuarios.stream()
                .map(this::convertirAfiliadoDTO)
                .toList();
    }



    public List<BeneficiarioDTO> buscarPorNombreYApellidosBeneficiario (String nombre, String apellidoP, String apellidoM) {

        String buscarNombre = (nombre != null && !nombre.isBlank()) ? nombre : null;
        String buscarApellidoP = (apellidoP != null && !apellidoP.isBlank()) ? apellidoP : null;
        String buscarApellidoM = (apellidoM != null && !apellidoM.isBlank()) ? apellidoM : null;

        List<BeanUser> usuarios = userRepository.BusquedaNombre(
                buscarNombre,
                buscarApellidoP,
                buscarApellidoM,
                UserType.BENEFICIARIO

        );

        return usuarios.stream()
                .map(user -> {
                    BeneficiarioDTO dto = new BeneficiarioDTO();

                    dto.setId(user.getId());
                    dto.setNombre(user.getNombre());
                    dto.setApellidoP(user.getApellidoP());
                    dto.setApellidoM(user.getApellidoM());
                    dto.setGenero(user.getGenero());
                    dto.setEdad(user.getEdad());
                    dto.setTelefono(user.getTelefono());

                    if (user.getMunicipio() != null) {
                        dto.setId_Municipio(user.getMunicipio().getId());
                        dto.setMunicipio(user.getMunicipio().getNombre());
                    }
                    dto.setColonia(user.getColonia());
                    dto.setCorreo(user.getCorreo());

                    if (user.getFoto() != null) {
                        String base64String = java.util.Base64.getEncoder().encodeToString(user.getFoto());
                        dto.setFoto(base64String);
                    }

                    return dto;
                })
                .toList();
    }


    public List<AfiliadoDTO> buscarPorNombreYApellidosAfiliado (String nombre, String apellidoP, String apellidoM) {

        String buscarNombre = (nombre != null && !nombre.isBlank()) ? nombre : null;
        String buscarApellidoP = (apellidoP != null && !apellidoP.isBlank()) ? apellidoP : null;
        String buscarApellidoM = (apellidoM != null && !apellidoM.isBlank()) ? apellidoM : null;

        List<BeanUser> usuarios = userRepository.BusquedaNombre(
                buscarNombre,
                buscarApellidoP,
                buscarApellidoM,
                UserType.AFILIADO
        );

        return usuarios.stream()
                .map(user -> {
                    AfiliadoDTO dto = new AfiliadoDTO();

                    dto.setId(user.getId());
                    dto.setNombre(user.getNombre());
                    dto.setApellidoP(user.getApellidoP());
                    dto.setApellidoM(user.getApellidoM());
                    dto.setEdad(user.getEdad());
                    dto.setTelefono(user.getTelefono());
                    dto.setGenero(user.getGenero());
                    if (user.getFoto() != null) {
                        String base64String = java.util.Base64.getEncoder().encodeToString(user.getFoto());
                        dto.setFoto(base64String);
                    }

                    return dto;
                })
                .toList();
    }

    //convertir Bean user a dto, para filtro de fecha
    private BeneficiarioDTO convertirABeneficiarioDTO(BeanUser user) {
        BeneficiarioDTO dto = new BeneficiarioDTO();

        dto.setId(user.getId());
        dto.setNombre(user.getNombre());
        dto.setApellidoP(user.getApellidoP());
        dto.setApellidoM(user.getApellidoM());
        dto.setGenero(user.getGenero());
        dto.setEdad(user.getEdad());
        dto.setTelefono(user.getTelefono());

        if (user.getMunicipio() != null) {
            dto.setId_Municipio(user.getMunicipio().getId());
            dto.setMunicipio(user.getMunicipio().getNombre());
        }

        dto.setColonia(user.getColonia());
        dto.setCorreo(user.getCorreo());

        if (user.getFoto() != null) {
            String base64String =
                    java.util.Base64.getEncoder().encodeToString(user.getFoto());
            dto.setFoto(base64String);
        }

        return dto;
    }


    private AfiliadoDTO convertirAfiliadoDTO(BeanUser user) {
        AfiliadoDTO dto = new AfiliadoDTO();

        dto.setId(user.getId());
        dto.setNombre(user.getNombre());
        dto.setApellidoP(user.getApellidoP());
        dto.setApellidoM(user.getApellidoM());
        dto.setGenero(user.getGenero());
        dto.setEdad(user.getEdad());
        dto.setTelefono(user.getTelefono());

        if (user.getFoto() != null) {
            String base64String =
                    java.util.Base64.getEncoder().encodeToString(user.getFoto());
            dto.setFoto(base64String);
        }

        return dto;
    }

    private byte[] convertirImagen(byte[] imagenOriginal) {
        try {


            ByteArrayInputStream bais = new ByteArrayInputStream(imagenOriginal);
            BufferedImage imagen = ImageIO.read(bais);

            if (imagen == null) {
                System.err.println("ImageIO no pudo leer el formato de la imagen.");
                return null;
            }

            ByteArrayOutputStream salida = new ByteArrayOutputStream();

            boolean resultado = ImageIO.write(imagen, "jpg", salida);

            if (!resultado) {
                return null;
            }

            return salida.toByteArray();

        } catch (Exception e) {
            System.err.println("Error convirtiendo imagen: " + e.getMessage());
            return null;
        }
    }


}




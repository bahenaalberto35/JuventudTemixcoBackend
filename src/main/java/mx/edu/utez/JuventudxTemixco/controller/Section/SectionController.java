package mx.edu.utez.JuventudxTemixco.controller.Section;

import lombok.RequiredArgsConstructor;
import mx.edu.utez.JuventudxTemixco.models.Sections.BeanSection;
import mx.edu.utez.JuventudxTemixco.models.Sections.SectionRepository;
import mx.edu.utez.JuventudxTemixco.models.programs.BeanProgram;
import mx.edu.utez.JuventudxTemixco.models.programs.ProgramRepository;
import mx.edu.utez.JuventudxTemixco.service.Sections.SectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/section")
@CrossOrigin(origins = "https://juventudxtemixco.org/", allowCredentials = "true")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> save (
            @RequestParam("archivo")MultipartFile archivo,
            @RequestParam("name") String name,
            @RequestParam("description")String description
            ) throws IOException {
        return new ResponseEntity<>(service.save(archivo, name, description), HttpStatus.CREATED);
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam("description") String description,
            @RequestParam(value = "name") String name
    ) throws IOException {
        return new ResponseEntity<>(service.update(id, archivo, description, name), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> list(){
        return new ResponseEntity<>(service.list(), HttpStatus.OK);
    }

    @GetMapping("/imagen/{id}")
    public ResponseEntity<byte[]> getImagen(@PathVariable Long id) {
        byte[] imagen = service.getImagenSeccion(id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imagen);
    }

    @GetMapping("/byName")
    public ResponseEntity<?> listByName(@RequestParam String name) {
        return new ResponseEntity<>(service.findByName(name), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/{id}/programs")
    public ResponseEntity<?>getProgramasPorSeccion(@PathVariable Long id) {

        return ResponseEntity.ok(service.getProgramasPorSeccion(id));
    }

    @GetMapping("/program/image/{id}")
    public ResponseEntity<byte[]>getImagenPrograma(@PathVariable Long id) {
        byte[] imagen = service.getImagenPrograma(id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imagen);
    }

}

package mx.edu.utez.JuventudxTemixco.controller.allyController;

import jakarta.validation.Valid;
import mx.edu.utez.JuventudxTemixco.Dto.alliesDTO.AliadoDTO;
import mx.edu.utez.JuventudxTemixco.models.Allies.BeanAlly;
import mx.edu.utez.JuventudxTemixco.service.AlianzaService.AlianzaService;
import mx.edu.utez.JuventudxTemixco.service.UsersService.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/Alianza")
@CrossOrigin(origins = "https://juventudxtemixco.org/", allowCredentials = "true")
public class allyController {


    private AlianzaService alianzaService;

    public allyController(AlianzaService alianzaService) {
        this.alianzaService = alianzaService;

    }


    @GetMapping
    public List<BeanAlly> Alianzas(){
        return alianzaService.listaAlianzas();
    }

    @PostMapping
    public BeanAlly agregarAlianza(@Valid @RequestBody AliadoDTO dto){

        return alianzaService.createAlianza(dto);
    }

    @PutMapping("/{id}")
    public BeanAlly actualizarAlianza(@Valid @RequestBody AliadoDTO dto, @PathVariable Long id){
        return alianzaService.editarAlianza(dto, id);
    }

    @DeleteMapping("/{id}")
    public void eliminarAlianza(@PathVariable Long id){
        alianzaService.eliminarAlianza(id);
    }

    @GetMapping("/busquedaAlianza")
    public List<BeanAlly> busquedaAlianza(@RequestParam(name = "nombre", required = false) String nombre){
        return alianzaService.buscarPorNombre(nombre);
    }

}

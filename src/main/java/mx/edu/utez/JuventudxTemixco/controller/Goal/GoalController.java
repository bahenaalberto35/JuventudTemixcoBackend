package mx.edu.utez.JuventudxTemixco.controller.Goal;


import lombok.RequiredArgsConstructor;
import mx.edu.utez.JuventudxTemixco.Dto.Goal.UpdateDescriptionDTO;
import mx.edu.utez.JuventudxTemixco.service.Goals.GoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goal")
@CrossOrigin(origins = "https://juventudxtemixco.org/", allowCredentials = "true")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<?> list() {
        return new ResponseEntity<>(goalService.list(), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody UpdateDescriptionDTO dto
    ) {
        return new ResponseEntity<>(goalService.updateDescription(id, dto), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> findByName(@PathVariable String name) {

        return ResponseEntity.ok(goalService.findByName(name));
    }

}

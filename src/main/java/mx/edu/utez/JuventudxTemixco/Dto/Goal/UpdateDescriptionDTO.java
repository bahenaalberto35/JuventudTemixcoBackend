package mx.edu.utez.JuventudxTemixco.Dto.Goal;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDescriptionDTO {

    @NotBlank
    private String description;

}

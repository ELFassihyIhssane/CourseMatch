package ma.kaoutar.courseservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer une inscription
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequestDTO {

    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;

    @NotNull(message = "L'ID cours est obligatoire")
    private Long courseId;
}

package ma.kaoutar.courseservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer/modifier un cours
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCreateDTO {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 200, message = "Le titre doit contenir entre 3 et 200 caractères")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, message = "La description doit contenir au moins 10 caractères")
    private String description;

    @NotBlank(message = "La catégorie est obligatoire")
    private String category;

    @NotBlank(message = "Le niveau est obligatoire")
    @Pattern(regexp = "BEGINNER|INTERMEDIATE|ADVANCED",
            message = "Le niveau doit être BEGINNER, INTERMEDIATE ou ADVANCED")
    private String level;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 1, message = "La durée doit être au moins 1 heure")
    private Integer duration;

    @NotBlank(message = "L'instructeur est obligatoire")
    private String instructor;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix doit être positif")
    private Double price;

    @Min(value = 1, message = "Le nombre maximum d'étudiants doit être au moins 1")
    private Integer maxStudents;

    private String imageUrl;
}

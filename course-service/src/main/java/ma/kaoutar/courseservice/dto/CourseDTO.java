package ma.kaoutar.courseservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour la réponse Course (lecture)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {

    private Long id;

    private String title;

    private String description;

    private String category;

    private String level;

    private Integer duration;

    private String instructor;

    private Double price;

    private Integer maxStudents;

    private Integer currentEnrollments;

    private Boolean active;

    private String imageUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Champs calculés
    private Boolean isFull;
    private Integer availableSeats;
}

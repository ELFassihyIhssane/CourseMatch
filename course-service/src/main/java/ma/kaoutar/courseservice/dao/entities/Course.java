package ma.kaoutar.courseservice.dao.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, length = 50)
    private String level; // BEGINNER, INTERMEDIATE, ADVANCED

    @Column(nullable = false)
    private Integer duration; // durée en heures

    @Column(nullable = false)
    private String instructor;

    @Column(nullable = false)
    private Double price;

    @Column(name = "max_students")
    private Integer maxStudents;

    @Column(name = "current_enrollments")
    @Builder.Default
    private Integer currentEnrollments = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Méthodes utilitaires
    public boolean isFull() {
        return maxStudents != null && currentEnrollments >= maxStudents;
    }

    public void incrementEnrollment() {
        this.currentEnrollments++;
    }

    public void decrementEnrollment() {
        if (this.currentEnrollments > 0) {
            this.currentEnrollments--;
        }
    }
}
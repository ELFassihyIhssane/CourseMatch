package ma.kaoutar.courseservice.dao.repositories;

import ma.kaoutar.courseservice.dao.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Recherche par catégorie
    List<Course> findByCategoryAndActiveTrue(String category);

    // Recherche par niveau
    List<Course> findByLevelAndActiveTrue(String level);

    // Recherche par instructeur
    List<Course> findByInstructorContainingIgnoreCaseAndActiveTrue(String instructor);

    // Recherche par titre (like)
    List<Course> findByTitleContainingIgnoreCaseAndActiveTrue(String title);

    // Cours actifs uniquement
    List<Course> findByActiveTrue();

    // Cours avec places disponibles
    @Query("SELECT c FROM Course c WHERE c.active = true AND " +
            "(c.maxStudents IS NULL OR c.currentEnrollments < c.maxStudents)")
    List<Course> findAvailableCourses();

    // Statistiques par catégorie
    @Query("SELECT c.category, COUNT(c) FROM Course c WHERE c.active = true GROUP BY c.category")
    List<Object[]> countCoursesByCategory();

    // Cours populaires (plus d'inscriptions)
    @Query("SELECT c FROM Course c WHERE c.active = true ORDER BY c.currentEnrollments DESC")
    List<Course> findPopularCourses();
}

package ma.kaoutar.courseservice.dao.repositories;

import ma.kaoutar.courseservice.dao.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Trouver les inscriptions d'un utilisateur
    List<Enrollment> findByUserId(Long userId);

    // Trouver les inscriptions actives d'un utilisateur
    List<Enrollment> findByUserIdAndStatus(Long userId, String status);

    // Trouver les inscriptions d'un cours
    List<Enrollment> findByCourseId(Long courseId);

    // Vérifier si l'utilisateur est déjà inscrit
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    // Compter les inscriptions actives d'un cours
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ACTIVE'")
    Long countActiveByCourseId(Long courseId);

    // Statistiques utilisateur
    @Query("SELECT e.status, COUNT(e) FROM Enrollment e WHERE e.userId = :userId GROUP BY e.status")
    List<Object[]> getUserEnrollmentStats(Long userId);

    // Vérifier existence
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}

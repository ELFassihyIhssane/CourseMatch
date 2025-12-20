package ma.kaoutar.courseservice.controller;

import ma.kaoutar.courseservice.dao.repositories.CourseRepository;
import ma.kaoutar.courseservice.dao.repositories.EnrollmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Metrics", description = "Statistiques et métriques du service")
@CrossOrigin(origins = "*")
public class MetricsController {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * GET /api/metrics - Statistiques globales
     */
    @GetMapping
    @Operation(summary = "Récupérer les statistiques globales du service")
    public ResponseEntity<Map<String, Object>> getServiceMetrics() {
        log.info("GET /api/metrics - Récupération des statistiques");

        Map<String, Object> metrics = new HashMap<>();

        // Nombre total de cours
        long totalCourses = courseRepository.count();
        long activeCourses = courseRepository.findByActiveTrue().size();

        // Nombre total d'inscriptions
        long totalEnrollments = enrollmentRepository.count();

        // Statistiques par catégorie
        Map<String, Long> coursesByCategory = new HashMap<>();
        courseRepository.countCoursesByCategory().forEach(obj -> {
            coursesByCategory.put((String) obj[0], (Long) obj[1]);
        });

        metrics.put("total_courses", totalCourses);
        metrics.put("active_courses", activeCourses);
        metrics.put("total_enrollments", totalEnrollments);
        metrics.put("courses_by_category", coursesByCategory);
        metrics.put("service_name", "course-service");
        metrics.put("status", "UP");

        return ResponseEntity.ok(metrics);
    }

    /**
     * GET /api/metrics/health - Health check simple
     */
    @GetMapping("/health")
    @Operation(summary = "Vérifier l'état de santé du service")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "course-service");
        return ResponseEntity.ok(health);
    }
}

package ma.kaoutar.courseservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.kaoutar.courseservice.dto.CourseCreateDTO;
import ma.kaoutar.courseservice.dto.CourseDTO;
import ma.kaoutar.courseservice.dto.EnrollmentDTO;
import ma.kaoutar.courseservice.dto.EnrollmentRequestDTO;
import ma.kaoutar.courseservice.service.CourseService;
import ma.kaoutar.courseservice.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Course Management", description = "APIs pour la gestion des cours")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    /**
     * GET /api/courses - Récupérer tous les cours
     */
    @GetMapping
    @Operation(summary = "Récupérer tous les cours actifs")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        log.info("GET /api/courses - Récupération de tous les cours");
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * GET /api/courses/{id} - Récupérer un cours par ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un cours par son ID")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        log.info("GET /api/courses/{} - Récupération du cours", id);
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    /**
     * POST /api/courses - Créer un nouveau cours
     */
    @PostMapping
    @Operation(summary = "Créer un nouveau cours")
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseCreateDTO createDTO) {
        log.info("POST /api/courses - Création d'un nouveau cours: {}", createDTO.getTitle());
        CourseDTO createdCourse = courseService.createCourse(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    /**
     * PUT /api/courses/{id} - Mettre à jour un cours
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un cours existant")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseCreateDTO updateDTO) {
        log.info("PUT /api/courses/{} - Mise à jour du cours", id);
        CourseDTO updatedCourse = courseService.updateCourse(id, updateDTO);
        return ResponseEntity.ok(updatedCourse);
    }

    /**
     * DELETE /api/courses/{id} - Supprimer un cours
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un cours (soft delete)")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable Long id) {
        log.info("DELETE /api/courses/{} - Suppression du cours", id);
        courseService.deleteCourse(id);
        return ResponseEntity.ok(Map.of("message", "Cours supprimé avec succès"));
    }

    /**
     * GET /api/courses/search/category - Rechercher par catégorie
     */
    @GetMapping("/search/category")
    @Operation(summary = "Rechercher des cours par catégorie")
    public ResponseEntity<List<CourseDTO>> getCoursesByCategory(
            @RequestParam String category) {
        log.info("GET /api/courses/search/category?category={}", category);
        List<CourseDTO> courses = courseService.getCoursesByCategory(category);
        return ResponseEntity.ok(courses);
    }

    /**
     * GET /api/courses/search/level - Rechercher par niveau
     */
    @GetMapping("/search/level")
    @Operation(summary = "Rechercher des cours par niveau")
    public ResponseEntity<List<CourseDTO>> getCoursesByLevel(
            @RequestParam String level) {
        log.info("GET /api/courses/search/level?level={}", level);
        List<CourseDTO> courses = courseService.getCoursesByLevel(level);
        return ResponseEntity.ok(courses);
    }

    /**
     * GET /api/courses/search - Rechercher par titre
     */
    @GetMapping("/search")
    @Operation(summary = "Rechercher des cours par titre")
    public ResponseEntity<List<CourseDTO>> searchCoursesByTitle(
            @RequestParam String title) {
        log.info("GET /api/courses/search?title={}", title);
        List<CourseDTO> courses = courseService.searchCoursesByTitle(title);
        return ResponseEntity.ok(courses);
    }

    /**
     * GET /api/courses/available - Cours avec places disponibles
     */
    @GetMapping("/available")
    @Operation(summary = "Récupérer les cours avec places disponibles")
    public ResponseEntity<List<CourseDTO>> getAvailableCourses() {
        log.info("GET /api/courses/available - Récupération des cours disponibles");
        List<CourseDTO> courses = courseService.getAvailableCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * GET /api/courses/popular - Cours populaires
     */
    @GetMapping("/popular")
    @Operation(summary = "Récupérer les cours les plus populaires")
    public ResponseEntity<List<CourseDTO>> getPopularCourses() {
        log.info("GET /api/courses/popular - Récupération des cours populaires");
        List<CourseDTO> courses = courseService.getPopularCourses();
        return ResponseEntity.ok(courses);
    }

    // ===== ENDPOINTS POUR LES INSCRIPTIONS =====

    /**
     * POST /api/courses/{id}/enroll - Inscrire un utilisateur
     */
    @PostMapping("/{courseId}/enroll")
    @Operation(summary = "Inscrire un utilisateur à un cours")
    public ResponseEntity<EnrollmentDTO> enrollUserToCourse(
            @PathVariable Long courseId,
            @RequestBody EnrollmentRequestDTO request) {
        log.info("POST /api/courses/{}/enroll - Inscription utilisateur {}",
                courseId, request.getUserId());
        request.setCourseId(courseId);
        EnrollmentDTO enrollment = enrollmentService.enrollUserToCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    /**
     * GET /api/courses/{id}/enrollments - Récupérer les inscriptions d'un cours
     */
    @GetMapping("/{courseId}/enrollments")
    @Operation(summary = "Récupérer toutes les inscriptions d'un cours")
    public ResponseEntity<List<EnrollmentDTO>> getCourseEnrollments(
            @PathVariable Long courseId) {
        log.info("GET /api/courses/{}/enrollments - Récupération des inscriptions", courseId);
        List<EnrollmentDTO> enrollments = enrollmentService.getCourseEnrollments(courseId);
        return ResponseEntity.ok(enrollments);
    }
}

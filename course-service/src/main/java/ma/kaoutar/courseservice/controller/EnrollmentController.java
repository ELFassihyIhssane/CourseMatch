package ma.kaoutar.courseservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.kaoutar.courseservice.dto.EnrollmentDTO;
import ma.kaoutar.courseservice.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Enrollment Management", description = "APIs pour la gestion des inscriptions")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * GET /api/enrollments/user/{userId} - Inscriptions d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupérer toutes les inscriptions d'un utilisateur")
    public ResponseEntity<List<EnrollmentDTO>> getUserEnrollments(
            @PathVariable Long userId) {
        log.info("GET /api/enrollments/user/{} - Récupération des inscriptions", userId);
        List<EnrollmentDTO> enrollments = enrollmentService.getUserEnrollments(userId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * GET /api/enrollments/user/{userId}/active - Inscriptions actives
     */
    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Récupérer les inscriptions actives d'un utilisateur")
    public ResponseEntity<List<EnrollmentDTO>> getUserActiveEnrollments(
            @PathVariable Long userId) {
        log.info("GET /api/enrollments/user/{}/active - Inscriptions actives", userId);
        List<EnrollmentDTO> enrollments = enrollmentService.getUserActiveEnrollments(userId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * DELETE /api/enrollments/{id} - Annuler une inscription
     */
    @DeleteMapping("/{enrollmentId}")
    @Operation(summary = "Annuler une inscription")
    public ResponseEntity<Map<String, String>> cancelEnrollment(
            @PathVariable Long enrollmentId) {
        log.info("DELETE /api/enrollments/{} - Annulation de l'inscription", enrollmentId);
        enrollmentService.cancelEnrollment(enrollmentId);
        return ResponseEntity.ok(Map.of("message", "Inscription annulée avec succès"));
    }

    /**
     * PATCH /api/enrollments/{id}/progress - Mettre à jour la progression
     */
    @PatchMapping("/{enrollmentId}/progress")
    @Operation(summary = "Mettre à jour la progression d'une inscription")
    public ResponseEntity<EnrollmentDTO> updateProgress(
            @PathVariable Long enrollmentId,
            @RequestBody Map<String, Integer> request) {
        log.info("PATCH /api/enrollments/{}/progress - Mise à jour progression", enrollmentId);
        Integer progress = request.get("progress");
        EnrollmentDTO enrollment = enrollmentService.updateProgress(enrollmentId, progress);
        return ResponseEntity.ok(enrollment);
    }
}

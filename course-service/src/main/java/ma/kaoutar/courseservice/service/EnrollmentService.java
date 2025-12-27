package ma.kaoutar.courseservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.kaoutar.courseservice.dao.entities.Course;
import ma.kaoutar.courseservice.dao.entities.Enrollment;
import ma.kaoutar.courseservice.dao.repositories.CourseRepository;
import ma.kaoutar.courseservice.dao.repositories.EnrollmentRepository;
import ma.kaoutar.courseservice.dto.EnrollmentDTO;
import ma.kaoutar.courseservice.dto.EnrollmentRequestDTO;
import ma.kaoutar.courseservice.exception.BusinessException;
import ma.kaoutar.courseservice.exception.ResourceNotFoundException;
import ma.kaoutar.courseservice.mapper.CourseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    /**
     * Inscrire un utilisateur à un cours
     */
    public EnrollmentDTO enrollUserToCourse(EnrollmentRequestDTO request) {
        log.info("Inscription de l'utilisateur {} au cours {}",
                request.getUserId(), request.getCourseId());

        // Vérifier si le cours existe
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        // Vérifier si le cours est actif
        if (!course.getActive()) {
            throw new BusinessException("Le cours n'est pas disponible");
        }

        // Vérifier si l'utilisateur est déjà inscrit
        if (enrollmentRepository.existsByUserIdAndCourseId(request.getUserId(), request.getCourseId())) {
            throw new BusinessException("L'utilisateur est déjà inscrit à ce cours");
        }

        // Vérifier s'il reste de la place
        if (course.isFull()) {
            throw new BusinessException("Le cours est complet");
        }

        // Créer l'inscription
        Enrollment enrollment = Enrollment.builder()
                .userId(request.getUserId())
                .course(course)
                .status("ACTIVE")
                .progress(0)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Incrémenter le compteur d'inscriptions
        course.incrementEnrollment();
        courseRepository.save(course);

        log.info("Inscription réussie, ID: {}", savedEnrollment.getId());

        return courseMapper.toEnrollmentDTO(savedEnrollment);
    }

    /**
     * Annuler une inscription
     */
    public void cancelEnrollment(Long enrollmentId) {
        log.info("Annulation de l'inscription ID: {}", enrollmentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        if ("CANCELLED".equals(enrollment.getStatus())) {
            throw new BusinessException("L'inscription est déjà annulée");
        }

        enrollment.setStatus("CANCELLED");
        enrollmentRepository.save(enrollment);

        // Décrémenter le compteur
        Course course = enrollment.getCourse();
        course.decrementEnrollment();
        courseRepository.save(course);

        log.info("Inscription annulée avec succès");
    }

    /**
     * Récupérer les inscriptions d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getUserEnrollments(Long userId) {
        log.info("Récupération des inscriptions de l'utilisateur {}", userId);
        return enrollmentRepository.findByUserId(userId)
                .stream()
                .map(courseMapper::toEnrollmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les inscriptions actives d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getUserActiveEnrollments(Long userId) {
        log.info("Récupération des inscriptions actives de l'utilisateur {}", userId);
        return enrollmentRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .stream()
                .map(courseMapper::toEnrollmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les inscriptions d'un cours
     */
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getCourseEnrollments(Long courseId) {
        log.info("Récupération des inscriptions du cours {}", courseId);
        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(courseMapper::toEnrollmentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mettre à jour la progression d'un utilisateur
     */
    public EnrollmentDTO updateProgress(Long enrollmentId, Integer progress) {
        log.info("Mise à jour de la progression de l'inscription {}: {}%", enrollmentId, progress);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        if (progress < 0 || progress > 100) {
            throw new BusinessException("La progression doit être entre 0 et 100");
        }

        enrollment.setProgress(progress);

        // Si progression = 100%, marquer comme complété
        if (progress == 100) {
            enrollment.markAsCompleted();
        }

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Progression mise à jour avec succès");

        return courseMapper.toEnrollmentDTO(updatedEnrollment);
    }
}
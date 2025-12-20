package ma.kaoutar.courseservice.service;
import ma.kaoutar.courseservice.dao.entities.Course;
import ma.kaoutar.courseservice.dao.repositories.CourseRepository;
import ma.kaoutar.courseservice.dto.CourseCreateDTO;
import ma.kaoutar.courseservice.dto.CourseDTO;
import ma.kaoutar.courseservice.exception.ResourceNotFoundException;
import ma.kaoutar.courseservice.mapper.CourseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    /**
     * Récupérer tous les cours actifs
     */
    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        log.info("Récupération de tous les cours actifs");
        return courseRepository.findByActiveTrue()
                .stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer un cours par ID
     */
    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        log.info("Récupération du cours avec ID: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        return courseMapper.toDTO(course);
    }

    /**
     * Créer un nouveau cours
     */
    public CourseDTO createCourse(CourseCreateDTO createDTO) {
        log.info("Création d'un nouveau cours: {}", createDTO.getTitle());

        Course course = courseMapper.toEntity(createDTO);
        course.setActive(true);
        course.setCurrentEnrollments(0);

        Course savedCourse = courseRepository.save(course);
        log.info("Cours créé avec succès, ID: {}", savedCourse.getId());

        return courseMapper.toDTO(savedCourse);
    }

    /**
     * Mettre à jour un cours
     */
    public CourseDTO updateCourse(Long id, CourseCreateDTO updateDTO) {
        log.info("Mise à jour du cours ID: {}", id);

        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        // Mettre à jour les champs
        existingCourse.setTitle(updateDTO.getTitle());
        existingCourse.setDescription(updateDTO.getDescription());
        existingCourse.setCategory(updateDTO.getCategory());
        existingCourse.setLevel(updateDTO.getLevel());
        existingCourse.setDuration(updateDTO.getDuration());
        existingCourse.setInstructor(updateDTO.getInstructor());
        existingCourse.setPrice(updateDTO.getPrice());
        existingCourse.setMaxStudents(updateDTO.getMaxStudents());
        existingCourse.setImageUrl(updateDTO.getImageUrl());

        Course updatedCourse = courseRepository.save(existingCourse);
        log.info("Cours mis à jour avec succès");

        return courseMapper.toDTO(updatedCourse);
    }

    /**
     * Supprimer un cours (soft delete)
     */
    public void deleteCourse(Long id) {
        log.info("Suppression du cours ID: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        course.setActive(false);
        courseRepository.save(course);

        log.info("Cours désactivé avec succès");
    }

    /**
     * Rechercher des cours par catégorie
     */
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByCategory(String category) {
        log.info("Recherche des cours par catégorie: {}", category);
        return courseRepository.findByCategoryAndActiveTrue(category)
                .stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Rechercher des cours par niveau
     */
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByLevel(String level) {
        log.info("Recherche des cours par niveau: {}", level);
        return courseRepository.findByLevelAndActiveTrue(level)
                .stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Rechercher des cours par titre
     */
    @Transactional(readOnly = true)
    public List<CourseDTO> searchCoursesByTitle(String title) {
        log.info("Recherche des cours par titre: {}", title);
        return courseRepository.findByTitleContainingIgnoreCaseAndActiveTrue(title)
                .stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les cours disponibles (avec places disponibles)
     */
    @Transactional(readOnly = true)
    public List<CourseDTO> getAvailableCourses() {
        log.info("Récupération des cours avec places disponibles");
        return courseRepository.findAvailableCourses()
                .stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les cours populaires
     */
    @Transactional(readOnly = true)
    public List<CourseDTO> getPopularCourses() {
        log.info("Récupération des cours populaires");
        return courseRepository.findPopularCourses()
                .stream()
                .limit(10)
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }
}

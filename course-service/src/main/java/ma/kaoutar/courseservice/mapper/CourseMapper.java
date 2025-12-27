package ma.kaoutar.courseservice.mapper;

import ma.kaoutar.courseservice.dao.entities.Course;
import ma.kaoutar.courseservice.dao.entities.Enrollment;
import ma.kaoutar.courseservice.dto.CourseCreateDTO;
import ma.kaoutar.courseservice.dto.CourseDTO;
import ma.kaoutar.courseservice.dto.EnrollmentDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {

    /**
     * Convertir Course Entity → CourseDTO
     */
    @Mapping(target = "isFull", expression = "java(course.isFull())")
    @Mapping(target = "availableSeats",
            expression = "java(calculateAvailableSeats(course))")
    CourseDTO toDTO(Course course);

    /**
     * Convertir CourseCreateDTO → Course Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "currentEnrollments", constant = "0")
    @Mapping(target = "active", constant = "true")
    Course toEntity(CourseCreateDTO dto);

    /**
     * Convertir Enrollment Entity → EnrollmentDTO
     */
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.title")
    EnrollmentDTO toEnrollmentDTO(Enrollment enrollment);

    /**
     * Calculer les places disponibles
     */
    default Integer calculateAvailableSeats(Course course) {
        if (course.getMaxStudents() == null) {
            return null; // Pas de limite
        }
        int available = course.getMaxStudents() - course.getCurrentEnrollments();
        return Math.max(0, available);
    }
}

package com.artur.java.ulearner.repository;

import com.artur.java.ulearner.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByPublished(Boolean published);
    List<Course> findByInstructorId(Long instructorId);

    @Query("SELECT c FROM Course c WHERE c.published = true AND " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Course> searchPublishedCourses(String keyword);
}

package com.artur.java.ulearner.repository;

import com.artur.java.ulearner.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findByUserIdAndLessonId(Long userId, Long lessonId);
    List<Progress> findByUserId(Long userId);

    @Query("SELECT p FROM Progress p WHERE p.user.id = :userId AND p.lesson.course.id = :courseId")
    List<Progress> findByUserIdAndCourseId(Long userId, Long courseId);

    @Query("SELECT COUNT(p) * 100.0 / (SELECT COUNT(l) FROM Lesson l WHERE l.course.id = :courseId) " +
            "FROM Progress p WHERE p.user.id = :userId AND p.lesson.course.id = :courseId AND p.completed = true")
    Double getCourseCompletionPercentage(Long userId, Long courseId);
}

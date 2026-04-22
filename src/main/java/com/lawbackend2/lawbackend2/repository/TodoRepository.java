package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findByUserId(Long userId, Pageable pageable);

    Page<Todo> findByUserIdAndCreateTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    Page<Todo> findByUserIdAndDeadlineBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    Page<Todo> findByUserIdAndDeadlineBefore(Long userId, LocalDateTime endTime, Pageable pageable);

    Page<Todo> findByUserIdAndDeadlineAfter(Long userId, LocalDateTime startTime, Pageable pageable);

    Page<Todo> findByUserIdAndStatus(Long userId, String status, Pageable pageable);

    Page<Todo> findByUserIdAndType(Long userId, String type, Pageable pageable);

    Page<Todo> findByUserIdAndPriority(Long userId, String priority, Pageable pageable);

    List<Todo> findByUserIdAndStatusOrderByDeadlineAsc(Long userId, String status);

    List<Todo> findByUserIdAndDeadlineBefore(Long userId, LocalDateTime deadline);

    List<Todo> findByUserIdAndStatusOrderByCompletedTimeDesc(Long userId, String status);

    @Query("SELECT t FROM Todo t WHERE " +
           "t.userId = :userId AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority)")
    Page<Todo> searchTodos(@Param("userId") Long userId,
                           @Param("type") String type,
                           @Param("status") String status,
                           @Param("priority") String priority,
                           Pageable pageable);

    Long countByUserIdAndStatus(Long userId, String status);

    Long countByUserIdAndDeadlineBefore(Long userId, LocalDateTime deadline);

    Long countPendingByUserId(Long userId);

    Long countCompletedByUserId(Long userId);

    @Query("SELECT t.status, COUNT(t) FROM Todo t WHERE t.userId = :userId GROUP BY t.status")
    List<Object[]> countByUserIdAndStatusGroup(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.userId = :userId AND t.deadline IS NOT NULL AND t.deadline < CURRENT_TIMESTAMP AND t.status = 'PENDING'")
    Long countOverdueByUserId(@Param("userId") Long userId);
}

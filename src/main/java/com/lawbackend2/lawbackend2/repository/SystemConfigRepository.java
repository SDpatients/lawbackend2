package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    Optional<SystemConfig> findByConfigKey(String configKey);

    List<SystemConfig> findByConfigGroup(String configGroup);

    @Query("SELECT sc FROM SystemConfig sc WHERE sc.status = :status AND sc.isDeleted = false ORDER BY sc.sortOrder ASC")
    List<SystemConfig> findByStatus(@Param("status") String status);

    @Query("SELECT sc FROM SystemConfig sc WHERE sc.status = :status AND sc.isDeleted = false ORDER BY sc.sortOrder ASC")
    List<SystemConfig> findAllActive(@Param("status") String status);

    @Query("SELECT sc FROM SystemConfig sc WHERE sc.configGroup = :configGroup AND sc.status = :status AND sc.isDeleted = false ORDER BY sc.sortOrder ASC")
    List<SystemConfig> findByConfigGroupAndStatus(@Param("configGroup") String configGroup, @Param("status") String status);

    boolean existsByConfigKey(String configKey);
}

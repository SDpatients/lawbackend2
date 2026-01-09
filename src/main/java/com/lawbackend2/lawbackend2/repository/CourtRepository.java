package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Court;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {

    Page<Court> findByCourtLevel(String courtLevel, Pageable pageable);

    Page<Court> findByShortNameContaining(String shortName, Pageable pageable);

    Page<Court> findByFullNameContaining(String fullName, Pageable pageable);

    java.util.Optional<Court> findByShortName(String shortName);

    @Query("SELECT c FROM Court c WHERE " +
           "(:courtLevel IS NULL OR c.courtLevel = :courtLevel) AND " +
           "(:shortName IS NULL OR c.shortName LIKE %:shortName%) AND " +
           "(:fullName IS NULL OR c.fullName LIKE %:fullName%)")
    Page<Court> findByCourtLevelAndShortNameContainingAndFullNameContaining(@Param("courtLevel") String courtLevel,
                                                                            @Param("shortName") String shortName,
                                                                            @Param("fullName") String fullName,
                                                                            Pageable pageable);
}

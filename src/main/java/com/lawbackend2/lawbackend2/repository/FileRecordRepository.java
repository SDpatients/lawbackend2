package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.FileRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {

    @Query("SELECT fr FROM FileRecord fr WHERE fr.isDeleted = false " +
           "AND (:bizType IS NULL OR fr.bizType = :bizType) " +
           "AND (:bizId IS NULL OR fr.bizId = :bizId) " +
           "AND (:status IS NULL OR fr.status = :status)")
    Page<FileRecord> findByConditions(@Param("bizType") String bizType,
                                     @Param("bizId") String bizId,
                                     @Param("status") String status,
                                     Pageable pageable);

    @Query("SELECT fr FROM FileRecord fr WHERE fr.isDeleted = false " +
           "AND fr.bizType = :bizType " +
           "AND fr.bizId IN :bizIds " +
           "AND (:status IS NULL OR fr.status = :status) " +
           "ORDER BY fr.bizId, fr.sortOrder")
    List<FileRecord> findByBizTypeAndBizIds(@Param("bizType") String bizType,
                                            @Param("bizIds") List<String> bizIds,
                                            @Param("status") String status);
}

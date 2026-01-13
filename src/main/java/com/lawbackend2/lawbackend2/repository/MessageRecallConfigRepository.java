package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.MessageRecallConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRecallConfigRepository extends JpaRepository<MessageRecallConfig, Long> {

    Optional<MessageRecallConfig> findByConfigTypeAndTargetIdAndStatus(String configType, Long targetId, String status);

    Optional<MessageRecallConfig> findByConfigTypeAndStatus(String configType, String status);

    List<MessageRecallConfig> findByConfigTypeAndStatusOrderByCreateTimeDesc(String configType, String status);

    @Query("SELECT mrc FROM MessageRecallConfig mrc WHERE " +
           "((mrc.configType = 'USER' AND mrc.targetId = :userId) OR " +
           "(mrc.configType = 'GLOBAL' AND mrc.targetId IS NULL)) AND " +
           "mrc.status = 'ACTIVE' ORDER BY " +
           "CASE WHEN mrc.configType = 'USER' THEN 0 ELSE 1 END, " +
           "mrc.createTime DESC")
    Optional<MessageRecallConfig> findEffectiveConfigForUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE MessageRecallConfig mrc SET mrc.status = :status WHERE mrc.id = :configId")
    int updateConfigStatus(@Param("configId") Long configId, @Param("status") String status);
}

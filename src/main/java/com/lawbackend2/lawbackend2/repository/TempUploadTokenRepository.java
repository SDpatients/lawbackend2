package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.TempUploadToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TempUploadTokenRepository extends JpaRepository<TempUploadToken, Long> {

    Optional<TempUploadToken> findByToken(String token);

    Optional<TempUploadToken> findByTokenAndStatus(String token, String status);

    List<TempUploadToken> findByUserIdAndStatus(Long userId, String status);

    List<TempUploadToken> findByBizTypeAndUserIdAndStatus(String bizType, Long userId, String status);

    @Query("SELECT t FROM TempUploadToken t WHERE t.status = 'ACTIVE' AND t.expireTime < :now")
    List<TempUploadToken> findExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE TempUploadToken t SET t.status = 'EXPIRED', t.updateTime = :now WHERE t.status = 'ACTIVE' AND t.expireTime < :now")
    int markExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE TempUploadToken t SET t.fileCount = t.fileCount + 1, t.updateTime = :now WHERE t.id = :tokenId")
    int incrementFileCount(@Param("tokenId") Long tokenId, @Param("now") LocalDateTime now);

    @Query("SELECT t FROM TempUploadToken t WHERE t.token = :token AND t.status = 'ACTIVE' AND t.expireTime > :now")
    Optional<TempUploadToken> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);
}

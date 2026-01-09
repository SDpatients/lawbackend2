package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByTokenValue(String tokenValue);

    @Query("SELECT t FROM Token t WHERE t.userId = :userId AND t.status = 'ACTIVE' AND t.expireTime > :now")
    List<Token> findActiveTokensByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT t FROM Token t WHERE t.userId = :userId AND t.status = 'ACTIVE' AND t.expireTime > :now")
    Optional<Token> findActiveTokenByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE Token t SET t.status = 'INACTIVE', t.revokeTime = :revokeTime WHERE t.userId = :userId AND t.status = 'ACTIVE'")
    int revokeAllTokensByUserId(@Param("userId") Long userId, @Param("revokeTime") LocalDateTime revokeTime);

    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.expireTime < :expireTime")
    int deleteExpiredTokens(@Param("expireTime") LocalDateTime expireTime);

    @Query("SELECT t FROM Token t WHERE t.expireTime < :now")
    List<Token> findExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Token t WHERE t.expireTime < :now AND t.isDeleted = false")
    List<Token> findByExpireTimeBeforeAndIsDeletedFalse(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Token t WHERE t.status = :status AND t.isDeleted = false")
    List<Token> findByStatusAndIsDeletedFalse(@Param("status") String status);
}

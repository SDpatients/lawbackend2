package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.LoginFail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LoginFailRepository extends JpaRepository<LoginFail, Long> {

    Optional<LoginFail> findByLoginAccountAndFailIp(String loginAccount, String failIp);

    @Query("SELECT lf FROM LoginFail lf WHERE lf.loginAccount = :loginAccount AND lf.failIp = :failIp AND lf.unlockTime IS NULL OR lf.unlockTime > :now")
    Optional<LoginFail> findActiveFailRecord(@Param("loginAccount") String loginAccount, @Param("failIp") String failIp, @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE LoginFail lf SET lf.failCount = lf.failCount + 1, lf.lastFailTime = :lastFailTime WHERE lf.id = :id")
    int incrementFailCount(@Param("id") Long id, @Param("lastFailTime") LocalDateTime lastFailTime);

    @Modifying
    @Transactional
    @Query("UPDATE LoginFail lf SET lf.failCount = 0, lf.unlockTime = :unlockTime WHERE lf.id = :id")
    int resetFailCount(@Param("id") Long id, @Param("unlockTime") LocalDateTime unlockTime);

    @Query("SELECT CASE WHEN COUNT(lf) > 0 THEN true ELSE false END FROM LoginFail lf WHERE lf.loginAccount = :loginAccount AND lf.failIp = :failIp AND lf.failCount >= :maxFailCount AND (lf.unlockTime IS NULL OR lf.unlockTime > :now)")
    boolean isAccountLocked(@Param("loginAccount") String loginAccount, @Param("failIp") String failIp, @Param("maxFailCount") int maxFailCount, @Param("now") LocalDateTime now);
}

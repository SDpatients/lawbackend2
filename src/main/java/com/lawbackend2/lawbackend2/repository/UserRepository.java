package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByMobile(String mobile);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByMobile(String mobile);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.status = :status AND u.isDeleted = false")
    List<User> findByStatus(@Param("status") String status);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.realName LIKE %:keyword% OR u.mobile LIKE %:keyword%")
    Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false ORDER BY u.createTime DESC")
    List<User> findAllActive();

    @Query("SELECT u.realName FROM User u WHERE u.id = :id")
    Optional<String> findRealNameById(@Param("id") Long id);
}

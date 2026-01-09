package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.isDeleted = false " +
           "AND (:accountType IS NULL OR ba.accountType = :accountType) " +
           "AND (:status IS NULL OR ba.status = :status) " +
           "AND (:accountName IS NULL OR ba.accountName LIKE %:accountName%)")
    Page<BankAccount> findByConditions(@Param("accountType") String accountType,
                                        @Param("status") String status,
                                        @Param("accountName") String accountName,
                                        Pageable pageable);

    boolean existsByAccountNumber(String accountNumber);
}

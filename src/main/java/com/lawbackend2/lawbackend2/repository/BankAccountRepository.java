package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.dto.response.BankAccountResponse;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long>, JpaSpecificationExecutor<BankAccount> {

    @Query("SELECT ba FROM BankAccount ba WHERE ba.accountNumber = :accountNumber")
    Optional<BankAccount> findByAccountNumberIncludeDeleted(@Param("accountNumber") String accountNumber);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    @Query("SELECT ba FROM BankAccount ba WHERE " +
           "(:accountType IS NULL OR ba.accountType = :accountType) " +
           "AND (:status IS NULL OR ba.status = :status) " +
           "AND (:accountName IS NULL OR ba.accountName LIKE %:accountName%) " +
           "AND (:caseId IS NULL OR ba.caseId = :caseId)")
    Page<BankAccount> findByConditions(@Param("accountType") String accountType,
                                        @Param("status") String status,
                                        @Param("accountName") String accountName,
                                        @Param("caseId") Long caseId,
                                        Pageable pageable);

    boolean existsByAccountNumber(String accountNumber);

    @Modifying
    @Query("DELETE FROM BankAccount ba WHERE ba.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT ba FROM BankAccount ba " +
           "LEFT JOIN BankruptCase bc ON ba.caseId = bc.id " +
           "WHERE (:accountType IS NULL OR ba.accountType = :accountType) " +
           "AND (:status IS NULL OR ba.status = :status) " +
           "AND (:accountName IS NULL OR ba.accountName LIKE %:accountName%) " +
           "AND (:caseId IS NULL OR ba.caseId = :caseId) " +
           "AND (:isAdmin = true OR ba.createUserId = :userId OR (ba.caseId IS NOT NULL AND ba.caseId IN :accessibleCaseIds))")
    Page<BankAccount> findBankAccountsWithCaseInfo(@Param("accountType") String accountType,
                                                            @Param("status") String status,
                                                            @Param("accountName") String accountName,
                                                            @Param("caseId") Long caseId,
                                                            @Param("accessibleCaseIds") java.util.List<Long> accessibleCaseIds,
                                                            @Param("isAdmin") Boolean isAdmin,
                                                            @Param("userId") Long userId,
                                                            Pageable pageable);
}

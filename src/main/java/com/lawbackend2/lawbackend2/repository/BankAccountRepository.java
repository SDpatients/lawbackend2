package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.dto.response.BankAccountResponse;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long>, JpaSpecificationExecutor<BankAccount> {

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    @Query("SELECT ba FROM BankAccount ba WHERE ba.isDeleted = false " +
           "AND (:accountType IS NULL OR ba.accountType = :accountType) " +
           "AND (:status IS NULL OR ba.status = :status) " +
           "AND (:accountName IS NULL OR ba.accountName LIKE %:accountName%) " +
           "AND (:caseId IS NULL OR ba.caseId = :caseId)")
    Page<BankAccount> findByConditions(@Param("accountType") String accountType,
                                        @Param("status") String status,
                                        @Param("accountName") String accountName,
                                        @Param("caseId") Long caseId,
                                        Pageable pageable);

    boolean existsByAccountNumber(String accountNumber);
    void deleteByCaseId(Long caseId);

    @Query("SELECT new com.lawbackend2.lawbackend2.dto.response.BankAccountResponse(" +
           "ba.id, ba.status, ba.isDeleted, ba.createTime, ba.updateTime, " +
           "ba.createUserId, ba.updateUserId, ba.accountName, ba.bankName, " +
           "ba.accountNumber, ba.accountType, ba.currency, ba.currentBalance, " +
           "ba.openingDate, ba.closingDate, ba.caseId, bc.caseNumber, bc.caseName) " +
           "FROM BankAccount ba " +
           "LEFT JOIN BankruptCase bc ON ba.caseId = bc.id " +
           "WHERE ba.isDeleted = false " +
           "AND (:accountType IS NULL OR ba.accountType = :accountType) " +
           "AND (:status IS NULL OR ba.status = :status) " +
           "AND (:accountName IS NULL OR ba.accountName LIKE %:accountName%) " +
           "AND (:caseId IS NULL OR ba.caseId = :caseId) " +
           "AND (:userId IS NULL OR ba.createUserId = :userId OR :isAdmin = true)")
    Page<BankAccountResponse> findBankAccountsWithCaseInfo(@Param("accountType") String accountType,
                                                            @Param("status") String status,
                                                            @Param("accountName") String accountName,
                                                            @Param("caseId") Long caseId,
                                                            @Param("userId") Long userId,
                                                            @Param("isAdmin") Boolean isAdmin,
                                                            Pageable pageable);
}

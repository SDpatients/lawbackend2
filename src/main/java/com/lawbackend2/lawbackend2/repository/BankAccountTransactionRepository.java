package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.dto.response.BankAccountTransactionResponse;
import com.lawbackend2.lawbackend2.entity.BankAccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BankAccountTransactionRepository extends JpaRepository<BankAccountTransaction, Long>, JpaSpecificationExecutor<BankAccountTransaction> {

    @Query("SELECT bat FROM BankAccountTransaction bat WHERE bat.isDeleted = false " +
           "AND (:accountId IS NULL OR bat.accountId = :accountId) " +
           "AND (:transactionType IS NULL OR bat.transactionType = :transactionType) " +
           "AND (:businessType IS NULL OR bat.businessType = :businessType) " +
           "AND (:startDate IS NULL OR bat.transactionDate >= :startDate) " +
           "AND (:endDate IS NULL OR bat.transactionDate <= :endDate) " +
           "AND (:caseId IS NULL OR bat.caseId = :caseId)")
    Page<BankAccountTransaction> findByConditions(@Param("accountId") Long accountId,
                                                    @Param("transactionType") String transactionType,
                                                    @Param("businessType") String businessType,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate,
                                                    @Param("caseId") Long caseId,
                                                    Pageable pageable);

    @Query("SELECT new com.lawbackend2.lawbackend2.dto.response.BankAccountTransactionResponse(" +
           "bat.id, bat.status, bat.isDeleted, bat.createTime, bat.updateTime, " +
           "bat.createUserId, bat.updateUserId, bat.accountId, ba.accountName, ba.accountNumber, ba.bankName, " +
           "bat.transactionType, bat.amount, bat.transactionDate, bat.summary, bat.businessType, " +
           "bat.counterpartyAccount, bat.counterpartyName, bat.balanceAfter, bat.attachmentId, " +
           "bat.relatedBusinessId, bat.remark, bat.caseId, bc.caseNumber, bc.caseName) " +
           "FROM BankAccountTransaction bat " +
           "LEFT JOIN BankAccount ba ON bat.accountId = ba.id " +
           "LEFT JOIN BankruptCase bc ON bat.caseId = bc.id " +
           "WHERE bat.isDeleted = false " +
           "AND (:accountId IS NULL OR bat.accountId = :accountId) " +
           "AND (:transactionType IS NULL OR bat.transactionType = :transactionType) " +
           "AND (:businessType IS NULL OR bat.businessType = :businessType) " +
           "AND (:startDate IS NULL OR bat.transactionDate >= :startDate) " +
           "AND (:endDate IS NULL OR bat.transactionDate <= :endDate) " +
           "AND (:caseId IS NULL OR bat.caseId = :caseId) " +
           "ORDER BY bat.transactionDate DESC, bat.createTime DESC")
    Page<BankAccountTransactionResponse> findTransactionsWithDetails(@Param("accountId") Long accountId,
                                                                        @Param("transactionType") String transactionType,
                                                                        @Param("businessType") String businessType,
                                                                        @Param("startDate") LocalDate startDate,
                                                                        @Param("endDate") LocalDate endDate,
                                                                        @Param("caseId") Long caseId,
                                                                        Pageable pageable);

    List<BankAccountTransaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);

    void deleteByAccountId(Long accountId);

    void deleteByCaseId(Long caseId);
}

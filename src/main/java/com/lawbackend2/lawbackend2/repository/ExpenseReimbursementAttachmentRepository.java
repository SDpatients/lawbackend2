package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ExpenseReimbursementAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseReimbursementAttachmentRepository extends JpaRepository<ExpenseReimbursementAttachment, Long> {

    List<ExpenseReimbursementAttachment> findByReimbursementId(Long reimbursementId);

    void deleteByReimbursementId(Long reimbursementId);
}

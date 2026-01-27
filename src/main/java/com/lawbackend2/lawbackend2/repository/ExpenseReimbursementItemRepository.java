package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ExpenseReimbursementItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseReimbursementItemRepository extends JpaRepository<ExpenseReimbursementItem, Long> {

    List<ExpenseReimbursementItem> findByReimbursementId(Long reimbursementId);

    void deleteByReimbursementId(Long reimbursementId);
}

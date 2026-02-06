package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.SystemField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemFieldRepository extends JpaRepository<SystemField, Long>, JpaSpecificationExecutor<SystemField> {
    List<SystemField> findByGroupNameOrderBySortOrderAsc(String groupName);
    List<SystemField> findAllByOrderByGroupNameAscSortOrderAsc();
    void deleteByGroupName(String groupName);
}

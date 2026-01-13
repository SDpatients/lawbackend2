package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.EscrowManagementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.EscrowManagementReleaseRequest;
import com.lawbackend2.lawbackend2.entity.EscrowManagement;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.EscrowManagementRepository;
import com.lawbackend2.lawbackend2.service.EscrowManagementService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class EscrowManagementServiceImpl implements EscrowManagementService {

    private final EscrowManagementRepository escrowManagementRepository;

    public EscrowManagementServiceImpl(EscrowManagementRepository escrowManagementRepository) {
        this.escrowManagementRepository = escrowManagementRepository;
    }

    @Override
    public Long createEscrowManagement(EscrowManagementCreateRequest request) {
        EscrowManagement escrow = new EscrowManagement();
        BeanUtils.copyProperties(request, escrow);
        escrow.setEscrowNo(generateEscrowNo());
        escrow.setReleaseStatus("UNRELEASED");
        escrow.setStatus("ACTIVE");

        if (escrow.getReleasedAmount() == null) {
            escrow.setReleasedAmount(BigDecimal.ZERO);
        }

        EscrowManagement saved = escrowManagementRepository.save(escrow);
        return saved.getId();
    }

    @Override
    public PageResult<EscrowManagement> getEscrowManagementList(Integer pageNum, Integer pageSize, Long caseId, String escrowType, String releaseStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<EscrowManagement> page = escrowManagementRepository.findByConditions(caseId, escrowType, releaseStatus, pageable);

        PageResult<EscrowManagement> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public EscrowManagement getEscrowManagementDetail(Long escrowId) {
        return escrowManagementRepository.findById(escrowId)
                .orElseThrow(() -> new BusinessException("提存记录不存在"));
    }

    @Override
    public void releaseEscrow(Long escrowId, EscrowManagementReleaseRequest request) {
        EscrowManagement escrow = getEscrowManagementDetail(escrowId);

        if ("UNRELEASED".equals(escrow.getReleaseStatus())) {
            throw new BusinessException("提存已释放，无法再次操作");
        }

        escrow.setReleaseStatus(request.getReleaseStatus());
        escrow.setReleasedAmount(request.getReleasedAmount());
        escrow.setReleaseAccountId(request.getReleaseAccountId());
        escrow.setReleaseVoucher(request.getReleaseVoucher());
        escrow.setReleaseDate(request.getReleaseDate() != null ? request.getReleaseDate() : LocalDateTime.now());
        escrow.setIsConditionMet(request.getIsConditionMet());
        escrow.setConditionMetDate(request.getConditionMetDate());

        if (escrow.getEscrowAmount() != null) {
            BigDecimal unreleasedAmount = escrow.getEscrowAmount().subtract(request.getReleasedAmount());
            escrow.setUnreleasedAmount(unreleasedAmount.compareTo(BigDecimal.ZERO) > 0 ? unreleasedAmount : BigDecimal.ZERO);
        }

        escrowManagementRepository.save(escrow);
    }

    @Override
    public void deleteEscrowManagement(Long escrowId) {
        EscrowManagement escrow = getEscrowManagementDetail(escrowId);
        escrowManagementRepository.delete(escrow);
    }

    private String generateEscrowNo() {
        return "ESC" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
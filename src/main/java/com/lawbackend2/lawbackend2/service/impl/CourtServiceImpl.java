package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.CourtCreateRequest;
import com.lawbackend2.lawbackend2.dto.CourtUpdateRequest;
import com.lawbackend2.lawbackend2.entity.Court;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CourtRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.CourtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;
    private final UserRepository userRepository;

    public CourtServiceImpl(CourtRepository courtRepository, UserRepository userRepository) {
        this.courtRepository = courtRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Court createCourt(CourtCreateRequest request, Long userId) {
        log.info("创建法院信息, 简称: {}, 创建人ID: {}", request.getShortName(), userId);

        if (courtRepository.findByShortName(request.getShortName()).isPresent()) {
            throw new BusinessException("法院简称已存在");
        }

        if (request.getResponsibleUserId() != null) {
            User user = userRepository.findById(request.getResponsibleUserId())
                    .orElseThrow(() -> new BusinessException("用户不存在"));
        }

        Court court = new Court();
        BeanUtils.copyProperties(request, court);
        court.setCreateUserId(userId);
        court.setUpdateUserId(userId);

        Court saved = courtRepository.save(court);
        log.info("法院信息创建成功, ID: {}", saved.getId());
        return saved;
    }

    @Override
    public Court getCourtById(Long courtId) {
        log.debug("查询法院信息, ID: {}", courtId);
        return courtRepository.findById(courtId)
                .orElseThrow(() -> new BusinessException("法院信息不存在"));
    }

    @Override
    public List<Court> getCourtList(Integer pageNum, Integer pageSize, String courtLevel, String shortName, String fullName) {
        log.debug("查询法院列表, pageNum: {}, pageSize: {}, courtLevel: {}, shortName: {}, fullName: {}", 
                  pageNum, pageSize, courtLevel, shortName, fullName);

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<Court> page;
        if (courtLevel != null && shortName != null && !shortName.isEmpty() && fullName != null && !fullName.isEmpty()) {
            page = courtRepository.findByCourtLevelAndShortNameContainingAndFullNameContaining(courtLevel, shortName, fullName, pageable);
        } else if (courtLevel != null && shortName != null && !shortName.isEmpty()) {
            page = courtRepository.findByCourtLevelAndShortNameContainingAndFullNameContaining(courtLevel, shortName, null, pageable);
        } else if (courtLevel != null && fullName != null && !fullName.isEmpty()) {
            page = courtRepository.findByCourtLevelAndShortNameContainingAndFullNameContaining(courtLevel, null, fullName, pageable);
        } else if (shortName != null && !shortName.isEmpty() && fullName != null && !fullName.isEmpty()) {
            page = courtRepository.findByCourtLevelAndShortNameContainingAndFullNameContaining(null, shortName, fullName, pageable);
        } else if (courtLevel != null) {
            page = courtRepository.findByCourtLevel(courtLevel, pageable);
        } else if (shortName != null && !shortName.isEmpty()) {
            page = courtRepository.findByShortNameContaining(shortName, pageable);
        } else if (fullName != null && !fullName.isEmpty()) {
            page = courtRepository.findByFullNameContaining(fullName, pageable);
        } else {
            page = courtRepository.findAll(pageable);
        }

        return page.getContent();
    }

    @Override
    public Long getCourtCount(String courtLevel, String shortName, String fullName) {
        Pageable pageable = Pageable.unpaged();

        if (courtLevel != null && shortName != null && !shortName.isEmpty() && fullName != null && !fullName.isEmpty()) {
            return courtRepository.findByCourtLevelAndShortNameContainingAndFullNameContaining(courtLevel, shortName, fullName, pageable).getTotalElements();
        } else if (courtLevel != null && shortName != null && !shortName.isEmpty()) {
            return courtRepository.findByCourtLevelAndShortNameContainingAndFullNameContaining(courtLevel, shortName, null, pageable).getTotalElements();
        } else if (courtLevel != null && fullName != null && !fullName.isEmpty()) {
            return courtRepository.findByCourtLevelAndShortNameContainingAndFullNameContaining(courtLevel, null, fullName, pageable).getTotalElements();
        } else if (shortName != null && !shortName.isEmpty() && fullName != null && !fullName.isEmpty()) {
            return courtRepository.findByCourtLevelAndShortNameContainingAndFullNameContaining(null, shortName, fullName, pageable).getTotalElements();
        } else if (courtLevel != null) {
            return courtRepository.findByCourtLevel(courtLevel, pageable).getTotalElements();
        } else if (shortName != null && !shortName.isEmpty()) {
            return courtRepository.findByShortNameContaining(shortName, pageable).getTotalElements();
        } else if (fullName != null && !fullName.isEmpty()) {
            return courtRepository.findByFullNameContaining(fullName, pageable).getTotalElements();
        } else {
            return courtRepository.count();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Court updateCourt(Long courtId, CourtUpdateRequest request) {
        log.info("更新法院信息, ID: {}", courtId);

        Court court = getCourtById(courtId);

        if (request.getFullName() != null) {
            court.setFullName(request.getFullName());
        }
        if (request.getShortName() != null) {
            court.setShortName(request.getShortName());
        }
        if (request.getContactPhone() != null) {
            court.setContactPhone(request.getContactPhone());
        }
        if (request.getUndertakingJudge() != null) {
            court.setUndertakingJudge(request.getUndertakingJudge());
        }
        if (request.getAddress() != null) {
            court.setAddress(request.getAddress());
        }

        Court updated = courtRepository.save(court);
        log.info("法院信息更新成功, ID: {}", updated.getId());
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourt(Long courtId) {
        log.info("删除法院信息, ID: {}", courtId);
        if (!courtRepository.existsById(courtId)) {
            throw new BusinessException("法院信息不存在");
        }
        courtRepository.deleteById(courtId);
        log.info("法院信息删除成功, ID: {}", courtId);
    }
}

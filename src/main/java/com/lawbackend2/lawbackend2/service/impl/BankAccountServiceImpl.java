package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.BankAccountCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountPasswordRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankAccountRepository;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.service.BankAccountService;
import com.lawbackend2.lawbackend2.common.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final com.lawbackend2.lawbackend2.util.PasswordUtil passwordUtil;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, com.lawbackend2.lawbackend2.util.PasswordUtil passwordUtil, UserRoleRepository userRoleRepository, RoleRepository roleRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.passwordUtil = passwordUtil;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Long createBankAccount(BankAccountCreateRequest request, Long userId) {
        if (bankAccountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new BusinessException("银行账号已存在");
        }

        BankAccount bankAccount = new BankAccount();
        BeanUtils.copyProperties(request, bankAccount);
        bankAccount.setPassword(passwordUtil.encode(request.getPassword()));
        bankAccount.setStatus("ACTIVE");
        bankAccount.setCreateUserId(userId);
        bankAccount.setUpdateUserId(userId);

        BankAccount saved = bankAccountRepository.save(bankAccount);
        return saved.getId();
    }

    @Override
    public PageResult<BankAccount> getBankAccountList(Integer pageNum, Integer pageSize, String accountType, String status, String accountName, Long caseId, Long userId) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        Specification<BankAccount> spec = buildSpecificationWithPermission(accountType, status, accountName, caseId, userId);
        Page<BankAccount> page = bankAccountRepository.findAll(spec, pageable);

        PageResult<BankAccount> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public BankAccount getBankAccountDetail(Long accountId, Long userId) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("银行账户不存在"));
        checkPermission(bankAccount, userId);
        return bankAccount;
    }

    @Override
    public void updateBankAccount(Long accountId, BankAccountUpdateRequest request, Long userId) {
        BankAccount bankAccount = getBankAccountDetail(accountId, userId);
        bankAccount.setAccountName(request.getAccountName());
        bankAccount.setCurrentBalance(request.getCurrentBalance());
        if (request.getCaseId() != null) {
            bankAccount.setCaseId(request.getCaseId());
        }
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void updateBankAccountPassword(Long accountId, BankAccountPasswordRequest request, Long userId) {
        BankAccount bankAccount = getBankAccountDetail(accountId, userId);

        if (!passwordUtil.matches(request.getOldPassword(), bankAccount.getPassword())) {
            throw new BusinessException("原密码不正确");
        }

        bankAccount.setPassword(passwordUtil.encode(request.getNewPassword()));
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void updateBankAccountStatus(Long accountId, BankAccountStatusRequest request, Long userId) {
        BankAccount bankAccount = getBankAccountDetail(accountId, userId);
        bankAccount.setStatus(request.getStatus());
        bankAccountRepository.save(bankAccount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBankAccount(Long accountId) {
        if (!bankAccountRepository.existsById(accountId)) {
            throw new BusinessException("银行账户不存在");
        }
        bankAccountRepository.deleteById(accountId);
    }

    private Specification<BankAccount> buildSpecificationWithPermission(String accountType, String status, String accountName, Long caseId, Long userId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (accountType != null && !accountType.isEmpty()) {
                predicates.add(cb.equal(root.get("accountType"), accountType));
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (accountName != null && !accountName.isEmpty()) {
                predicates.add(cb.like(root.get("accountName"), "%" + accountName + "%"));
            }

            if (caseId != null) {
                predicates.add(cb.equal(root.get("caseId"), caseId));
            }

            if (!isAdminOrSuperAdmin(userId)) {
                predicates.add(cb.equal(root.get("createUserId"), userId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void checkPermission(BankAccount bankAccount, Long userId) {
        if (isAdminOrSuperAdmin(userId)) {
            return;
        }
        
        if (bankAccount.getCreateUserId().equals(userId)) {
            return;
        }
        
        throw new BusinessException("无权访问该银行账户");
    }

    private boolean isAdminOrSuperAdmin(Long userId) {
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId).orElse(null);
            if (role != null && ("ADMIN".equals(role.getRoleCode()) || "SUPER_ADMIN".equals(role.getRoleCode()))) {
                return true;
            }
        }
        return false;
    }
}

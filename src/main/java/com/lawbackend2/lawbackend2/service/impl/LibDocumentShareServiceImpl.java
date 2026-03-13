package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.LibShareCreateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentResponse;
import com.lawbackend2.lawbackend2.dto.response.LibShareResponse;
import com.lawbackend2.lawbackend2.entity.LibDocument;
import com.lawbackend2.lawbackend2.entity.LibDocumentShare;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.LibDocumentRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentShareRepository;
import com.lawbackend2.lawbackend2.service.LibDocumentOperationLogService;
import com.lawbackend2.lawbackend2.service.LibDocumentService;
import com.lawbackend2.lawbackend2.service.LibDocumentShareService;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibDocumentShareServiceImpl implements LibDocumentShareService {

    private final LibDocumentShareRepository shareRepository;
    private final LibDocumentRepository documentRepository;
    private final LibDocumentService documentService;
    private final LibDocumentOperationLogService operationLogService;

    @Override
    @Transactional
    public LibShareResponse createShare(LibShareCreateRequest request, Long userId) {
        LibDocument document = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new BusinessException("文档不存在"));

        if (document.getIsDeleted()) {
            throw new BusinessException("文档已被删除");
        }

        String shareCode = generateShareCode();

        LibDocumentShare share = LibDocumentShare.builder()
                .documentId(request.getDocumentId())
                .shareCode(shareCode)
                .sharePassword(request.getSharePassword())
                .permissionType(request.getPermissionType() != null ? request.getPermissionType() : "READ")
                .expireTime(request.getExpireTime())
                .maxAccessCount(request.getMaxAccessCount() != null ? request.getMaxAccessCount() : 0)
                .accessCount(0)
                .isEnabled(true)
                .build();
        share.setCreateUserId(userId);

        share = shareRepository.save(share);

        log.info("创建分享链接成功 - 分享码: {}, 文档ID: {}, 用户ID: {}", shareCode, request.getDocumentId(), userId);
        operationLogService.logOperation(request.getDocumentId(), null, "SHARE", "创建分享链接", null, shareCode, userId, null, null);

        return convertToResponse(share, document);
    }

    @Override
    public LibShareResponse getShareByCode(String shareCode) {
        LibDocumentShare share = shareRepository.findByShareCode(shareCode)
                .orElseThrow(() -> new BusinessException("分享链接不存在"));

        if (!share.getIsEnabled()) {
            throw new BusinessException("分享链接已被禁用");
        }

        if (share.getExpireTime() != null && share.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("分享链接已过期");
        }

        if (share.getMaxAccessCount() > 0 && share.getAccessCount() >= share.getMaxAccessCount()) {
            throw new BusinessException("分享链接访问次数已达上限");
        }

        LibDocument document = documentRepository.findById(share.getDocumentId())
                .orElseThrow(() -> new BusinessException("文档不存在"));

        return convertToResponse(share, document);
    }

    @Override
    public LibDocumentResponse accessSharedDocument(String shareCode, String password, Long userId) {
        LibDocumentShare share = shareRepository.findByShareCode(shareCode)
                .orElseThrow(() -> new BusinessException("分享链接不存在"));

        if (!share.getIsEnabled()) {
            throw new BusinessException("分享链接已被禁用");
        }

        if (share.getExpireTime() != null && share.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("分享链接已过期");
        }

        if (share.getMaxAccessCount() > 0 && share.getAccessCount() >= share.getMaxAccessCount()) {
            throw new BusinessException("分享链接访问次数已达上限");
        }

        if (share.getSharePassword() != null && !share.getSharePassword().isEmpty()) {
            if (password == null || !password.equals(share.getSharePassword())) {
                throw new BusinessException("分享密码错误");
            }
        }

        shareRepository.incrementAccessCount(share.getId());

        return documentService.getDocumentById(share.getDocumentId(), userId);
    }

    @Override
    public void downloadSharedDocument(String shareCode, String password, HttpServletResponse response, Long userId) {
        LibDocumentShare share = shareRepository.findByShareCode(shareCode)
                .orElseThrow(() -> new BusinessException("分享链接不存在"));

        if (!share.getIsEnabled()) {
            throw new BusinessException("分享链接已被禁用");
        }

        if (share.getExpireTime() != null && share.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("分享链接已过期");
        }

        if (share.getMaxAccessCount() > 0 && share.getAccessCount() >= share.getMaxAccessCount()) {
            throw new BusinessException("分享链接访问次数已达上限");
        }

        if (share.getSharePassword() != null && !share.getSharePassword().isEmpty()) {
            if (password == null || !password.equals(share.getSharePassword())) {
                throw new BusinessException("分享密码错误");
            }
        }

        if (!"DOWNLOAD".equals(share.getPermissionType()) && !"EDIT".equals(share.getPermissionType())) {
            throw new BusinessException("该分享链接不支持下载");
        }

        shareRepository.incrementAccessCount(share.getId());
        documentService.downloadDocument(share.getDocumentId(), response, userId);
    }

    @Override
    public LibShareResponse getShareById(Long id) {
        LibDocumentShare share = shareRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分享链接不存在"));

        LibDocument document = documentRepository.findById(share.getDocumentId())
                .orElseThrow(() -> new BusinessException("文档不存在"));

        return convertToResponse(share, document);
    }

    @Override
    @Transactional
    public void deleteShare(Long id, Long userId) {
        LibDocumentShare share = shareRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分享链接不存在"));

        share.setIsDeleted(true);
        share.setUpdateUserId(userId);
        shareRepository.save(share);

        log.info("删除分享链接成功 - 分享ID: {}, 用户ID: {}", id, userId);
        operationLogService.logOperation(share.getDocumentId(), null, "DELETE", "删除分享链接", share.getShareCode(), null, userId, null, null);
    }

    @Override
    @Transactional
    public void disableShare(Long id, Long userId) {
        LibDocumentShare share = shareRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分享链接不存在"));

        share.setIsEnabled(false);
        share.setUpdateUserId(userId);
        shareRepository.save(share);

        log.info("禁用分享链接成功 - 分享ID: {}, 用户ID: {}", id, userId);
    }

    @Override
    @Transactional
    public void enableShare(Long id, Long userId) {
        LibDocumentShare share = shareRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分享链接不存在"));

        share.setIsEnabled(true);
        share.setUpdateUserId(userId);
        shareRepository.save(share);

        log.info("启用分享链接成功 - 分享ID: {}, 用户ID: {}", id, userId);
    }

    @Override
    public String generateShareCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    @Override
    public boolean isShareValid(String shareCode) {
        LibDocumentShare share = shareRepository.findByShareCode(shareCode).orElse(null);
        if (share == null) {
            return false;
        }

        if (!share.getIsEnabled()) {
            return false;
        }

        if (share.getExpireTime() != null && share.getExpireTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (share.getMaxAccessCount() > 0 && share.getAccessCount() >= share.getMaxAccessCount()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean checkSharePassword(String shareCode, String password) {
        LibDocumentShare share = shareRepository.findByShareCode(shareCode).orElse(null);
        if (share == null) {
            return false;
        }

        if (share.getSharePassword() == null || share.getSharePassword().isEmpty()) {
            return true;
        }

        return share.getSharePassword().equals(password);
    }

    private LibShareResponse convertToResponse(LibDocumentShare share, LibDocument document) {
        boolean isExpired = share.getExpireTime() != null && share.getExpireTime().isBefore(LocalDateTime.now());

        return LibShareResponse.builder()
                .id(share.getId())
                .documentId(share.getDocumentId())
                .documentName(document != null ? document.getDocumentName() : null)
                .shareCode(share.getShareCode())
                .shareUrl("/api/lib/share/" + share.getShareCode())
                .sharePassword(share.getSharePassword())
                .permissionType(share.getPermissionType())
                .expireTime(share.getExpireTime())
                .maxAccessCount(share.getMaxAccessCount())
                .accessCount(share.getAccessCount())
                .isEnabled(share.getIsEnabled())
                .isExpired(isExpired)
                .status(share.getStatus())
                .createTime(share.getCreateTime())
                .createUserId(share.getCreateUserId())
                .build();
    }
}

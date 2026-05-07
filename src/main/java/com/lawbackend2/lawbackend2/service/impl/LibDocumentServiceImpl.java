package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.LibDocumentCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.LibDocumentQueryRequest;
import com.lawbackend2.lawbackend2.dto.request.LibDocumentUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentResponse;
import com.lawbackend2.lawbackend2.dto.response.LibOfficePreviewResponse;
import com.lawbackend2.lawbackend2.entity.LibDocument;
import com.lawbackend2.lawbackend2.entity.LibDocumentFolder;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.LibDocumentFavoriteRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentFolderRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentPermissionRelRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentShareRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentVersionRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.LibDocumentOperationLogService;
import com.lawbackend2.lawbackend2.service.LibDocumentService;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.support.TransactionTemplate;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibDocumentServiceImpl implements LibDocumentService {

    private final LibDocumentRepository documentRepository;
    private final LibDocumentFolderRepository folderRepository;
    private final LibDocumentFavoriteRepository favoriteRepository;
    private final LibDocumentVersionRepository versionRepository;
    private final LibDocumentShareRepository shareRepository;
    private final LibDocumentPermissionRelRepository permissionRelRepository;
    private final LibDocumentOperationLogService operationLogService;
    private final UserRepository userRepository;
    private final TransactionTemplate transactionTemplate;

    private String getUsernameById(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElse(null);
    }

    @Override
    @Transactional
    @CacheEvict(value = "libDashboard", allEntries = true)
    public LibDocumentResponse createDocument(LibDocumentCreateRequest request, Long userId) {
        if (request.getFolderId() != null) {
            folderRepository.findById(request.getFolderId())
                    .orElseThrow(() -> new BusinessException("文件夹不存在"));
        }

        if (request.getDocumentCode() != null && documentRepository.existsByDocumentCode(request.getDocumentCode())) {
            throw new BusinessException("文档编码已存在");
        }

        String documentCode = request.getDocumentCode() != null ? request.getDocumentCode() : generateDocumentCode();

        LibDocument document = LibDocument.builder()
                .documentName(request.getDocumentName())
                .documentCode(documentCode)
                .folderId(request.getFolderId())
                .documentType(request.getDocumentType())
                .fileName(request.getFileName())
                .filePath(request.getFilePath())
                .fileSize(request.getFileSize() != null ? request.getFileSize() : 0L)
                .fileExtension(request.getFileExtension())
                .mimeType(request.getMimeType())
                .currentVersion(1)
                .description(request.getDescription())
                .tags(request.getTags())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .build();
        document.setCreateUserId(userId);
        document.setCreateUserName(getUsernameById(userId));

        document = documentRepository.save(document);
        log.info("创建文档成功 - 文档 ID: {}, 名称：{}, 用户 ID: {}", document.getId(), document.getDocumentName(), userId);

        operationLogService.logOperation(document.getId(), document.getFolderId(), "CREATE", "创建文档：" + document.getDocumentName(), null, document.getFilePath(), userId, null, null);

        return convertToResponse(document, userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "libDashboard", allEntries = true)
    public LibDocumentResponse uploadDocument(MultipartFile file, Long folderId, String documentName, String description, String tags, Boolean isPublic, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        if (folderId == null) {
            throw new BusinessException("必须在文件夹内添加文档");
        }

        folderRepository.findById(folderId)
                .orElseThrow(() -> new BusinessException("文件夹不存在"));

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String documentType = getDocumentType(fileExtension);

        String filePath = saveFile(file, folderId);

        String docName = documentName != null ? documentName : originalFileName;

        String documentCode = generateDocumentCode();

        LibDocument document = LibDocument.builder()
                .documentName(docName)
                .documentCode(documentCode)
                .folderId(folderId)
                .documentType(documentType)
                .fileName(originalFileName)
                .filePath(filePath)
                .fileSize(file.getSize())
                .fileExtension(fileExtension)
                .mimeType(file.getContentType())
                .currentVersion(1)
                .description(description)
                .tags(tags)
                .isPublic(isPublic != null ? isPublic : false)
                .build();
        document.setCreateUserId(userId);
        document.setCreateUserName(getUsernameById(userId));

        document = documentRepository.save(document);
        log.info("上传文档成功 - 文档 ID: {}, 名称：{}, 用户 ID: {}", document.getId(), document.getDocumentName(), userId);

        operationLogService.logOperation(document.getId(), folderId, "UPLOAD", "上传文档：" + document.getDocumentName(), null, filePath, userId, null, null);

        return convertToResponse(document, userId);
    }

    @Override
    public LibDocumentResponse getDocumentById(Long id, Long userId) {
        LibDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文档不存在"));

        validateDocumentAccess(document, userId);

        // 异步增加浏览次数，不使用事务避免与 readOnly 冲突
        incrementViewCountAsync(id);

        return convertToResponse(document, userId);
    }

    @Override
    public LibDocumentResponse getDocumentByCode(String documentCode, Long userId) {
        LibDocument document = documentRepository.findByDocumentCode(documentCode)
                .orElseThrow(() -> new BusinessException("文档不存在"));

        validateDocumentAccess(document, userId);

        // 异步增加浏览次数，不使用事务避免与 readOnly 冲突
        incrementViewCountAsync(document.getId());

        return convertToResponse(document, userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "libDashboard", allEntries = true)
    public LibDocumentResponse updateDocument(Long id, LibDocumentUpdateRequest request, Long userId) {
        LibDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文档不存在"));

        if (document.getIsLocked()) {
            throw new BusinessException("文档已被锁定，禁止编辑");
        }

        if (!document.getCreateUserId().equals(userId)) {
            throw new BusinessException("无权限修改该文档");
        }

        if (request.getDocumentName() != null) {
            document.setDocumentName(request.getDocumentName());
        }
        if (request.getDescription() != null) {
            document.setDescription(request.getDescription());
        }
        if (request.getTags() != null) {
            document.setTags(request.getTags());
        }
        if (request.getIsPublic() != null) {
            document.setIsPublic(request.getIsPublic());
        }

        document.setUpdateUserId(userId);
        documentRepository.save(document);

        log.info("更新文档成功 - 文档 ID: {}, 用户 ID: {}", id, userId);
        operationLogService.logOperation(id, document.getFolderId(), "UPDATE", "更新文档", null, null, userId, null, null);

        return convertToResponse(document, userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "libDashboard", allEntries = true)
    public void deleteDocument(Long id, Long userId) {
        LibDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文档不存在，无法删除"));

        if (document.getIsLocked()) {
            throw new BusinessException("文档已被锁定，禁止删除。如需删除，请先解锁文档");
        }

        if (!document.getCreateUserId().equals(userId)) {
            throw new BusinessException("您没有权限删除该文档，只有文档创建者才能删除");
        }

        // 删除当前版本物理文件
        String filePath = document.getFilePath();
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.error("删除文档物理文件失败，文件可能被占用或权限不足 - 文档ID: {}, 路径: {}", id, filePath);
                    throw new BusinessException("删除文档失败：无法删除文档物理文件，请检查文件是否被其他程序占用或您是否有足够的权限");
                }
            }
        }

        // 删除该文档所有历史版本的物理文件
        List<com.lawbackend2.lawbackend2.entity.LibDocumentVersion> versions = versionRepository.findByDocumentIdOrderByVersionNumberDesc(id);
        for (com.lawbackend2.lawbackend2.entity.LibDocumentVersion version : versions) {
            String versionFilePath = version.getFilePath();
            if (versionFilePath != null) {
                File versionFile = new File(versionFilePath);
                if (versionFile.exists()) {
                    boolean deleted = versionFile.delete();
                    if (!deleted) {
                        log.warn("删除版本物理文件失败，文件可能被占用或权限不足 - 版本ID: {}, 路径: {}", version.getId(), versionFilePath);
                    }
                }
            }
        }

        // 级联删除关联数据
        versionRepository.deleteByDocumentId(id);
        shareRepository.deleteByDocumentId(id);
        favoriteRepository.deleteByDocumentId(id);
        permissionRelRepository.deleteByDocumentId(id);

        // 硬删除文档
        documentRepository.deleteById(id);

        log.info("删除文档成功 - 文档 ID: {}, 用户 ID: {}", id, userId);
        operationLogService.logOperation(id, document.getFolderId(), "DELETE", "删除文档", filePath, null, userId, null, null);
    }

    @Override
    public LibDocumentListResponse getDocumentList(LibDocumentQueryRequest request, Long userId) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        Page<LibDocument> documentPage = documentRepository.findAll((root, query, cb) -> {
            var predicates = new java.util.ArrayList<javax.persistence.criteria.Predicate>();
            
            // 添加文件夹过滤条件
            if (request.getFolderId() != null) {
                predicates.add(cb.equal(root.get("folderId"), request.getFolderId()));
            }
            
            // 添加权限过滤条件：公开文档或自己创建的文档
            predicates.add(cb.or(
                    cb.equal(root.get("isPublic"), true),
                    cb.equal(root.get("createUserId"), userId)
            ));
            
            return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        }, pageable);

        return buildDocumentListResponse(documentPage, request.getPage(), request.getSize(), userId);
    }

    @Override
    public LibDocumentListResponse searchDocuments(String keyword, Integer page, Integer size, Long userId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<LibDocument> documentPage = documentRepository.searchByKeywordAndUser(keyword, userId, pageable);

        return buildDocumentListResponse(documentPage, page, size, userId);
    }

    @Override
    public LibDocumentListResponse getDocumentsByFolder(Long folderId, Integer page, Integer size, Long userId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<LibDocument> documentPage = documentRepository.findAll((root, query, cb) -> {
            var predicates = new java.util.ArrayList<javax.persistence.criteria.Predicate>();
            
            predicates.add(cb.equal(root.get("folderId"), folderId));
            predicates.add(cb.or(
                    cb.equal(root.get("isPublic"), true),
                    cb.equal(root.get("createUserId"), userId)
            ));
            
            return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        }, pageable);

        return buildDocumentListResponse(documentPage, page, size, userId);
    }

    @Override
    public LibDocumentListResponse getDocumentsByType(String documentType, Integer page, Integer size, Long userId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<LibDocument> documentPage = documentRepository.findByDocumentTypeAndUser(documentType, userId, pageable);

        return buildDocumentListResponse(documentPage, page, size, userId);
    }

    @Override
    public LibDocumentListResponse getMyDocuments(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<LibDocument> documentPage = documentRepository.findByCreateUserId(userId, pageable);

        return buildDocumentListResponse(documentPage, page, size, userId);
    }

    @Override
    public LibDocumentListResponse getPublicDocuments(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<LibDocument> documentPage = documentRepository.findPublicDocuments(pageable);

        return buildDocumentListResponse(documentPage, page, size, null);
    }

    @Override
    @Transactional
    public void downloadDocument(Long id, HttpServletResponse response, Long userId) {
        LibDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文档不存在"));

        validateDocumentAccess(document, userId);

        File file = new File(document.getFilePath());
        if (!file.exists()) {
            throw new BusinessException("文件不存在");
        }

        String documentName = document.getDocumentName();
        String fileName = document.getFileName();
        log.info("下载文档 - ID: {}, documentName: {}, fileName: {}", id, documentName, fileName);

        String displayName = documentName;
        if (displayName == null || displayName.isBlank()) {
            displayName = fileName;
        } else {
            if (fileName != null && fileName.contains(".")) {
                String extension = fileName.substring(fileName.lastIndexOf("."));
                if (!displayName.contains(".")) {
                    displayName = displayName + extension;
                }
            }
        }

        log.info("最终下载文件名: {}", displayName);

        response.setContentType(document.getMimeType());
        String encodedFilename = URLEncoder.encode(displayName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);
        response.setContentLengthLong(file.length());

        try (InputStream is = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        } catch (IOException e) {
            log.error("下载文档失败：{}", e.getMessage());
            throw new BusinessException("下载文档失败");
        }

        incrementViewCount(id);
        documentRepository.incrementDownloadCount(id);
        operationLogService.logOperation(id, document.getFolderId(), "DOWNLOAD", "下载文档", null, null, userId, null, null);
    }

    @Override
    @Transactional
    public void lockDocument(Long id, Long userId) {
        LibDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文档不存在"));

        document.setIsLocked(true);
        document.setLockedBy(userId);
        document.setLockedTime(LocalDateTime.now());
        document.setUpdateUserId(userId);
        documentRepository.save(document);

        log.info("锁定文档成功 - 文档 ID: {}, 用户 ID: {}", id, userId);
        operationLogService.logOperation(id, document.getFolderId(), "LOCK", "锁定文档", null, null, userId, null, null);
    }

    @Override
    @Transactional
    public void unlockDocument(Long id, Long userId) {
        LibDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文档不存在"));

        if (document.getIsLocked()) {
            Long lockedBy = document.getLockedBy();
            if (lockedBy != null && !lockedBy.equals(userId)) {
                throw new BusinessException("只有锁定人或管理员才能解锁");
            }
        }

        document.setIsLocked(false);
        document.setLockedBy(null);
        document.setLockedTime(null);
        document.setUpdateUserId(userId);
        documentRepository.save(document);

        log.info("解锁文档成功 - 文档 ID: {}, 用户 ID: {}", id, userId);
        operationLogService.logOperation(id, document.getFolderId(), "UNLOCK", "解锁文档", null, null, userId, null, null);
    }

    @Override
    @Transactional
    public void moveDocument(Long documentId, Long newFolderId, Long userId) {
        LibDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessException("文档不存在"));

        if (document.getIsLocked()) {
            throw new BusinessException("文档已被锁定，禁止移动");
        }

        if (newFolderId != null) {
            folderRepository.findById(newFolderId)
                    .orElseThrow(() -> new BusinessException("目标文件夹不存在"));
        }

        document.setFolderId(newFolderId);
        document.setUpdateUserId(userId);
        documentRepository.save(document);

        log.info("移动文档成功 - 文档 ID: {}, 新文件夹 ID: {}, 用户 ID: {}", documentId, newFolderId, userId);
        operationLogService.logOperation(documentId, newFolderId, "MOVE", "移动文档", null, null, userId, null, null);
    }

    @Override
    @Transactional
    public void copyDocument(Long documentId, Long targetFolderId, Long userId) {
        LibDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessException("文档不存在，无法复制"));

        if (document.getIsLocked()) {
            throw new BusinessException("文档已被锁定，禁止复制。如需复制，请先解锁文档");
        }

        if (targetFolderId != null) {
            folderRepository.findById(targetFolderId)
                    .orElseThrow(() -> new BusinessException("目标文件夹不存在，请确认文件夹ID是否正确"));
        }

        // 复制物理文件，避免新旧文档共享同一文件
        String newFilePath = null;
        String sourceFilePath = document.getFilePath();
        if (sourceFilePath != null) {
            File sourceFile = new File(sourceFilePath);
            if (!sourceFile.exists()) {
                throw new BusinessException("复制文档失败：原文档的物理文件不存在或已被删除");
            }

            String baseDir = System.getProperty("user.dir");
            String uploadDir = baseDir + File.separator + "uploads" + File.separator + "documents" + File.separator;
            if (targetFolderId != null) {
                uploadDir += "folder_" + targetFolderId + File.separator;
            }

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String newFileName = timestamp + "_副本_" + document.getFileName();
            File destFile = new File(uploadDir + newFileName);

            try {
                java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                newFilePath = destFile.getAbsolutePath();
            } catch (IOException e) {
                log.error("复制文档物理文件失败 - 原文档ID: {}, 源路径: {}, 目标路径: {}, 错误: {}", documentId, sourceFilePath, destFile.getAbsolutePath(), e.getMessage());
                throw new BusinessException("复制文档失败：无法复制文档文件，请检查磁盘空间或文件权限");
            }
        }

        LibDocument newDocument = LibDocument.builder()
                .documentName(document.getDocumentName() + "_副本")
                .documentCode(generateDocumentCode())
                .folderId(targetFolderId)
                .documentType(document.getDocumentType())
                .fileName(document.getFileName())
                .filePath(newFilePath)
                .fileSize(document.getFileSize())
                .fileExtension(document.getFileExtension())
                .mimeType(document.getMimeType())
                .currentVersion(1)
                .description(document.getDescription())
                .tags(document.getTags())
                .isPublic(document.getIsPublic())
                .build();
        newDocument.setCreateUserId(userId);

        newDocument = documentRepository.save(newDocument);

        log.info("复制文档成功 - 原文档 ID: {}, 新文档 ID: {}, 用户 ID: {}", documentId, newDocument.getId(), userId);
        operationLogService.logOperation(newDocument.getId(), targetFolderId, "COPY", "复制文档", sourceFilePath, newFilePath, userId, null, null);
    }

    @Override
    public boolean hasPermission(Long documentId, Long userId, String permissionType) {
        LibDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessException("文档不存在"));

        // 公开文档：所有人都有READ权限，其他权限需要进一步判断
        if (document.getIsPublic()) {
            return "READ".equals(permissionType) || "PREVIEW".equals(permissionType) || "DOWNLOAD".equals(permissionType);
        }

        // 文档创建者拥有所有权限
        if (document.getCreateUserId() != null && document.getCreateUserId().equals(userId)) {
            return true;
        }

        // 收藏者仅拥有READ/PREVIEW/DOWNLOAD权限
        if (favoriteRepository.existsByDocumentIdAndUserId(documentId, userId)) {
            return "READ".equals(permissionType) || "PREVIEW".equals(permissionType) || "DOWNLOAD".equals(permissionType);
        }

        return false;
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        documentRepository.incrementViewCount(id);
    }

    private void incrementViewCountInternal(Long id) {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                documentRepository.incrementViewCount(id);
            });
        } catch (Exception e) {
            log.error("增加浏览次数失败：{}", e.getMessage());
        }
    }

    private void incrementViewCountAsync(Long id) {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                documentRepository.incrementViewCount(id);
            });
        } catch (Exception e) {
            log.error("增加浏览次数失败：{}", e.getMessage());
        }
    }

    @Override
    public String generateDocumentCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "DOC" + timestamp + uuid;
    }

    @Override
    @Transactional
    public void previewDocument(Long id, HttpServletResponse response, Long userId) {
        LibDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文档不存在，无法预览"));

        validateDocumentAccess(document, userId);

        File file = new File(document.getFilePath());
        if (!file.exists()) {
            throw new BusinessException("文档对应的物理文件不存在或已被删除，无法预览");
        }

        String mimeType = document.getMimeType();
        if (mimeType == null || mimeType.isEmpty()) {
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "inline; filename=\"" + 
                URLEncoder.encode(document.getFileName(), StandardCharsets.UTF_8) + "\"");
        response.setContentLengthLong(file.length());

        try (InputStream is = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        } catch (IOException e) {
            log.error("预览文档失败：{}", e.getMessage());
            throw new BusinessException("预览文档失败");
        }

        incrementViewCount(id);
        operationLogService.logOperation(id, document.getFolderId(), "PREVIEW", "预览文档", null, null, userId, null, null);
    }

    @Override
    public LibOfficePreviewResponse getOfficePreviewConfig(Long id, Long userId, String serverUrl) {
        LibDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文档不存在"));

        String fileExtension = document.getFileExtension();
        if (fileExtension == null) {
            fileExtension = getFileExtension(document.getFileName());
        }

        String documentType = getOfficeDocumentType(fileExtension);
        if (documentType == null) {
            throw new BusinessException("该文档类型不支持 Office 在线预览");
        }

        String documentKey = generateDocumentKey(id);

        String fileUrl = serverUrl + "/api/lib/documents/" + id + "/preview";

        return LibOfficePreviewResponse.builder()
                .documentType(documentType)
                .document(LibOfficePreviewResponse.DocumentInfo.builder()
                        .fileType(fileExtension)
                        .key(documentKey)
                        .title(document.getDocumentName())
                        .url(fileUrl)
                        .build())
                .editorConfig(LibOfficePreviewResponse.EditorConfig.builder()
                        .mode("view")
                        .lang("zh-CN")
                        .user(LibOfficePreviewResponse.UserInfo.builder()
                                .id(userId != null ? userId.toString() : "anonymous")
                                .name("用户")
                                .build())
                        .build())
                .build();
    }

    private String getOfficeDocumentType(String extension) {
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("doc", "word");
        typeMap.put("docx", "word");
        typeMap.put("xls", "cell");
        typeMap.put("xlsx", "cell");
        typeMap.put("ppt", "slide");
        typeMap.put("pptx", "slide");
        typeMap.put("pdf", "word");
        return typeMap.get(extension);
    }

    private String generateDocumentKey(Long documentId) {
        return "doc_" + documentId + "_" + System.currentTimeMillis();
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private String getDocumentType(String fileExtension) {
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("doc", "WORD");
        typeMap.put("docx", "WORD");
        typeMap.put("xls", "EXCEL");
        typeMap.put("xlsx", "EXCEL");
        typeMap.put("ppt", "PPT");
        typeMap.put("pptx", "PPT");
        typeMap.put("pdf", "PDF");
        typeMap.put("txt", "TXT");
        typeMap.put("csv", "CSV");

        return typeMap.getOrDefault(fileExtension.toLowerCase(), "OTHER");
    }

    private String saveFile(MultipartFile file, Long folderId) {
        String baseDir = System.getProperty("user.dir");
        String uploadDir = baseDir + File.separator + "uploads" + File.separator + "documents" + File.separator;
        if (folderId != null) {
            uploadDir += "folder_" + folderId + File.separator;
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String originalFileName = file.getOriginalFilename();

        // 安全检查：过滤路径遍历字符
        if (originalFileName != null) {
            originalFileName = originalFileName.replaceAll("[\\\\/:*?\"<>|]", "_");
            originalFileName = originalFileName.replaceAll("\\.\\./", "_");
            originalFileName = originalFileName.replaceAll("\\./", "_");
            if (originalFileName.isBlank()) {
                originalFileName = "unnamed_file";
            }
        } else {
            originalFileName = "unnamed_file";
        }

        String newFileName = timestamp + "_" + originalFileName;

        try {
            File destFile = new File(uploadDir + newFileName);
            // 确保解析后的路径在目标目录内，防止路径遍历
            String canonicalDestPath = destFile.getCanonicalPath();
            String canonicalDirPath = dir.getCanonicalPath();
            if (!canonicalDestPath.startsWith(canonicalDirPath)) {
                log.error("检测到非法文件路径，可能存在路径遍历攻击 - 文件名: {}", originalFileName);
                throw new BusinessException("上传文件失败：文件名包含非法字符");
            }

            file.transferTo(destFile);
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            log.error("保存文件失败：{}", e.getMessage());
            throw new BusinessException("保存文件失败：无法写入文件，请检查磁盘空间或目录权限");
        }
    }

    private LibDocumentResponse convertToResponse(LibDocument document, Long userId) {
        boolean hasPermission = checkDocumentPermission(document, userId);

        String createUserName = document.getCreateUserName();
        if (createUserName == null && document.getCreateUserId() != null) {
            User user = userRepository.findById(document.getCreateUserId()).orElse(null);
            if (user != null) {
                createUserName = user.getRealName() != null ? user.getRealName() : user.getUsername();
            }
        }

        String folderName = null;
        if (document.getFolderId() != null) {
            folderName = folderRepository.findById(document.getFolderId())
                    .map(f -> f.getFolderName())
                    .orElse(null);
        }

        return LibDocumentResponse.builder()
                .id(document.getId())
                .documentName(document.getDocumentName())
                .documentCode(document.getDocumentCode())
                .folderId(document.getFolderId())
                .folderName(folderName)
                .documentType(document.getDocumentType())
                .fileName(document.getFileName())
                .filePath(document.getFilePath())
                .fileSize(document.getFileSize())
                .fileExtension(document.getFileExtension())
                .mimeType(document.getMimeType())
                .currentVersion(document.getCurrentVersion())
                .description(document.getDescription())
                .tags(document.getTags())
                .isPublic(document.getIsPublic())
                .isLocked(document.getIsLocked())
                .lockedBy(document.getLockedBy())
                .lockedTime(document.getLockedTime())
                .downloadCount(document.getDownloadCount())
                .viewCount(document.getViewCount())
                .createTime(document.getCreateTime())
                .updateTime(document.getUpdateTime())
                .createUserId(document.getCreateUserId())
                .createUserName(createUserName)
                .hasPermission(hasPermission)
                .build();
    }

    private boolean checkDocumentPermission(LibDocument document, Long userId) {
        if (document.getIsPublic()) {
            return true;
        }
        if (userId != null && document.getCreateUserId().equals(userId)) {
            return true;
        }
        if (userId != null) {
            List<com.lawbackend2.lawbackend2.entity.LibDocumentPermissionRel> permRels =
                    permissionRelRepository.findByDocumentId(document.getId());
            for (com.lawbackend2.lawbackend2.entity.LibDocumentPermissionRel rel : permRels) {
                if ("USER".equals(rel.getTargetType()) && userId.equals(rel.getTargetId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void validateDocumentAccess(LibDocument document, Long userId) {
        if (!checkDocumentPermission(document, userId)) {
            throw new BusinessException("无权限访问该文档");
        }
    }

    private LibDocumentListResponse buildDocumentListResponse(Page<LibDocument> documentPage, Integer page, Integer size, Long userId) {
        return LibDocumentListResponse.builder()
                .total(documentPage.getTotalElements())
                .page(page)
                .size(size)
                .totalPages(documentPage.getTotalPages())
                .documents(documentPage.getContent().stream()
                        .map(doc -> convertToResponse(doc, userId))
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public LibDocumentListResponse getRecentDocuments(Integer page, Integer size, Long userId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LibDocument> documentPage = documentRepository.findRecentPublicDocuments(pageable);
        return buildDocumentListResponse(documentPage, page, size, userId);
    }

    @Override
    public LibDocumentListResponse getPopularDocuments(String timeRange, Integer page, Integer size, Long userId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LibDocument> documentPage;
        
        LocalDateTime startTime = null;
        if (timeRange != null && !timeRange.equals("all")) {
            LocalDateTime now = LocalDateTime.now();
            switch (timeRange.toLowerCase()) {
                case "week":
                    startTime = now.minusWeeks(1);
                    break;
                case "month":
                    startTime = now.minusMonths(1);
                    break;
                case "year":
                    startTime = now.minusYears(1);
                    break;
                default:
                    break;
            }
        }
        
        if (startTime != null) {
            documentPage = documentRepository.findPopularPublicDocumentsByTimeRange(startTime, pageable);
        } else {
            documentPage = documentRepository.findPopularPublicDocuments(pageable);
        }
        
        return buildDocumentListResponse(documentPage, page, size, userId);
    }
}

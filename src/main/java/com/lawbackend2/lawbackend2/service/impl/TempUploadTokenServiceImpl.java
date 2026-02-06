package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.TempUploadToken;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.repository.TempUploadTokenRepository;
import com.lawbackend2.lawbackend2.service.FileService;
import com.lawbackend2.lawbackend2.service.TempUploadTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class TempUploadTokenServiceImpl implements TempUploadTokenService {

    @Value("${file.upload.temp-token-expire-minutes:30}")
    private Integer defaultExpireMinutes;

    @Autowired
    private TempUploadTokenRepository tempUploadTokenRepository;

    @Autowired
    private FileRecordRepository fileRecordRepository;

    @Autowired
    private FileService fileService;

    @Override
    @Transactional
    public TempUploadToken createToken(String bizType, Long userId, String description) {
        return createToken(bizType, userId, description, defaultExpireMinutes);
    }

    @Override
    @Transactional
    public TempUploadToken createToken(String bizType, Long userId, String description, Integer expireMinutes) {
        String token = generateToken();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusMinutes(expireMinutes);

        TempUploadToken tempToken = new TempUploadToken();
        tempToken.setToken(token);
        tempToken.setBizType(bizType);
        tempToken.setUserId(userId);
        tempToken.setExpireTime(expireTime);
        tempToken.setStatus("ACTIVE");
        tempToken.setFileCount(0);
        tempToken.setDescription(description);
        tempToken.setCreateUserId(userId);
        tempToken.setUpdateUserId(userId);
        tempToken.setCreateTime(now);
        tempToken.setUpdateTime(now);

        TempUploadToken savedToken = tempUploadTokenRepository.save(tempToken);
        log.info("创建临时上传Token成功: token={}, bizType={}, userId={}, expireTime={}",
                token, bizType, userId, expireTime);

        return savedToken;
    }

    @Override
    public TempUploadToken validateToken(String token) {
        LocalDateTime now = LocalDateTime.now();
        Optional<TempUploadToken> tokenOpt = tempUploadTokenRepository.findValidToken(token, now);

        if (tokenOpt.isEmpty()) {
            throw new BusinessException("上传令牌无效或已过期");
        }

        return tokenOpt.get();
    }

    @Override
    @Transactional
    public FileRecord uploadFileByToken(String token, MultipartFile file, String description) {
        TempUploadToken tempToken = validateToken(token);

        FileRecord fileRecord = fileService.uploadFile(file, "TEMP_UPLOAD", tempToken.getToken());

        if (description != null && !description.isEmpty()) {
            fileRecord.setDescription(description);
            fileRecordRepository.save(fileRecord);
        }

        tempUploadTokenRepository.incrementFileCount(tempToken.getId(), LocalDateTime.now());

        log.info("通过Token上传文件成功: token={}, fileId={}, fileName={}",
                token, fileRecord.getId(), fileRecord.getOriginalFileName());

        return fileRecord;
    }

    @Override
    @Transactional
    public List<FileRecord> uploadFilesByToken(String token, List<MultipartFile> files, List<String> descriptions) {
        TempUploadToken tempToken = validateToken(token);
        List<FileRecord> uploadedFiles = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String description = (descriptions != null && i < descriptions.size()) ? descriptions.get(i) : null;
            FileRecord fileRecord = uploadFileByToken(token, file, description);
            uploadedFiles.add(fileRecord);
        }

        return uploadedFiles;
    }

    @Override
    public List<FileRecord> getFilesByToken(String token) {
        TempUploadToken tempToken = validateToken(token);
        return fileRecordRepository.findByBizTypeAndBizId("TEMP_UPLOAD", tempToken.getToken());
    }

    @Override
    @Transactional
    public List<FileRecord> transferFilesToBiz(String token, String bizType, String bizId) {
        TempUploadToken tempToken = validateToken(token);
        List<FileRecord> tempFiles = fileRecordRepository.findByBizTypeAndBizId("TEMP_UPLOAD", tempToken.getToken());

        if (tempFiles.isEmpty()) {
            log.warn("Token下没有文件需要转移: token={}", token);
            return new ArrayList<>();
        }

        List<FileRecord> transferredFiles = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (FileRecord fileRecord : tempFiles) {
            fileRecord.setBizType(bizType);
            fileRecord.setBizId(bizId);
            fileRecord.setUpdateTime(now);
            fileRecord.setUpdateUserId(tempToken.getUserId());
            fileRecordRepository.save(fileRecord);
            transferredFiles.add(fileRecord);
        }

        tempToken.setStatus("USED");
        tempToken.setUpdateTime(now);
        tempToken.setUpdateUserId(tempToken.getUserId());
        tempUploadTokenRepository.save(tempToken);

        log.info("文件转移成功: token={}, bizType={}, bizId={}, fileCount={}",
                token, bizType, bizId, transferredFiles.size());

        return transferredFiles;
    }

    @Override
    @Transactional
    public void cancelToken(String token) {
        Optional<TempUploadToken> tokenOpt = tempUploadTokenRepository.findByToken(token);
        if (tokenOpt.isPresent()) {
            TempUploadToken tempToken = tokenOpt.get();
            tempToken.setStatus("CANCELLED");
            tempToken.setUpdateTime(LocalDateTime.now());
            tempUploadTokenRepository.save(tempToken);

            List<FileRecord> tempFiles = fileRecordRepository.findByBizTypeAndBizId("TEMP_UPLOAD", token);
            for (FileRecord fileRecord : tempFiles) {
                fileRecord.setStatus("CANCELLED");
                fileRecord.setUpdateTime(LocalDateTime.now());
                fileRecordRepository.save(fileRecord);

                try {
                    Path filePath = Paths.get(fileRecord.getFilePath());
                    java.nio.file.Files.deleteIfExists(filePath);
                } catch (Exception e) {
                    log.warn("删除临时文件失败: filePath={}, error={}", fileRecord.getFilePath(), e.getMessage());
                }
            }

            log.info("取消Token成功: token={}, 删除文件数={}", token, tempFiles.size());
        }
    }

    @Override
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int updatedCount = tempUploadTokenRepository.markExpiredTokens(now);
        log.info("清理过期Token完成: 更新数量={}", updatedCount);
    }

    @Override
    public TempUploadToken getTokenInfo(String token) {
        return tempUploadTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Token不存在"));
    }

    private String generateToken() {
        return "TEMP" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}

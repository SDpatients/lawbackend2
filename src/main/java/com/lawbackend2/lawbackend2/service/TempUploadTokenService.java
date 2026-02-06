package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.TempUploadToken;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TempUploadTokenService {

    TempUploadToken createToken(String bizType, Long userId, String description);

    TempUploadToken createToken(String bizType, Long userId, String description, Integer expireMinutes);

    TempUploadToken validateToken(String token);

    FileRecord uploadFileByToken(String token, MultipartFile file, String description);

    List<FileRecord> uploadFilesByToken(String token, List<MultipartFile> files, List<String> descriptions);

    List<FileRecord> getFilesByToken(String token);

    List<FileRecord> transferFilesToBiz(String token, String bizType, String bizId);

    void cancelToken(String token);

    void cleanupExpiredTokens();

    TempUploadToken getTokenInfo(String token);
}

package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.LibShareCreateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentResponse;
import com.lawbackend2.lawbackend2.dto.response.LibShareResponse;

import javax.servlet.http.HttpServletResponse;

public interface LibDocumentShareService {

    LibShareResponse createShare(LibShareCreateRequest request, Long userId);

    LibShareResponse getShareByCode(String shareCode);

    LibDocumentResponse accessSharedDocument(String shareCode, String password, Long userId);

    void downloadSharedDocument(String shareCode, String password, HttpServletResponse response, Long userId);

    LibShareResponse getShareById(Long id);

    void deleteShare(Long id, Long userId);

    void disableShare(Long id, Long userId);

    void enableShare(Long id, Long userId);

    String generateShareCode();

    boolean isShareValid(String shareCode);

    boolean checkSharePassword(String shareCode, String password);
}

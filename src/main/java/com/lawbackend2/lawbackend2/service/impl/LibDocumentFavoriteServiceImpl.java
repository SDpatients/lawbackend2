package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.response.LibFavoriteListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibFavoriteResponse;
import com.lawbackend2.lawbackend2.entity.LibDocument;
import com.lawbackend2.lawbackend2.entity.LibDocumentFavorite;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.LibDocumentFavoriteRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentRepository;
import com.lawbackend2.lawbackend2.service.LibDocumentFavoriteService;
import com.lawbackend2.lawbackend2.service.LibDocumentOperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibDocumentFavoriteServiceImpl implements LibDocumentFavoriteService {

    private final LibDocumentFavoriteRepository favoriteRepository;
    private final LibDocumentRepository documentRepository;
    private final LibDocumentOperationLogService operationLogService;

    @Override
    @Transactional
    public LibFavoriteResponse addFavorite(Long documentId, String folderName, Long userId) {
        LibDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessException("文档不存在"));

        if (favoriteRepository.existsByDocumentIdAndUserId(documentId, userId)) {
            throw new BusinessException("已收藏该文档");
        }

        LibDocumentFavorite favorite = LibDocumentFavorite.builder()
                .documentId(documentId)
                .userId(userId)
                .folderName(folderName != null ? folderName : "默认收藏夹")
                .sortOrder(0)
                .build();
        favorite.setCreateUserId(userId);

        favorite = favoriteRepository.save(favorite);

        log.info("添加收藏成功 - 文档ID: {}, 用户ID: {}", documentId, userId);
        operationLogService.logOperation(documentId, null, "FAVORITE", "添加收藏", null, null, userId, null, null);

        return convertToResponse(favorite, document);
    }

    @Override
    @Transactional
    public void removeFavorite(Long documentId, Long userId) {
        LibDocumentFavorite favorite = favoriteRepository.findByDocumentIdAndUserId(documentId, userId)
                .orElseThrow(() -> new BusinessException("未收藏该文档"));

        // 硬删除收藏
        favoriteRepository.deleteById(favorite.getId());

        log.info("取消收藏成功 - 文档ID: {}, 用户ID: {}", documentId, userId);
        operationLogService.logOperation(documentId, null, "UNFAVORITE", "取消收藏", null, null, userId, null, null);
    }

    @Override
    public LibFavoriteListResponse getMyFavorites(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LibDocumentFavorite> favoritePage = favoriteRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);

        List<LibFavoriteResponse> favorites = favoritePage.getContent().stream()
                .map(favorite -> {
                    LibDocument document = documentRepository.findById(favorite.getDocumentId()).orElse(null);
                    return convertToResponse(favorite, document);
                })
                .collect(Collectors.toList());

        return LibFavoriteListResponse.builder()
                .total(favoritePage.getTotalElements())
                .page(page)
                .size(size)
                .totalPages(favoritePage.getTotalPages())
                .favorites(favorites)
                .build();
    }

    @Override
    public LibFavoriteListResponse getFavoritesByFolder(String folderName, Long userId, Integer page, Integer size) {
        List<LibDocumentFavorite> allFavorites = favoriteRepository.findByUserIdAndFolderName(userId, folderName);

        int start = (page - 1) * size;
        int end = Math.min(start + size, allFavorites.size());
        List<LibDocumentFavorite> pagedFavorites = start < allFavorites.size() ? allFavorites.subList(start, end) : new ArrayList<>();

        List<LibFavoriteResponse> favorites = pagedFavorites.stream()
                .map(favorite -> {
                    LibDocument document = documentRepository.findById(favorite.getDocumentId()).orElse(null);
                    return convertToResponse(favorite, document);
                })
                .collect(Collectors.toList());

        int total = allFavorites.size();
        int totalPages = (int) Math.ceil((double) total / size);

        return LibFavoriteListResponse.builder()
                .total((long) total)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .favorites(favorites)
                .build();
    }

    @Override
    public List<String> getFavoriteFolders(Long userId) {
        return favoriteRepository.findDistinctFolderNamesByUserId(userId);
    }

    @Override
    public boolean isFavorited(Long documentId, Long userId) {
        return favoriteRepository.existsByDocumentIdAndUserId(documentId, userId);
    }

    @Override
    @Transactional
    public void moveFavorite(Long documentId, String newFolderName, Long userId) {
        LibDocumentFavorite favorite = favoriteRepository.findByDocumentIdAndUserId(documentId, userId)
                .orElseThrow(() -> new BusinessException("未收藏该文档"));

        favorite.setFolderName(newFolderName);
        favorite.setUpdateUserId(userId);
        favoriteRepository.save(favorite);

        log.info("移动收藏成功 - 文档ID: {}, 新文件夹: {}, 用户ID: {}", documentId, newFolderName, userId);
    }

    private LibFavoriteResponse convertToResponse(LibDocumentFavorite favorite, LibDocument document) {
        return LibFavoriteResponse.builder()
                .id(favorite.getId())
                .documentId(favorite.getDocumentId())
                .documentName(document != null ? document.getDocumentName() : null)
                .documentType(document != null ? document.getDocumentType() : null)
                .fileName(document != null ? document.getFileName() : null)
                .fileSize(document != null ? document.getFileSize() : null)
                .folderName(favorite.getFolderName())
                .sortOrder(favorite.getSortOrder())
                .createTime(favorite.getCreateTime())
                .documentCreateTime(document != null ? document.getCreateTime() : null)
                .build();
    }
}

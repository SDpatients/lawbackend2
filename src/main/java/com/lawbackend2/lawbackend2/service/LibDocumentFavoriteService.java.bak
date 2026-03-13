package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.response.LibFavoriteListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibFavoriteResponse;

import java.util.List;

public interface LibDocumentFavoriteService {

    LibFavoriteResponse addFavorite(Long documentId, String folderName, Long userId);

    void removeFavorite(Long documentId, Long userId);

    LibFavoriteListResponse getMyFavorites(Long userId, Integer page, Integer size);

    LibFavoriteListResponse getFavoritesByFolder(String folderName, Long userId, Integer page, Integer size);

    List<String> getFavoriteFolders(Long userId);

    boolean isFavorited(Long documentId, Long userId);

    void moveFavorite(Long documentId, String newFolderName, Long userId);
}

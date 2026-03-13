package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.response.LibFavoriteListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibFavoriteResponse;
import com.lawbackend2.lawbackend2.entity.LibDocument;
import com.lawbackend2.lawbackend2.entity.LibDocumentFavorite;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.LibDocumentFavoriteRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentRepository;
import com.lawbackend2.lawbackend2.service.impl.LibDocumentFavoriteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibDocumentFavoriteServiceTest {

    @Mock
    private LibDocumentFavoriteRepository favoriteRepository;

    @Mock
    private LibDocumentRepository documentRepository;

    @Mock
    private LibDocumentOperationLogService operationLogService;

    @InjectMocks
    private LibDocumentFavoriteServiceImpl favoriteService;

    private LibDocument mockDocument;
    private LibDocumentFavorite mockFavorite;

    @BeforeEach
    void setUp() {
        mockDocument = LibDocument.builder()
                .documentName("测试文档")
                .documentType("WORD")
                .fileName("测试文档.docx")
                .fileSize(10240L)
                .build();
        mockDocument.setId(1L);
        mockDocument.setStatus("ACTIVE");
        mockDocument.setIsDeleted(false);

        mockFavorite = LibDocumentFavorite.builder()
                .documentId(1L)
                .userId(1L)
                .folderName("默认收藏夹")
                .sortOrder(0)
                .build();
        mockFavorite.setId(1L);
        mockFavorite.setStatus("ACTIVE");
        mockFavorite.setIsDeleted(false);
    }

    @Test
    void testAddFavorite_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(favoriteRepository.existsByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(false);
        when(favoriteRepository.save(any(LibDocumentFavorite.class))).thenAnswer(invocation -> {
            LibDocumentFavorite favorite = invocation.getArgument(0);
            favorite.setId(1L);
            return favorite;
        });

        LibFavoriteResponse response = favoriteService.addFavorite(1L, "默认收藏夹", 1L);

        assertNotNull(response);
        assertEquals(1L, response.getDocumentId());
        verify(favoriteRepository, times(1)).save(any(LibDocumentFavorite.class));
    }

    @Test
    void testAddFavorite_DocumentNotFound() {
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> favoriteService.addFavorite(999L, null, 1L));
    }

    @Test
    void testAddFavorite_AlreadyFavorited() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));
        when(favoriteRepository.existsByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> favoriteService.addFavorite(1L, null, 1L));
    }

    @Test
    void testRemoveFavorite_Success() {
        when(favoriteRepository.findByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(Optional.of(mockFavorite));
        when(favoriteRepository.save(any(LibDocumentFavorite.class))).thenReturn(mockFavorite);

        favoriteService.removeFavorite(1L, 1L);

        verify(favoriteRepository, times(1)).save(any(LibDocumentFavorite.class));
    }

    @Test
    void testRemoveFavorite_NotFavorited() {
        when(favoriteRepository.findByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> favoriteService.removeFavorite(1L, 1L));
    }

    @Test
    void testGetMyFavorites() {
        List<LibDocumentFavorite> favorites = new ArrayList<>();
        favorites.add(mockFavorite);
        Page<LibDocumentFavorite> page = new PageImpl<>(favorites);

        when(favoriteRepository.findByUserIdAndIsDeletedFalseOrderByCreateTimeDesc(eq(1L), any(Pageable.class))).thenReturn(page);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));

        LibFavoriteListResponse response = favoriteService.getMyFavorites(1L, 1, 10);

        assertNotNull(response);
        assertEquals(1, response.getFavorites().size());
    }

    @Test
    void testGetFavoritesByFolder() {
        List<LibDocumentFavorite> favorites = new ArrayList<>();
        favorites.add(mockFavorite);

        when(favoriteRepository.findByUserIdAndFolderName(1L, "默认收藏夹")).thenReturn(favorites);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));

        LibFavoriteListResponse response = favoriteService.getFavoritesByFolder("默认收藏夹", 1L, 1, 10);

        assertNotNull(response);
        assertEquals(1, response.getFavorites().size());
    }

    @Test
    void testGetFavoriteFolders() {
        List<String> folders = new ArrayList<>();
        folders.add("默认收藏夹");
        folders.add("工作文档");

        when(favoriteRepository.findDistinctFolderNamesByUserId(1L)).thenReturn(folders);

        List<String> result = favoriteService.getFavoriteFolders(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testIsFavorited_True() {
        when(favoriteRepository.existsByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(true);

        boolean result = favoriteService.isFavorited(1L, 1L);

        assertTrue(result);
    }

    @Test
    void testIsFavorited_False() {
        when(favoriteRepository.existsByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(false);

        boolean result = favoriteService.isFavorited(1L, 1L);

        assertFalse(result);
    }

    @Test
    void testMoveFavorite_Success() {
        when(favoriteRepository.findByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(Optional.of(mockFavorite));
        when(favoriteRepository.save(any(LibDocumentFavorite.class))).thenReturn(mockFavorite);

        favoriteService.moveFavorite(1L, "工作文档", 1L);

        verify(favoriteRepository, times(1)).save(any(LibDocumentFavorite.class));
    }

    @Test
    void testMoveFavorite_NotFavorited() {
        when(favoriteRepository.findByDocumentIdAndUserIdAndIsDeletedFalse(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> favoriteService.moveFavorite(1L, "工作文档", 1L));
    }
}

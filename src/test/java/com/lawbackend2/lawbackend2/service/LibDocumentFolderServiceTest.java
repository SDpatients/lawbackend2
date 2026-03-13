package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.LibFolderCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.LibFolderUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.LibDocumentTreeResponse;
import com.lawbackend2.lawbackend2.dto.response.LibFolderListResponse;
import com.lawbackend2.lawbackend2.dto.response.LibFolderResponse;
import com.lawbackend2.lawbackend2.entity.LibDocumentFolder;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.LibDocumentFolderRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentRepository;
import com.lawbackend2.lawbackend2.service.impl.LibDocumentFolderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibDocumentFolderServiceTest {

    @Mock
    private LibDocumentFolderRepository folderRepository;

    @Mock
    private LibDocumentRepository documentRepository;

    @Mock
    private LibDocumentOperationLogService operationLogService;

    @InjectMocks
    private LibDocumentFolderServiceImpl folderService;

    private LibDocumentFolder mockFolder;
    private LibDocumentFolder mockSubFolder;

    @BeforeEach
    void setUp() {
        mockFolder = LibDocumentFolder.builder()
                .folderName("测试文件夹")
                .folderPath("/测试文件夹")
                .parentId(null)
                .folderLevel(1)
                .sortOrder(0)
                .description("测试描述")
                .isPublic(false)
                .build();
        mockFolder.setId(1L);
        mockFolder.setStatus("ACTIVE");
        mockFolder.setIsDeleted(false);

        mockSubFolder = LibDocumentFolder.builder()
                .folderName("子文件夹")
                .folderPath("/测试文件夹/子文件夹")
                .parentId(1L)
                .folderLevel(2)
                .sortOrder(0)
                .build();
        mockSubFolder.setId(2L);
        mockSubFolder.setStatus("ACTIVE");
        mockSubFolder.setIsDeleted(false);
    }

    @Test
    void testCreateFolder_Success() {
        LibFolderCreateRequest request = LibFolderCreateRequest.builder()
                .folderName("新文件夹")
                .description("新文件夹描述")
                .build();

        when(folderRepository.existsByFolderPath(anyString())).thenReturn(false);
        when(folderRepository.save(any(LibDocumentFolder.class))).thenAnswer(invocation -> {
            LibDocumentFolder folder = invocation.getArgument(0);
            folder.setId(3L);
            return folder;
        });

        LibFolderResponse response = folderService.createFolder(request, 1L);

        assertNotNull(response);
        assertEquals("新文件夹", response.getFolderName());
        verify(folderRepository, times(1)).save(any(LibDocumentFolder.class));
    }

    @Test
    void testCreateFolder_WithParent() {
        LibFolderCreateRequest request = LibFolderCreateRequest.builder()
                .folderName("子文件夹")
                .parentId(1L)
                .build();

        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));
        when(folderRepository.existsByParentIdAndFolderNameAndIsDeletedFalse(1L, "子文件夹")).thenReturn(false);
        when(folderRepository.existsByFolderPath(anyString())).thenReturn(false);
        when(folderRepository.save(any(LibDocumentFolder.class))).thenAnswer(invocation -> {
            LibDocumentFolder folder = invocation.getArgument(0);
            folder.setId(3L);
            return folder;
        });

        LibFolderResponse response = folderService.createFolder(request, 1L);

        assertNotNull(response);
        assertEquals("子文件夹", response.getFolderName());
    }

    @Test
    void testCreateFolder_DuplicateName() {
        LibFolderCreateRequest request = LibFolderCreateRequest.builder()
                .folderName("测试文件夹")
                .parentId(1L)
                .build();

        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));
        when(folderRepository.existsByParentIdAndFolderNameAndIsDeletedFalse(1L, "测试文件夹")).thenReturn(true);

        assertThrows(BusinessException.class, () -> folderService.createFolder(request, 1L));
    }

    @Test
    void testGetFolderById_Success() {
        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));
        when(documentRepository.countByFolderId(1L)).thenReturn(5L);
        when(folderRepository.countByParentId(1L)).thenReturn(2L);

        LibFolderResponse response = folderService.getFolderById(1L);

        assertNotNull(response);
        assertEquals("测试文件夹", response.getFolderName());
        assertEquals(5L, response.getDocumentCount());
        assertEquals(2L, response.getSubFolderCount());
    }

    @Test
    void testGetFolderById_NotFound() {
        when(folderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> folderService.getFolderById(999L));
    }

    @Test
    void testUpdateFolder_Success() {
        LibFolderUpdateRequest request = LibFolderUpdateRequest.builder()
                .folderName("更新后的文件夹")
                .description("更新后的描述")
                .build();

        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));
        when(folderRepository.existsByParentIdAndFolderNameAndIsDeletedFalse(null, "更新后的文件夹")).thenReturn(false);
        when(folderRepository.save(any(LibDocumentFolder.class))).thenReturn(mockFolder);

        LibFolderResponse response = folderService.updateFolder(1L, request, 1L);

        assertNotNull(response);
        verify(folderRepository, times(1)).save(any(LibDocumentFolder.class));
    }

    @Test
    void testDeleteFolder_Success() {
        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));
        when(documentRepository.countByFolderId(1L)).thenReturn(0L);
        when(folderRepository.countByParentId(1L)).thenReturn(0L);
        when(folderRepository.save(any(LibDocumentFolder.class))).thenReturn(mockFolder);

        folderService.deleteFolder(1L, 1L);

        verify(folderRepository, times(1)).save(any(LibDocumentFolder.class));
    }

    @Test
    void testDeleteFolder_HasDocuments() {
        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));
        when(documentRepository.countByFolderId(1L)).thenReturn(5L);

        assertThrows(BusinessException.class, () -> folderService.deleteFolder(1L, 1L));
    }

    @Test
    void testGetSubFolders() {
        List<LibDocumentFolder> subFolders = new ArrayList<>();
        subFolders.add(mockSubFolder);

        when(folderRepository.findByParentIdAndIsDeletedFalseOrderBySortOrderAsc(1L)).thenReturn(subFolders);
        when(documentRepository.countByFolderId(2L)).thenReturn(3L);
        when(folderRepository.countByParentId(2L)).thenReturn(0L);

        List<LibFolderResponse> response = folderService.getSubFolders(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("子文件夹", response.get(0).getFolderName());
    }

    @Test
    void testGetFolderTree() {
        List<LibDocumentFolder> rootFolders = new ArrayList<>();
        rootFolders.add(mockFolder);

        when(folderRepository.findByParentIdIsNullAndIsDeletedFalseOrderBySortOrderAsc()).thenReturn(rootFolders);
        when(folderRepository.findByParentIdAndIsDeletedFalseOrderBySortOrderAsc(1L)).thenReturn(new ArrayList<>());
        when(documentRepository.countByFolderId(1L)).thenReturn(5L);

        LibDocumentTreeResponse response = folderService.getFolderTree();

        assertNotNull(response);
        assertEquals("文档库", response.getName());
        assertNotNull(response.getChildren());
    }

    @Test
    void testMoveFolder_Success() {
        LibDocumentFolder targetFolder = LibDocumentFolder.builder()
                .folderName("目标文件夹")
                .folderPath("/目标文件夹")
                .folderLevel(1)
                .build();
        targetFolder.setId(3L);

        when(folderRepository.findById(2L)).thenReturn(Optional.of(mockSubFolder));
        when(folderRepository.findById(3L)).thenReturn(Optional.of(targetFolder));
        when(folderRepository.existsByParentIdAndFolderNameAndIsDeletedFalse(3L, "子文件夹")).thenReturn(false);
        when(folderRepository.save(any(LibDocumentFolder.class))).thenReturn(mockSubFolder);

        folderService.moveFolder(2L, 3L, 1L);

        verify(folderRepository, times(1)).save(any(LibDocumentFolder.class));
    }

    @Test
    void testMoveFolder_ToSelf() {
        when(folderRepository.findById(1L)).thenReturn(Optional.of(mockFolder));

        assertThrows(BusinessException.class, () -> folderService.moveFolder(1L, 1L, 1L));
    }

    @Test
    void testGetDocumentCount() {
        when(documentRepository.countByFolderId(1L)).thenReturn(10L);

        Long count = folderService.getDocumentCount(1L);

        assertEquals(10L, count);
    }
}

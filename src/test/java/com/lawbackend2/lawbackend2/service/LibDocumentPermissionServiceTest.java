package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.LibPermissionGrantRequest;
import com.lawbackend2.lawbackend2.dto.response.LibPermissionResponse;
import com.lawbackend2.lawbackend2.entity.LibDocumentPermission;
import com.lawbackend2.lawbackend2.entity.LibDocumentPermissionRel;
import com.lawbackend2.lawbackend2.entity.LibFolderPermission;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.LibDocumentPermissionRelRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentPermissionRepository;
import com.lawbackend2.lawbackend2.repository.LibFolderPermissionRepository;
import com.lawbackend2.lawbackend2.service.impl.LibDocumentPermissionServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibDocumentPermissionServiceTest {

    @Mock
    private LibDocumentPermissionRepository permissionRepository;

    @Mock
    private LibFolderPermissionRepository folderPermissionRepository;

    @Mock
    private LibDocumentPermissionRelRepository documentPermissionRelRepository;

    @InjectMocks
    private LibDocumentPermissionServiceImpl permissionService;

    private LibDocumentPermission mockPermission;

    @BeforeEach
    void setUp() {
        mockPermission = LibDocumentPermission.builder()
                .permissionName("查看权限")
                .permissionCode("DOC_READ")
                .permissionType("READ")
                .description("查看文档内容和信息")
                .sortOrder(1)
                .build();
        mockPermission.setId(1L);
        mockPermission.setStatus("ACTIVE");
        mockPermission.setIsDeleted(false);
    }

    @Test
    void testGetAllPermissions() {
        List<LibDocumentPermission> permissions = new ArrayList<>();
        permissions.add(mockPermission);

        when(permissionRepository.findAllActive()).thenReturn(permissions);

        List<LibPermissionResponse> response = permissionService.getAllPermissions();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("查看权限", response.get(0).getPermissionName());
    }

    @Test
    void testGetPermissionById_Success() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(mockPermission));

        LibPermissionResponse response = permissionService.getPermissionById(1L);

        assertNotNull(response);
        assertEquals("查看权限", response.getPermissionName());
    }

    @Test
    void testGetPermissionById_NotFound() {
        when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> permissionService.getPermissionById(999L));
    }

    @Test
    void testGetPermissionByCode_Success() {
        when(permissionRepository.findByPermissionCode("DOC_READ")).thenReturn(Optional.of(mockPermission));

        LibPermissionResponse response = permissionService.getPermissionByCode("DOC_READ");

        assertNotNull(response);
        assertEquals("DOC_READ", response.getPermissionCode());
    }

    @Test
    void testGetPermissionByCode_NotFound() {
        when(permissionRepository.findByPermissionCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> permissionService.getPermissionByCode("INVALID"));
    }

    @Test
    void testGrantFolderPermission_Success() {
        LibPermissionGrantRequest request = LibPermissionGrantRequest.builder()
                .permissionId(1L)
                .targetType("USER")
                .targetId(2L)
                .isInherit(true)
                .build();

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(mockPermission));
        when(folderPermissionRepository.findByFolderPermissionTarget(1L, 1L, "USER", 2L)).thenReturn(null);
        when(folderPermissionRepository.save(any(LibFolderPermission.class))).thenAnswer(invocation -> {
            LibFolderPermission fp = invocation.getArgument(0);
            fp.setId(1L);
            return fp;
        });

        permissionService.grantFolderPermission(1L, request, 1L);

        verify(folderPermissionRepository, times(1)).save(any(LibFolderPermission.class));
    }

    @Test
    void testGrantFolderPermission_AlreadyGranted() {
        LibPermissionGrantRequest request = LibPermissionGrantRequest.builder()
                .permissionId(1L)
                .targetType("USER")
                .targetId(2L)
                .build();

        LibFolderPermission existingPermission = LibFolderPermission.builder()
                .folderId(1L)
                .permissionId(1L)
                .targetType("USER")
                .targetId(2L)
                .build();
        existingPermission.setId(1L);

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(mockPermission));
        when(folderPermissionRepository.findByFolderPermissionTarget(1L, 1L, "USER", 2L)).thenReturn(existingPermission);

        assertThrows(BusinessException.class, () -> permissionService.grantFolderPermission(1L, request, 1L));
    }

    @Test
    void testGrantDocumentPermission_Success() {
        LibPermissionGrantRequest request = LibPermissionGrantRequest.builder()
                .permissionId(1L)
                .targetType("USER")
                .targetId(2L)
                .build();

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(mockPermission));
        when(documentPermissionRelRepository.findByDocumentIdAndTarget(1L, "USER", 2L)).thenReturn(new ArrayList<>());
        when(documentPermissionRelRepository.save(any(LibDocumentPermissionRel.class))).thenAnswer(invocation -> {
            LibDocumentPermissionRel dpr = invocation.getArgument(0);
            dpr.setId(1L);
            return dpr;
        });

        permissionService.grantDocumentPermission(1L, request, 1L);

        verify(documentPermissionRelRepository, times(1)).save(any(LibDocumentPermissionRel.class));
    }

    @Test
    void testRevokeFolderPermission_Success() {
        LibFolderPermission existingPermission = LibFolderPermission.builder()
                .folderId(1L)
                .permissionId(1L)
                .targetType("USER")
                .targetId(2L)
                .build();
        existingPermission.setId(1L);

        when(folderPermissionRepository.findByFolderPermissionTarget(1L, 1L, "USER", 2L)).thenReturn(existingPermission);
        when(folderPermissionRepository.save(any(LibFolderPermission.class))).thenReturn(existingPermission);

        permissionService.revokeFolderPermission(1L, 1L, "USER", 2L);

        verify(folderPermissionRepository, times(1)).save(any(LibFolderPermission.class));
    }

    @Test
    void testRevokeFolderPermission_NotFound() {
        when(folderPermissionRepository.findByFolderPermissionTarget(1L, 1L, "USER", 2L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> 
            permissionService.revokeFolderPermission(1L, 1L, "USER", 2L));
    }

    @Test
    void testGetFolderPermissions() {
        LibFolderPermission fp = LibFolderPermission.builder()
                .folderId(1L)
                .permissionId(1L)
                .targetType("USER")
                .targetId(2L)
                .isInherit(true)
                .build();
        fp.setId(1L);
        fp.setStatus("ACTIVE");
        fp.setIsDeleted(false);

        List<LibFolderPermission> permissions = new ArrayList<>();
        permissions.add(fp);

        when(folderPermissionRepository.findByFolderIdAndIsDeletedFalse(1L)).thenReturn(permissions);
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(mockPermission));

        List<?> response = permissionService.getFolderPermissions(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void testGetDocumentPermissions() {
        LibDocumentPermissionRel dpr = LibDocumentPermissionRel.builder()
                .documentId(1L)
                .permissionId(1L)
                .targetType("USER")
                .targetId(2L)
                .build();
        dpr.setId(1L);
        dpr.setStatus("ACTIVE");
        dpr.setIsDeleted(false);

        List<LibDocumentPermissionRel> permissions = new ArrayList<>();
        permissions.add(dpr);

        when(documentPermissionRelRepository.findByDocumentIdAndIsDeletedFalse(1L)).thenReturn(permissions);
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(mockPermission));

        List<?> response = permissionService.getDocumentPermissions(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void testGetAccessibleFolderIds() {
        LibFolderPermission fp = LibFolderPermission.builder()
                .folderId(1L)
                .permissionId(1L)
                .targetType("USER")
                .targetId(1L)
                .build();
        fp.setId(1L);
        fp.setStatus("ACTIVE");
        fp.setIsDeleted(false);

        List<LibFolderPermission> permissions = new ArrayList<>();
        permissions.add(fp);

        when(folderPermissionRepository.findByTarget("USER", 1L)).thenReturn(permissions);
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(mockPermission));

        List<Long> result = permissionService.getAccessibleFolderIds(1L, "READ");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0));
    }

    @Test
    void testGetAccessibleDocumentIds() {
        LibDocumentPermissionRel dpr = LibDocumentPermissionRel.builder()
                .documentId(1L)
                .permissionId(1L)
                .targetType("USER")
                .targetId(1L)
                .build();
        dpr.setId(1L);
        dpr.setStatus("ACTIVE");
        dpr.setIsDeleted(false);

        List<LibDocumentPermissionRel> permissions = new ArrayList<>();
        permissions.add(dpr);

        when(documentPermissionRelRepository.findByTarget("USER", 1L)).thenReturn(permissions);
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(mockPermission));

        List<Long> result = permissionService.getAccessibleDocumentIds(1L, "READ");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0));
    }
}

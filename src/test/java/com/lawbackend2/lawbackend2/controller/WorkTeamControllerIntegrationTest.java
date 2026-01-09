package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkTeamMemberPermissionRequest;
import com.lawbackend2.lawbackend2.entity.WorkTeam;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.entity.WorkTeamPermission;
import com.lawbackend2.lawbackend2.repository.WorkTeamMemberRepository;
import com.lawbackend2.lawbackend2.repository.WorkTeamPermissionRepository;
import com.lawbackend2.lawbackend2.repository.WorkTeamRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WorkTeamControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkTeamRepository workTeamRepository;

    @Autowired
    private WorkTeamMemberRepository workTeamMemberRepository;

    @Autowired
    private WorkTeamPermissionRepository workTeamPermissionRepository;

    private WorkTeam testTeam;
    private WorkTeamMember testMember;
    private WorkTeamPermission testPermission;

    @BeforeEach
    void setUp() {
        testTeam = new WorkTeam();
        testTeam.setTeamName("测试团队");
        testTeam.setTeamLeaderId(1L);
        testTeam.setCaseId(1L);
        testTeam.setTeamDescription("测试团队描述");
        testTeam.setStatus("ACTIVE");
        testTeam = workTeamRepository.save(testTeam);

        testMember = new WorkTeamMember();
        testMember.setTeamId(testTeam.getId());
        testMember.setCaseId(1L);
        testMember.setUserId(2L);
        testMember.setTeamRole("成员");
        testMember.setPermissionLevel("VIEW");
        testMember.setIsActive(1);
        testMember.setStatus("ACTIVE");
        testMember = workTeamMemberRepository.save(testMember);

        testPermission = new WorkTeamPermission();
        testPermission.setTeamMemberId(testMember.getId());
        testPermission.setModuleType("fund");
        testPermission.setPermissionType("read");
        testPermission.setIsAllowed(1);
        testPermission.setStatus("ACTIVE");
        testPermission = workTeamPermissionRepository.save(testPermission);
    }

    @AfterEach
    void tearDown() {
        workTeamPermissionRepository.deleteAll();
        workTeamMemberRepository.deleteAll();
        workTeamRepository.deleteAll();
    }

    @Test
    void testCreateWorkTeam_Success() throws Exception {
        WorkTeamCreateRequest request = new WorkTeamCreateRequest();
        request.setTeamName("新测试团队");
        request.setTeamLeaderId(3L);
        request.setCaseId(2L);
        request.setTeamDescription("新测试团队描述");

        mockMvc.perform(post("/work-team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.teamId").isNumber());
    }

    @Test
    void testGetWorkTeamList_Success() throws Exception {
        mockMvc.perform(get("/work-team/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testGetWorkTeamList_WithTeamName_Success() throws Exception {
        mockMvc.perform(get("/work-team/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("teamName", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testGetWorkTeamList_WithTeamLeaderId_Success() throws Exception {
        mockMvc.perform(get("/work-team/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("teamLeaderId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testGetWorkTeamDetail_Success() throws Exception {
        mockMvc.perform(get("/work-team/{teamId}", testTeam.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testTeam.getId()))
                .andExpect(jsonPath("$.data.teamName").value("测试团队"));
    }

    @Test
    void testAddWorkTeamMember_Success() throws Exception {
        WorkTeamMemberCreateRequest request = new WorkTeamMemberCreateRequest();
        request.setCaseId(1L);
        request.setUserId(3L);
        request.setTeamRole("新成员");
        request.setPermissionLevel("EDIT");

        mockMvc.perform(post("/work-team/{teamId}/member", testTeam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.memberId").isNumber());
    }

    @Test
    void testGetWorkTeamMembers_Success() throws Exception {
        mockMvc.perform(get("/work-team/{teamId}/members", testTeam.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testUpdateWorkTeamMemberPermission_Success() throws Exception {
        WorkTeamMemberPermissionRequest request = new WorkTeamMemberPermissionRequest();
        request.setPermissionLevel("ADMIN");

        mockMvc.perform(put("/work-team/work-team-member/{memberId}/permission", testMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetWorkTeamMemberPermissions_Success() throws Exception {
        mockMvc.perform(get("/work-team/work-team-member/{memberId}/permissions", testMember.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
}

package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.response.ApiResponse;
import com.lawbackend2.lawbackend2.entity.Todo;
import com.lawbackend2.lawbackend2.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    private Todo mockTodo;

    @BeforeEach
    void setUp() {
        mockTodo = Todo.builder()
                .id(1L)
                .userId(1L)
                .userAccount("testuser")
                .userName("测试用户")
                .title("测试待办")
                .description("这是一条测试待办")
                .type("WORK")
                .priority("NORMAL")
                .status("PENDING")
                .createTime(LocalDateTime.now())
                .build();

        when(todoService.createTodo(any(Todo.class))).thenReturn(mockTodo);
        when(todoService.getTodoById(1L)).thenReturn(mockTodo);
        when(todoService.completeTodo(1L)).thenReturn(mockTodo);
    }

    @Test
    void testCreateTodo_Success() throws Exception {
        mockMvc.perform(post("/api/v1/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockTodo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("测试待办"));
    }

    @Test
    void testGetTodoById_Success() throws Exception {
        mockMvc.perform(get("/api/v1/todo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testCompleteTodo_Success() throws Exception {
        mockMvc.perform(put("/api/v1/todo/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}

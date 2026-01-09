package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.Todo;
import com.lawbackend2.lawbackend2.repository.TodoRepository;
import com.lawbackend2.lawbackend2.service.TodoService;
import com.lawbackend2.lawbackend2.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WebSocketService webSocketService;

    @InjectMocks
    private TodoServiceImpl todoService;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_ACCOUNT = "testuser";
    private static final String TEST_USER_NAME = "测试用户";
    private static final Long TEST_TODO_ID = 1L;

    @BeforeEach
    void setUp() {
        Todo mockTodo = Todo.builder()
                .id(TEST_TODO_ID)
                .userId(TEST_USER_ID)
                .userAccount(TEST_USER_ACCOUNT)
                .userName(TEST_USER_NAME)
                .title("测试待办")
                .description("这是一条测试待办")
                .type("WORK")
                .priority("NORMAL")
                .status("PENDING")
                .createTime(LocalDateTime.now())
                .build();

        when(todoRepository.save(any(Todo.class))).thenReturn(mockTodo);
        when(todoRepository.findById(eq(TEST_TODO_ID)))
                .thenReturn(java.util.Optional.of(mockTodo));
        when(todoRepository.findByUserId(eq(TEST_USER_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockTodo), PageRequest.of(0, 10), 1));
        when(todoRepository.findByUserIdAndStatus(eq(TEST_USER_ID), eq("PENDING"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockTodo), PageRequest.of(0, 10), 1));
        when(todoRepository.findByUserIdAndStatusOrderByDeadlineAsc(eq(TEST_USER_ID), eq("PENDING")))
                .thenReturn(List.of(mockTodo));
        when(todoRepository.countPendingByUserId(eq(TEST_USER_ID))).thenReturn(1L);
        when(todoRepository.countCompletedByUserId(eq(TEST_USER_ID))).thenReturn(0L);
        when(todoRepository.countByUserIdAndDeadlineBefore(eq(TEST_USER_ID), any(LocalDateTime.class)))
                .thenReturn(0L);
    }

    @Test
    void testCreateTodo_Success() {
        Todo todo = Todo.builder()
                .userId(TEST_USER_ID)
                .userAccount(TEST_USER_ACCOUNT)
                .userName(TEST_USER_NAME)
                .title("测试待办")
                .description("这是一条测试待办")
                .type("WORK")
                .build();

        Todo createdTodo = todoService.createTodo(todo);

        assertNotNull(createdTodo);
        assertEquals(TEST_USER_ID, createdTodo.getUserId());
        assertEquals("测试待办", createdTodo.getTitle());
        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(webSocketService, times(1)).sendTodoUpdate(eq(TEST_USER_ID), any());
    }

    @Test
    void testCreateTodo_WithAllParameters() {
        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        
        Todo mockTodo = Todo.builder()
                .id(TEST_TODO_ID)
                .userId(TEST_USER_ID)
                .userAccount(TEST_USER_ACCOUNT)
                .userName(TEST_USER_NAME)
                .title("测试标题")
                .description("测试描述")
                .type("WORK")
                .priority("HIGH")
                .status("PENDING")
                .deadline(deadline)
                .createTime(LocalDateTime.now())
                .build();
        
        when(todoRepository.save(any(Todo.class))).thenReturn(mockTodo);
        
        Todo todo = todoService.createTodo(
                TEST_USER_ID, TEST_USER_ACCOUNT, TEST_USER_NAME,
                "测试标题", "测试描述", "WORK",
                "HIGH", deadline,
                100L, "CASE", 2L, "被分配人",
                3L, "创建人"
        );

        assertNotNull(todo);
        assertEquals(TEST_USER_ID, todo.getUserId());
        assertEquals("测试标题", todo.getTitle());
        assertEquals("HIGH", todo.getPriority());
        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(webSocketService, never()).sendTodoUpdate(any(), any());
    }

    @Test
    void testGetTodoById_Success() {
        Todo todo = todoService.getTodoById(TEST_TODO_ID);

        assertNotNull(todo);
        assertEquals(TEST_TODO_ID, todo.getId());
        verify(todoRepository, times(1)).findById(TEST_TODO_ID);
    }

    @Test
    void testGetTodoById_NotFound() {
        when(todoRepository.findById(eq(999L)))
                .thenReturn(java.util.Optional.empty());

        assertThrows(Exception.class, () -> todoService.getTodoById(999L));
    }

    @Test
    void testGetUserTodos_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Todo> page = todoService.getUserTodos(TEST_USER_ID, pageable);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(todoRepository, times(1)).findByUserId(TEST_USER_ID, pageable);
    }

    @Test
    void testGetUserTodosByStatus_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Todo> page = todoService.getUserTodosByStatus(TEST_USER_ID, "PENDING", pageable);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(todoRepository, times(1)).findByUserIdAndStatus(TEST_USER_ID, "PENDING", pageable);
    }

    @Test
    void testGetPendingTodos_Success() {
        List<Todo> todos = todoService.getPendingTodos(TEST_USER_ID);

        assertNotNull(todos);
        verify(todoRepository, times(1)).findByUserIdAndStatusOrderByDeadlineAsc(TEST_USER_ID, "PENDING");
    }

    @Test
    void testGetOverdueTodos_Success() {
        List<Todo> todos = todoService.getOverdueTodos(TEST_USER_ID);

        assertNotNull(todos);
        verify(todoRepository, times(1)).findByUserIdAndDeadlineBefore(eq(TEST_USER_ID), any(LocalDateTime.class));
    }

    @Test
    void testCountPendingTodos_Success() {
        Long count = todoService.countPendingTodos(TEST_USER_ID);

        assertEquals(1L, count);
        verify(todoRepository, times(1)).countPendingByUserId(TEST_USER_ID);
    }

    @Test
    void testCountCompletedTodos_Success() {
        Long count = todoService.countCompletedTodos(TEST_USER_ID);

        assertEquals(0L, count);
        verify(todoRepository, times(1)).countCompletedByUserId(TEST_USER_ID);
    }

    @Test
    void testCountOverdueTodos_Success() {
        Long count = todoService.countOverdueTodos(TEST_USER_ID);

        assertEquals(0L, count);
        verify(todoRepository, times(1)).countByUserIdAndDeadlineBefore(eq(TEST_USER_ID), any(LocalDateTime.class));
    }

    @Test
    void testCompleteTodo_Success() {
        Todo todo = todoService.completeTodo(TEST_TODO_ID);

        assertNotNull(todo);
        assertEquals("COMPLETED", todo.getStatus());
        assertNotNull(todo.getCompletedTime());
        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(webSocketService, times(1)).sendTodoUpdate(eq(TEST_USER_ID), any());
    }

    @Test
    void testCompleteTodo_AlreadyCompleted() {
        Todo completedTodo = Todo.builder()
                .id(TEST_TODO_ID)
                .userId(TEST_USER_ID)
                .title("测试待办")
                .status("COMPLETED")
                .build();

        when(todoRepository.findById(eq(TEST_TODO_ID)))
                .thenReturn(java.util.Optional.of(completedTodo));

        assertThrows(Exception.class, () -> todoService.completeTodo(TEST_TODO_ID));
    }

    @Test
    void testUpdateTodo_Success() {
        Todo updateTodo = Todo.builder()
                .title("更新标题")
                .description("更新描述")
                .priority("HIGH")
                .build();

        Todo updatedTodo = todoService.updateTodo(TEST_TODO_ID, updateTodo);

        assertNotNull(updatedTodo);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void testDeleteTodo_Success() {
        todoService.deleteTodo(TEST_TODO_ID);

        verify(todoRepository, times(1)).delete(any(Todo.class));
    }

    @Test
    void testBatchDeleteTodos_Success() {
        todoService.batchDeleteTodos(List.of(TEST_TODO_ID));

        verify(todoRepository, times(1)).deleteAll(anyList());
    }

    @Test
    void testUpdateTodoStatus_Success() {
        todoService.updateTodoStatus(TEST_TODO_ID, "IN_PROGRESS");

        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void testAssignTodo_Success() {
        todoService.assignTodo(TEST_TODO_ID, 2L, "被分配人");

        verify(todoRepository, times(1)).save(any(Todo.class));
    }
}

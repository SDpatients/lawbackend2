package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.TodoCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.TodoUpdateRequest;
import com.lawbackend2.lawbackend2.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoService {

    Todo createTodo(Todo todo);

    Todo createTodo(Long userId, String userAccount, String userName,
                     String title, String description, String type,
                     String priority, LocalDateTime deadline,
                     Long relatedId, String relatedType,
                     Long assigneeId, String assigneeName,
                     Long createUserId, String createUserName);

    Todo createTodoWithCaseAssociation(TodoCreateRequest request);

    Todo updateTodoWithCaseAssociation(Long todoId, TodoUpdateRequest request);

    Todo getTodoById(Long todoId);

    Page<Todo> getUserTodos(Long userId, Pageable pageable);

    Page<Todo> getUserTodos(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    Page<Todo> getUserTodosByStatus(Long userId, String status, Pageable pageable);

    Page<Todo> getUserTodosByType(Long userId, String type, Pageable pageable);

    Page<Todo> getUserTodosByPriority(Long userId, String priority, Pageable pageable);

    Page<Todo> searchTodos(Long userId, String type, String status, String priority, Pageable pageable);

    List<Todo> getPendingTodos(Long userId);

    List<Todo> getCompletedTodos(Long userId);

    List<Todo> getOverdueTodos(Long userId);

    Long countPendingTodos(Long userId);

    Long countCompletedTodos(Long userId);

    Long countOverdueTodos(Long userId);

    Todo completeTodo(Long todoId);

    Todo updateTodo(Long todoId, Todo todo);

    void deleteTodo(Long todoId);

    void batchDeleteTodos(List<Long> todoIds);

    Todo updateTodoStatus(Long todoId, String status);

    Todo assignTodo(Long todoId, Long assigneeId, String assigneeName);

    com.lawbackend2.lawbackend2.dto.MyTodoStatisticsResponse getMyTodoStatistics(Long userId);
}

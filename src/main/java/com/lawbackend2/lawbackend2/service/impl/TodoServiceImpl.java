package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.WebSocketMessage;
import com.lawbackend2.lawbackend2.entity.Todo;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.TodoRepository;
import com.lawbackend2.lawbackend2.service.TodoService;
import com.lawbackend2.lawbackend2.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final WebSocketService webSocketService;

    @Autowired
    public TodoServiceImpl(TodoRepository todoRepository,
                            WebSocketService webSocketService) {
        this.todoRepository = todoRepository;
        this.webSocketService = webSocketService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Todo createTodo(Todo todo) {
        log.info("创建待办事项, 用户ID: {}, 标题: {}", todo.getUserId(), todo.getTitle());
        Todo savedTodo = todoRepository.save(todo);
        
        WebSocketMessage message = WebSocketMessage.builder()
                .type("NEW_TODO")
                .userId(todo.getUserId())
                .title(todo.getTitle())
                .content(todo.getDescription())
                .data(savedTodo)
                .timestamp(System.currentTimeMillis())
                .build();
        webSocketService.sendTodoUpdate(todo.getUserId(), message);
        
        return savedTodo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Todo createTodo(Long userId, String userAccount, String userName,
                           String title, String description, String type,
                           String priority, LocalDateTime deadline,
                           Long relatedId, String relatedType,
                           Long assigneeId, String assigneeName,
                           Long createUserId, String createUserName) {
        Todo todo = Todo.builder()
                .userId(userId)
                .userAccount(userAccount)
                .userName(userName)
                .title(title)
                .description(description)
                .type(type)
                .priority(priority != null ? priority : "NORMAL")
                .status("PENDING")
                .deadline(deadline)
                .relatedId(relatedId)
                .relatedType(relatedType)
                .assigneeId(assigneeId)
                .assigneeName(assigneeName)
                .createUserId(createUserId)
                .createUserName(createUserName)
                .build();
        return todoRepository.save(todo);
    }

    @Override
    public Todo getTodoById(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new BusinessException("待办事项不存在"));
    }

    @Override
    public Page<Todo> getUserTodos(Long userId, Pageable pageable) {
        return todoRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<Todo> getUserTodos(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return todoRepository.findByUserIdAndDeadlineBetween(userId, startTime, endTime, pageable);
    }

    @Override
    public Page<Todo> getUserTodosByStatus(Long userId, String status, Pageable pageable) {
        return todoRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    @Override
    public Page<Todo> getUserTodosByType(Long userId, String type, Pageable pageable) {
        return todoRepository.findByUserIdAndType(userId, type, pageable);
    }

    @Override
    public Page<Todo> getUserTodosByPriority(Long userId, String priority, Pageable pageable) {
        return todoRepository.findByUserIdAndPriority(userId, priority, pageable);
    }

    @Override
    public Page<Todo> searchTodos(Long userId, String type, String status, String priority, Pageable pageable) {
        return todoRepository.searchTodos(userId, type, status, priority, pageable);
    }

    @Override
    public List<Todo> getPendingTodos(Long userId) {
        return todoRepository.findByUserIdAndStatusOrderByDeadlineAsc(userId, "PENDING");
    }

    @Override
    public List<Todo> getCompletedTodos(Long userId) {
        return todoRepository.findByUserIdAndStatusOrderByCompletedTimeDesc(userId, "COMPLETED");
    }

    @Override
    public List<Todo> getOverdueTodos(Long userId) {
        return todoRepository.findByUserIdAndDeadlineBefore(userId, LocalDateTime.now());
    }

    @Override
    public Long countPendingTodos(Long userId) {
        return todoRepository.countPendingByUserId(userId);
    }

    @Override
    public Long countCompletedTodos(Long userId) {
        return todoRepository.countCompletedByUserId(userId);
    }

    @Override
    public Long countOverdueTodos(Long userId) {
        return todoRepository.countByUserIdAndDeadlineBefore(userId, LocalDateTime.now());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Todo completeTodo(Long todoId) {
        Todo todo = getTodoById(todoId);
        if ("COMPLETED".equals(todo.getStatus())) {
            throw new BusinessException("待办事项已完成");
        }
        todo.setStatus("COMPLETED");
        todo.setCompletedTime(LocalDateTime.now());
        Todo savedTodo = todoRepository.save(todo);
        
        WebSocketMessage message = WebSocketMessage.builder()
                .type("TODO_COMPLETED")
                .userId(todo.getUserId())
                .title(todo.getTitle())
                .content("待办事项已完成")
                .data(savedTodo)
                .timestamp(System.currentTimeMillis())
                .build();
        webSocketService.sendTodoUpdate(todo.getUserId(), message);
        
        return savedTodo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Todo updateTodo(Long todoId, Todo todo) {
        Todo existingTodo = getTodoById(todoId);
        existingTodo.setTitle(todo.getTitle());
        existingTodo.setDescription(todo.getDescription());
        existingTodo.setType(todo.getType());
        existingTodo.setPriority(todo.getPriority());
        existingTodo.setDeadline(todo.getDeadline());
        existingTodo.setRemark(todo.getRemark());
        return todoRepository.save(existingTodo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTodo(Long todoId) {
        Todo todo = getTodoById(todoId);
        todoRepository.delete(todo);
        log.info("删除待办事项, ID: {}", todoId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteTodos(List<Long> todoIds) {
        List<Todo> todos = todoRepository.findAllById(todoIds);
        todoRepository.deleteAll(todos);
        log.info("批量删除待办事项, 共 {} 条", todos.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Todo updateTodoStatus(Long todoId, String status) {
        Todo todo = getTodoById(todoId);
        todo.setStatus(status);
        if ("COMPLETED".equals(status)) {
            todo.setCompletedTime(LocalDateTime.now());
        }
        return todoRepository.save(todo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Todo assignTodo(Long todoId, Long assigneeId, String assigneeName) {
        Todo todo = getTodoById(todoId);
        todo.setAssigneeId(assigneeId);
        todo.setAssigneeName(assigneeName);
        return todoRepository.save(todo);
    }
}

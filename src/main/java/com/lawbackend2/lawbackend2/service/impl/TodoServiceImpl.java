package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.WebSocketMessage;
import com.lawbackend2.lawbackend2.dto.request.TodoCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.TodoUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.Todo;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
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
import java.util.Optional;

@Slf4j
@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final WebSocketService webSocketService;
    private final BankruptCaseRepository bankruptCaseRepository;

    @Autowired
    public TodoServiceImpl(TodoRepository todoRepository,
                            WebSocketService webSocketService,
                            BankruptCaseRepository bankruptCaseRepository) {
        this.todoRepository = todoRepository;
        this.webSocketService = webSocketService;
        this.bankruptCaseRepository = bankruptCaseRepository;
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
    @Transactional(rollbackFor = Exception.class)
    public Todo createTodoWithCaseAssociation(TodoCreateRequest request) {
        log.info("创建待办事项(支持案件关联), 用户ID: {}, 标题: {}", request.getUserId(), request.getTitle());
        
        Long relatedId = request.getRelatedId();
        String relatedType = request.getRelatedType();
        
        if (relatedId == null && relatedType == null) {
            if (request.getCaseId() != null) {
                relatedId = request.getCaseId();
                relatedType = "CASE";
                log.info("通过案件ID自动关联: caseId={}", request.getCaseId());
            } else if (request.getCaseNumber() != null && !request.getCaseNumber().isEmpty()) {
                Optional<BankruptCase> caseOpt = bankruptCaseRepository.findByCaseNumber(request.getCaseNumber());
                if (caseOpt.isPresent()) {
                    relatedId = caseOpt.get().getId();
                    relatedType = "CASE";
                    log.info("通过案号精确匹配关联: caseNumber={}, caseId={}", request.getCaseNumber(), relatedId);
                } else {
                    Page<BankruptCase> fuzzyMatches = bankruptCaseRepository.searchByCaseNumber(
                        request.getCaseNumber(),
                        org.springframework.data.domain.PageRequest.of(0, 1, org.springframework.data.domain.Sort.by("createTime"))
                    );
                    if (!fuzzyMatches.getContent().isEmpty()) {
                        relatedId = fuzzyMatches.getContent().get(0).getId();
                        relatedType = "CASE";
                        log.info("通过案号模糊匹配关联: caseNumber={}, caseId={}", request.getCaseNumber(), relatedId);
                    } else {
                        log.warn("案号不存在: caseNumber={}", request.getCaseNumber());
                        throw new BusinessException("案号不存在");
                    }
                }
            } else {
                log.info("未提供案件关联信息，创建常规待办事项");
            }
        }
        
        Todo todo = Todo.builder()
                .userId(request.getUserId())
                .userAccount(request.getUserAccount())
                .userName(request.getUserName())
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .priority(request.getPriority() != null ? request.getPriority() : "NORMAL")
                .status("PENDING")
                .deadline(request.getDeadline())
                .relatedId(relatedId)
                .relatedType(relatedType)
                .assigneeId(request.getAssigneeId())
                .assigneeName(request.getAssigneeName())
                .createUserId(request.getCreateUserId())
                .createUserName(request.getCreateUserName())
                .remark(request.getRemark())
                .build();
        
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
    public Todo updateTodoWithCaseAssociation(Long todoId, TodoUpdateRequest request) {
        log.info("更新待办事项(支持案件关联), todoId: {}", todoId);
        
        Todo existingTodo = getTodoById(todoId);
        
        existingTodo.setTitle(request.getTitle() != null ? request.getTitle() : existingTodo.getTitle());
        existingTodo.setDescription(request.getDescription() != null ? request.getDescription() : existingTodo.getDescription());
        existingTodo.setType(request.getType() != null ? request.getType() : existingTodo.getType());
        existingTodo.setPriority(request.getPriority() != null ? request.getPriority() : existingTodo.getPriority());
        existingTodo.setDeadline(request.getDeadline() != null ? request.getDeadline() : existingTodo.getDeadline());
        existingTodo.setRemark(request.getRemark() != null ? request.getRemark() : existingTodo.getRemark());
        
        if (request.getCaseId() != null || (request.getCaseNumber() != null && !request.getCaseNumber().isEmpty())) {
            Long relatedId = null;
            String relatedType = "CASE";
            
            if (request.getCaseId() != null) {
                relatedId = request.getCaseId();
                log.info("通过案件ID更新关联: caseId={}", request.getCaseId());
            } else if (request.getCaseNumber() != null && !request.getCaseNumber().isEmpty()) {
                Optional<BankruptCase> caseOpt = bankruptCaseRepository.findByCaseNumber(request.getCaseNumber());
                if (caseOpt.isPresent()) {
                    relatedId = caseOpt.get().getId();
                    log.info("通过案号精确匹配更新关联: caseNumber={}, caseId={}", request.getCaseNumber(), relatedId);
                } else {
                    Page<BankruptCase> fuzzyMatches = bankruptCaseRepository.searchByCaseNumber(
                        request.getCaseNumber(),
                        org.springframework.data.domain.PageRequest.of(0, 1, org.springframework.data.domain.Sort.by("createTime"))
                    );
                    if (!fuzzyMatches.getContent().isEmpty()) {
                        relatedId = fuzzyMatches.getContent().get(0).getId();
                        log.info("通过案号模糊匹配更新关联: caseNumber={}, caseId={}", request.getCaseNumber(), relatedId);
                    } else {
                        log.warn("案号不存在: caseNumber={}", request.getCaseNumber());
                        throw new BusinessException("案号不存在");
                    }
                }
            }
            
            existingTodo.setRelatedId(relatedId);
            existingTodo.setRelatedType(relatedType);
        } else if (request.getRelatedId() != null && request.getRelatedType() != null) {
            existingTodo.setRelatedId(request.getRelatedId());
            existingTodo.setRelatedType(request.getRelatedType());
        }
        
        return todoRepository.save(existingTodo);
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
        // 如果开始时间和结束时间都为null，则查询该用户的所有待办事项
        if (startTime == null && endTime == null) {
            return todoRepository.findByUserId(userId, pageable);
        }
        // 如果只有开始时间为null，则查询创建时间在结束时间之前的记录
        if (startTime == null) {
            return todoRepository.findByUserIdAndCreateTimeBetween(userId, LocalDateTime.MIN, endTime, pageable);
        }
        // 如果只有结束时间为null，则查询创建时间在开始时间之后的记录
        if (endTime == null) {
            return todoRepository.findByUserIdAndCreateTimeBetween(userId, startTime, LocalDateTime.MAX, pageable);
        }
        // 两个时间都有值，查询创建时间在时间范围内的记录
        return todoRepository.findByUserIdAndCreateTimeBetween(userId, startTime, endTime, pageable);
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
        return todoRepository.findByUserIdAndStatusAndDeadlineBefore(userId, "PENDING", LocalDateTime.now());
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
        return todoRepository.countOverdueByUserId(userId);
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
        if (todoIds == null || todoIds.isEmpty()) {
            log.warn("批量删除待办事项, 传入的ID列表为空");
            return;
        }
        List<Todo> todos = todoRepository.findAllById(todoIds);
        if (todos.isEmpty()) {
            log.warn("批量删除待办事项, 未找到匹配的待办记录, IDs: {}", todoIds);
            return;
        }
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
        } else {
            todo.setCompletedTime(null);
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

    @Override
    public com.lawbackend2.lawbackend2.dto.MyTodoStatisticsResponse getMyTodoStatistics(Long userId) {
        log.info("查询当前用户的待办统计数据, userId: {}", userId);

        com.lawbackend2.lawbackend2.dto.MyTodoStatisticsResponse response = 
            new com.lawbackend2.lawbackend2.dto.MyTodoStatisticsResponse();

        Long overdueCount = todoRepository.countOverdueByUserId(userId);
        response.setOverdueTodos(overdueCount);

        Long completedCount = todoRepository.countCompletedByUserId(userId);
        response.setCompletedTodos(completedCount);

        Long totalCount = todoRepository.countByUserId(userId);
        Long inProgressCount = totalCount - completedCount - overdueCount;
        response.setInProgressTodos(Math.max(inProgressCount, 0L));

        log.info("当前用户的待办统计数据: 进行中={}, 已完成={}, 已逾期={}", 
            response.getInProgressTodos(), response.getCompletedTodos(), response.getOverdueTodos());
        
        return response;
    }
}

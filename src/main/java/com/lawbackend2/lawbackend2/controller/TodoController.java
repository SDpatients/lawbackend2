package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.response.ApiResponse;
import com.lawbackend2.lawbackend2.entity.Todo;
import com.lawbackend2.lawbackend2.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/todo")
@Tag(name = "待办管理", description = "待办事项管理相关接口")
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    @Operation(summary = "创建待办事项", description = "创建新的待办事项")
    public ResponseEntity<ApiResponse<Todo>> createTodo(@RequestBody Todo todo) {
        log.info("创建待办事项请求, 用户ID: {}, 标题: {}", todo.getUserId(), todo.getTitle());
        Todo createdTodo = todoService.createTodo(todo);
        return ResponseEntity.ok(ApiResponse.success(createdTodo));
    }

    @GetMapping("/{todoId}")
    @Operation(summary = "获取待办事项详情", description = "根据待办事项ID获取详情")
    public ResponseEntity<ApiResponse<Todo>> getTodoById(
            @Parameter(description = "待办事项ID") @PathVariable Long todoId) {
        Todo todo = todoService.getTodoById(todoId);
        return ResponseEntity.ok(ApiResponse.success(todo));
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户待办列表", description = "分页获取用户的待办事项列表")
    public ResponseEntity<ApiResponse<Page<Todo>>> getUserTodos(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Todo> todos = todoService.getUserTodos(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(todos));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索待办事项", description = "根据条件搜索用户待办事项")
    public ResponseEntity<ApiResponse<Page<Todo>>> searchTodos(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "待办类型") @RequestParam(required = false) String type,
            @Parameter(description = "待办状态") @RequestParam(required = false) String status,
            @Parameter(description = "优先级") @RequestParam(required = false) String priority,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Todo> todos = todoService.searchTodos(userId, type, status, priority, pageable);
        return ResponseEntity.ok(ApiResponse.success(todos));
    }

    @GetMapping("/pending")
    @Operation(summary = "获取待处理待办事项", description = "获取用户的所有待处理待办事项")
    public ResponseEntity<ApiResponse<List<Todo>>> getPendingTodos(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        List<Todo> todos = todoService.getPendingTodos(userId);
        return ResponseEntity.ok(ApiResponse.success(todos));
    }

    @GetMapping("/COMPLETED")
    @Operation(summary = "获取已完成待办事项", description = "获取用户的所有已完成待办事项")
    public ResponseEntity<ApiResponse<List<Todo>>> getCompletedTodos(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        List<Todo> todos = todoService.getCompletedTodos(userId);
        return ResponseEntity.ok(ApiResponse.success(todos));
    }

    @GetMapping("/overdue")
    @Operation(summary = "获取过期待办事项", description = "获取用户的所有过期待办事项")
    public ResponseEntity<ApiResponse<List<Todo>>> getOverdueTodos(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        List<Todo> todos = todoService.getOverdueTodos(userId);
        return ResponseEntity.ok(ApiResponse.success(todos));
    }

    @GetMapping("/count/pending")
    @Operation(summary = "获取待处理待办数量", description = "统计用户的待处理待办事项数量")
    public ResponseEntity<ApiResponse<Long>> countPendingTodos(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        Long count = todoService.countPendingTodos(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/completed")
    @Operation(summary = "获取已完成待办数量", description = "统计用户的已完成待办事项数量")
    public ResponseEntity<ApiResponse<Long>> countCompletedTodos(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        Long count = todoService.countCompletedTodos(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/overdue")
    @Operation(summary = "获取过期待办数量", description = "统计用户的过期待办事项数量")
    public ResponseEntity<ApiResponse<Long>> countOverdueTodos(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        Long count = todoService.countOverdueTodos(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PutMapping("/{todoId}/complete")
    @Operation(summary = "完成待办事项", description = "将待办事项标记为已完成")
    public ResponseEntity<ApiResponse<Todo>> completeTodo(
            @Parameter(description = "待办事项ID") @PathVariable Long todoId) {
        Todo todo = todoService.completeTodo(todoId);
        return ResponseEntity.ok(ApiResponse.success(todo));
    }

    @PutMapping("/{todoId}")
    @Operation(summary = "更新待办事项", description = "更新待办事项信息")
    public ResponseEntity<ApiResponse<Todo>> updateTodo(
            @Parameter(description = "待办事项ID") @PathVariable Long todoId,
            @RequestBody Todo todo) {
        Todo updatedTodo = todoService.updateTodo(todoId, todo);
        return ResponseEntity.ok(ApiResponse.success(updatedTodo));
    }

    @PutMapping("/{todoId}/status")
    @Operation(summary = "更新待办状态", description = "更新待办事项的状态")
    public ResponseEntity<ApiResponse<Todo>> updateTodoStatus(
            @Parameter(description = "待办事项ID") @PathVariable Long todoId,
            @Parameter(description = "待办状态") @RequestParam String status) {
        Todo todo = todoService.updateTodoStatus(todoId, status);
        return ResponseEntity.ok(ApiResponse.success(todo));
    }

    @PutMapping("/{todoId}/assign")
    @Operation(summary = "分配待办事项", description = "将待办事项分配给指定用户")
    public ResponseEntity<ApiResponse<Todo>> assignTodo(
            @Parameter(description = "待办事项ID") @PathVariable Long todoId,
            @Parameter(description = "被分配人ID") @RequestParam Long assigneeId,
            @Parameter(description = "被分配人姓名") @RequestParam String assigneeName) {
        Todo todo = todoService.assignTodo(todoId, assigneeId, assigneeName);
        return ResponseEntity.ok(ApiResponse.success(todo));
    }

    @DeleteMapping("/{todoId}")
    @Operation(summary = "删除待办事项", description = "删除指定的待办事项")
    public ResponseEntity<ApiResponse<Void>> deleteTodo(
            @Parameter(description = "待办事项ID") @PathVariable Long todoId) {
        todoService.deleteTodo(todoId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除待办事项", description = "批量删除多个待办事项")
    public ResponseEntity<ApiResponse<Void>> batchDeleteTodos(
            @Parameter(description = "待办事项ID列表") @RequestBody List<Long> todoIds) {
        todoService.batchDeleteTodos(todoIds);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

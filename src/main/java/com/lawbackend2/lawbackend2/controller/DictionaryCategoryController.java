package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.entity.DictionaryCategory;
import com.lawbackend2.lawbackend2.entity.DictionaryItem;
import com.lawbackend2.lawbackend2.service.DictionaryCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dictionary/category")
@Tag(name = "字典分类管理", description = "字典分类及字典项的CRUD管理")
public class DictionaryCategoryController {

    @Autowired
    private DictionaryCategoryService dictionaryCategoryService;

    @GetMapping("/list")
    @Operation(summary = "获取字典分类列表(分页)", description = "支持按关键词搜索和状态筛选")
    public Result<PageResult<DictionaryCategory>> getCategoryList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键词(编码/名称模糊搜索)") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态(ACTIVE/INACTIVE)") @RequestParam(required = false) String status) {
        PageResult<DictionaryCategory> result = dictionaryCategoryService.getCategoryList(pageNum, pageSize, keyword, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取字典分类详情")
    public Result<DictionaryCategory> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        DictionaryCategory category = dictionaryCategoryService.getCategoryById(id);
        return Result.success(category);
    }

    @GetMapping("/code/{categoryCode}")
    @Operation(summary = "根据编码获取字典分类")
    public Result<DictionaryCategory> getCategoryByCode(
            @Parameter(description = "分类编码") @PathVariable String categoryCode) {
        DictionaryCategory category = dictionaryCategoryService.getCategoryByCode(categoryCode);
        return Result.success(category);
    }

    @PostMapping
    @Operation(summary = "创建字典分类")
    @AuditLog(module = "dictionary-category", moduleName = "字典分类管理", operationType = "CREATE", operationName = "创建字典分类")
    public Result<DictionaryCategory> createCategory(@RequestBody DictionaryCategory category) {
        DictionaryCategory created = dictionaryCategoryService.createCategory(category);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新字典分类")
    @AuditLog(module = "dictionary-category", moduleName = "字典分类管理", operationType = "UPDATE", operationName = "更新字典分类")
    public Result<DictionaryCategory> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @RequestBody DictionaryCategory category) {
        DictionaryCategory updated = dictionaryCategoryService.updateCategory(id, category);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除字典分类(软删除，同时删除其下所有字典项)")
    @AuditLog(module = "dictionary-category", moduleName = "字典分类管理", operationType = "DELETE", operationName = "删除字典分类")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        dictionaryCategoryService.deleteCategory(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新字典分类状态")
    public Result<Void> updateCategoryStatus(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "状态(ACTIVE/INACTIVE)") @RequestParam String status) {
        dictionaryCategoryService.updateCategoryStatus(id, status);
        return Result.success();
    }

    @GetMapping("/{categoryId}/items")
    @Operation(summary = "获取指定分类下的所有字典项")
    public Result<List<DictionaryItem>> getItemsByCategoryId(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        List<DictionaryItem> items = dictionaryCategoryService.getItemsByCategoryId(categoryId);
        return Result.success(items);
    }

    @GetMapping("/{categoryId}/items/list")
    @Operation(summary = "获取指定分类下的字典项(分页)")
    public Result<PageResult<DictionaryItem>> getItemList(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "状态(ACTIVE/INACTIVE)") @RequestParam(required = false) String status) {
        PageResult<DictionaryItem> result = dictionaryCategoryService.getItemList(categoryId, pageNum, pageSize, status);
        return Result.success(result);
    }

    @PostMapping("/{categoryId}/items")
    @Operation(summary = "创建字典项")
    @AuditLog(module = "dictionary-item", moduleName = "字典项管理", operationType = "CREATE", operationName = "创建字典项")
    public Result<DictionaryItem> createItem(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @RequestBody DictionaryItem item) {
        item.setCategoryId(categoryId);
        DictionaryItem created = dictionaryCategoryService.createItem(item);
        return Result.success(created);
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "更新字典项")
    @AuditLog(module = "dictionary-item", moduleName = "字典项管理", operationType = "UPDATE", operationName = "更新字典项")
    public Result<DictionaryItem> updateItem(
            @Parameter(description = "字典项ID") @PathVariable Long id,
            @RequestBody DictionaryItem item) {
        DictionaryItem updated = dictionaryCategoryService.updateItem(id, item);
        return Result.success(updated);
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "删除字典项(软删除)")
    @AuditLog(module = "dictionary-item", moduleName = "字典项管理", operationType = "DELETE", operationName = "删除字典项")
    public Result<Void> deleteItem(
            @Parameter(description = "字典项ID") @PathVariable Long id) {
        dictionaryCategoryService.deleteItem(id);
        return Result.success();
    }

    @PutMapping("/items/{id}/status")
    @Operation(summary = "更新字典项状态")
    public Result<Void> updateItemStatus(
            @Parameter(description = "字典项ID") @PathVariable Long id,
            @Parameter(description = "状态(ACTIVE/INACTIVE)") @RequestParam String status) {
        dictionaryCategoryService.updateItemStatus(id, status);
        return Result.success();
    }
}

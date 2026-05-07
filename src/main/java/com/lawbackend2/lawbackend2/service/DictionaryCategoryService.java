package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.entity.DictionaryCategory;
import com.lawbackend2.lawbackend2.entity.DictionaryItem;

import java.util.List;

public interface DictionaryCategoryService {

    PageResult<DictionaryCategory> getCategoryList(Integer pageNum, Integer pageSize, String keyword, String status);

    DictionaryCategory getCategoryById(Long id);

    DictionaryCategory getCategoryByCode(String categoryCode);

    DictionaryCategory createCategory(DictionaryCategory category);

    DictionaryCategory updateCategory(Long id, DictionaryCategory category);

    void deleteCategory(Long id);

    void updateCategoryStatus(Long id, String status);

    List<DictionaryItem> getItemsByCategoryId(Long categoryId);

    PageResult<DictionaryItem> getItemList(Long categoryId, Integer pageNum, Integer pageSize, String status);

    DictionaryItem createItem(DictionaryItem item);

    DictionaryItem updateItem(Long id, DictionaryItem item);

    void deleteItem(Long id);

    void updateItemStatus(Long id, String status);
}

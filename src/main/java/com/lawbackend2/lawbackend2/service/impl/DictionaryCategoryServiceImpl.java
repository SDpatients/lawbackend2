package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.entity.DictionaryCategory;
import com.lawbackend2.lawbackend2.entity.DictionaryItem;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.DictionaryCategoryRepository;
import com.lawbackend2.lawbackend2.repository.DictionaryItemRepository;
import com.lawbackend2.lawbackend2.service.DictionaryCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DictionaryCategoryServiceImpl implements DictionaryCategoryService {

    @Autowired
    private DictionaryCategoryRepository dictionaryCategoryRepository;

    @Autowired
    private DictionaryItemRepository dictionaryItemRepository;

    @Override
    public PageResult<DictionaryCategory> getCategoryList(Integer pageNum, Integer pageSize, String keyword, String status) {
        List<DictionaryCategory> all;
        if (keyword != null && !keyword.isEmpty()) {
            all = dictionaryCategoryRepository.searchByKeyword(keyword);
        } else if (status != null && !status.isEmpty()) {
            all = dictionaryCategoryRepository.findByStatus(status);
        } else {
            all = dictionaryCategoryRepository.findAllActive();
        }

        Long total = (long) all.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, all.size());
        List<DictionaryCategory> pageList = fromIndex < all.size() ? all.subList(fromIndex, toIndex) : List.of();

        return PageResult.of(total, pageList, pageNum, pageSize);
    }

    @Override
    public DictionaryCategory getCategoryById(Long id) {
        return dictionaryCategoryRepository.findById(id)
                .filter(c -> !c.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "字典分类不存在"));
    }

    @Override
    public DictionaryCategory getCategoryByCode(String categoryCode) {
        return dictionaryCategoryRepository.findByCategoryCode(categoryCode)
                .filter(c -> !c.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "字典分类不存在: " + categoryCode));
    }

    @Override
    @Transactional
    public DictionaryCategory createCategory(DictionaryCategory category) {
        if (dictionaryCategoryRepository.existsByCategoryCode(category.getCategoryCode())) {
            throw new BusinessException(400, "字典分类编码已存在: " + category.getCategoryCode());
        }
        DictionaryCategory saved = dictionaryCategoryRepository.save(category);
        log.info("创建字典分类成功 - 编码: {}, 名称: {}", saved.getCategoryCode(), saved.getCategoryName());
        return saved;
    }

    @Override
    @Transactional
    public DictionaryCategory updateCategory(Long id, DictionaryCategory category) {
        DictionaryCategory existing = getCategoryById(id);
        if (!existing.getCategoryCode().equals(category.getCategoryCode())
                && dictionaryCategoryRepository.existsByCategoryCode(category.getCategoryCode())) {
            throw new BusinessException(400, "字典分类编码已存在: " + category.getCategoryCode());
        }
        existing.setCategoryCode(category.getCategoryCode());
        existing.setCategoryName(category.getCategoryName());
        existing.setDescription(category.getDescription());
        existing.setSortOrder(category.getSortOrder());
        DictionaryCategory updated = dictionaryCategoryRepository.save(existing);
        log.info("更新字典分类成功 - ID: {}, 编码: {}", id, updated.getCategoryCode());
        return updated;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        DictionaryCategory category = getCategoryById(id);
        category.setIsDeleted(true);
        category.setStatus("DELETED");
        dictionaryCategoryRepository.save(category);

        List<DictionaryItem> items = dictionaryItemRepository.findByCategoryId(id);
        for (DictionaryItem item : items) {
            item.setIsDeleted(true);
            item.setStatus("DELETED");
            dictionaryItemRepository.save(item);
        }
        log.info("删除字典分类成功 - ID: {}, 编码: {}, 同时删除了{}个字典项", id, category.getCategoryCode(), items.size());
    }

    @Override
    @Transactional
    public void updateCategoryStatus(Long id, String status) {
        DictionaryCategory category = getCategoryById(id);
        category.setStatus(status);
        dictionaryCategoryRepository.save(category);
        log.info("更新字典分类状态 - ID: {}, 新状态: {}", id, status);
    }

    @Override
    public List<DictionaryItem> getItemsByCategoryId(Long categoryId) {
        return dictionaryItemRepository.findByCategoryId(categoryId);
    }

    @Override
    public PageResult<DictionaryItem> getItemList(Long categoryId, Integer pageNum, Integer pageSize, String status) {
        List<DictionaryItem> all;
        if (status != null && !status.isEmpty()) {
            all = dictionaryItemRepository.findByCategoryIdAndStatus(categoryId, status);
        } else {
            all = dictionaryItemRepository.findByCategoryId(categoryId);
        }

        Long total = (long) all.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, all.size());
        List<DictionaryItem> pageList = fromIndex < all.size() ? all.subList(fromIndex, toIndex) : List.of();

        return PageResult.of(total, pageList, pageNum, pageSize);
    }

    @Override
    @Transactional
    public DictionaryItem createItem(DictionaryItem item) {
        getCategoryById(item.getCategoryId());
        DictionaryItem saved = dictionaryItemRepository.save(item);
        log.info("创建字典项成功 - 分类ID: {}, 编码: {}, 名称: {}", saved.getCategoryId(), saved.getItemCode(), saved.getItemName());
        return saved;
    }

    @Override
    @Transactional
    public DictionaryItem updateItem(Long id, DictionaryItem item) {
        DictionaryItem existing = dictionaryItemRepository.findById(id)
                .filter(i -> !i.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "字典项不存在"));
        existing.setItemCode(item.getItemCode());
        existing.setItemName(item.getItemName());
        existing.setItemValue(item.getItemValue());
        existing.setDescription(item.getDescription());
        existing.setSortOrder(item.getSortOrder());
        DictionaryItem updated = dictionaryItemRepository.save(existing);
        log.info("更新字典项成功 - ID: {}, 编码: {}", id, updated.getItemCode());
        return updated;
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        DictionaryItem item = dictionaryItemRepository.findById(id)
                .filter(i -> !i.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "字典项不存在"));
        item.setIsDeleted(true);
        item.setStatus("DELETED");
        dictionaryItemRepository.save(item);
        log.info("删除字典项成功 - ID: {}, 编码: {}", id, item.getItemCode());
    }

    @Override
    @Transactional
    public void updateItemStatus(Long id, String status) {
        DictionaryItem item = dictionaryItemRepository.findById(id)
                .filter(i -> !i.getIsDeleted())
                .orElseThrow(() -> new BusinessException(404, "字典项不存在"));
        item.setStatus(status);
        dictionaryItemRepository.save(item);
        log.info("更新字典项状态 - ID: {}, 新状态: {}", id, status);
    }
}

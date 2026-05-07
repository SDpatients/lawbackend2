package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.CaseNodeTemplate;

import java.util.List;

public interface CaseNodeTemplateService {

    void initDefaultTemplates();

    List<CaseNodeTemplate> getTemplatesByCaseType(String caseType);

    List<CaseNodeTemplate> getAllActiveTemplates();

    CaseNodeTemplate getTemplateById(Long id);

    CaseNodeTemplate createTemplate(CaseNodeTemplate template);

    CaseNodeTemplate updateTemplate(Long id, CaseNodeTemplate template);

    void deleteTemplate(Long id);
}

package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.BankruptCase;

import java.time.LocalDate;
import java.util.List;

public interface CaseSearchService {
    List<BankruptCase> searchByKeyword(Integer page, Integer size, String keyword);

    List<BankruptCase> searchByKeywordAndStatus(Integer page, Integer size, String keyword, String caseStatus);

    List<BankruptCase> searchByKeywordAndProgress(Integer page, Integer size, String keyword, String caseProgress);

    List<BankruptCase> searchByKeywordAndStatusAndProgress(Integer page, Integer size, String keyword, String caseStatus, String caseProgress);

    List<BankruptCase> searchByCaseNumber(Integer page, Integer size, String caseNumber);

    List<BankruptCase> searchByCaseName(Integer page, Integer size, String caseName);

    List<BankruptCase> searchByAcceptanceCourt(Integer page, Integer size, String acceptanceCourt);

    List<BankruptCase> searchByDesignatedInstitution(Integer page, Integer size, String designatedInstitution);

    List<BankruptCase> searchByMainResponsiblePerson(Integer page, Integer size, String mainResponsiblePerson);

    List<BankruptCase> searchByAcceptanceDateRange(Integer page, Integer size, LocalDate startDate, LocalDate endDate);

    List<BankruptCase> searchByCaseSource(Integer page, Integer size, String caseSource);

    List<BankruptCase> searchByCaseReason(Integer page, Integer size, String caseReason);

    List<BankruptCase> searchByDesignatedJudge(Integer page, Integer size, String designatedJudge);
}

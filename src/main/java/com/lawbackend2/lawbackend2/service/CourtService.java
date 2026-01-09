package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CourtCreateRequest;
import com.lawbackend2.lawbackend2.dto.CourtUpdateRequest;
import com.lawbackend2.lawbackend2.entity.Court;

import java.util.List;

public interface CourtService {

    Court createCourt(CourtCreateRequest request, Long userId);

    Court getCourtById(Long courtId);

    List<Court> getCourtList(Integer pageNum, Integer pageSize, String courtLevel, String shortName, String fullName);

    Long getCourtCount(String courtLevel, String shortName, String fullName);

    Court updateCourt(Long courtId, CourtUpdateRequest request);

    void deleteCourt(Long courtId);
}

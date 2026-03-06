package com.lawbackend2.lawbackend2.dto.response;

import com.lawbackend2.lawbackend2.entity.FileRecord;
import lombok.Data;

import java.util.List;

@Data
public class CaseAnnouncementWithFilesResponse {

    private Long announcementId;

    private String title;

    private String announcementType;

    private String status;

    private List<FileRecord> files;
}

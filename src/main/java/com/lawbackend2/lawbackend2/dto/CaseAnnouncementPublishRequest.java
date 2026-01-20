package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CaseAnnouncementPublishRequest {

    private java.time.LocalDateTime topExpireTime;
}

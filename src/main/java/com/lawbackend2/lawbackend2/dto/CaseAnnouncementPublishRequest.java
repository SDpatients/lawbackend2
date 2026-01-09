package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CaseAnnouncementPublishRequest {

    @NotNull(message = "置顶过期时间不能为空")
    private java.time.LocalDateTime topExpireTime;
}

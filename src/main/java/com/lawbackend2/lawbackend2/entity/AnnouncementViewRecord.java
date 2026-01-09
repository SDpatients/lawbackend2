package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_announcement_view_record")
public class AnnouncementViewRecord extends BaseEntity {
    @Column(name = "announcement_id")
    private Long announcementId;

    @Column(name = "announcement_title", length = 255)
    private String announcementTitle;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_name", length = 255)
    private String caseName;

    @Column(name = "viewer_id")
    private Long viewerId;

    @Column(name = "viewer_name", length = 100)
    private String viewerName;

    @Column(name = "viewer_type", length = 50)
    private String viewerType;

    @Column(name = "view_time")
    private LocalDateTime viewTime;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "view_duration", columnDefinition = "INT DEFAULT 0")
    private Integer viewDuration;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "browser_type", length = 50)
    private String browserType;

    @Column(name = "os_type", length = 50)
    private String osType;

    @Column(name = "location", length = 255)
    private String location;
}

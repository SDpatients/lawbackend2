package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_case_announcement")
public class CaseAnnouncement extends BaseEntity {

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "announcement_type", length = 20)
    private String announcementType;

    @Column(name = "status", length = 20)
    private String status = "DRAFT";

    @Column(name = "publisher_id")
    private Long publisherId;

    @Column(name = "publisher_name", length = 100)
    private String publisherName;

    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "is_top")
    private Boolean isTop = false;

    @Column(name = "top_expire_time")
    private LocalDateTime topExpireTime;

    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;
}

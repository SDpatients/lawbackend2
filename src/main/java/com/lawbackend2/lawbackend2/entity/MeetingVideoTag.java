package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_meeting_video_tag")
public class MeetingVideoTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "video_title", nullable = false)
    private String videoTitle;

    @Column(name = "status", columnDefinition = "varchar(20) default 'pending'")
    private String status;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "create_time", updatable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime createTime;

    @Column(name = "update_time", columnDefinition = "datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateTime;

    @Column(name = "create_user_id")
    private Long createUserId;

    @Column(name = "update_user_id")
    private Long updateUserId;

    @Column(name = "is_deleted", columnDefinition = "tinyint(1) default 0")
    private Boolean isDeleted;
}

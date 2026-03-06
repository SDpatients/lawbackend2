package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_meeting_vote_item")
public class MeetingVoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "agree_count", columnDefinition = "int default 0")
    private Integer agreeCount;

    @Column(name = "oppose_count", columnDefinition = "int default 0")
    private Integer opposeCount;

    @Column(name = "abstain_count", columnDefinition = "int default 0")
    private Integer abstainCount;

    @Column(name = "remark")
    private String remark;

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

    @Column(name = "status", columnDefinition = "varchar(20) default 'ACTIVE'")
    private String status;
}

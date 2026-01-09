package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_court")
public class Court extends BaseEntity {

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "short_name", unique = true, length = 100)
    private String shortName;

    @Column(name = "court_level", length = 50)
    private String courtLevel;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "responsible_user_id")
    private Long responsibleUserId;

    @Column(name = "undertaking_judge", length = 100)
    private String undertakingJudge;
}

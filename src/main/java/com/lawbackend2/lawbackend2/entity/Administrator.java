package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_administrator")
public class Administrator extends BaseEntity {

    @Column(name = "administrator_name", length = 200, nullable = false)
    private String administratorName;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "administrator_type", length = 50)
    private String administratorType;

    @Column(name = "responsible_person_id")
    private Long responsiblePersonId;

    @Column(name = "responsible_person", length = 200)
    private String responsiblePerson;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "office_address", length = 500)
    private String officeAddress;
}

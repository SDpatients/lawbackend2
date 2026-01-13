package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_administrator_staff")
public class AdministratorStaff extends BaseEntity {

    @Column(name = "administrator_id")
    private Long administratorId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "staff_type", length = 50)
    private String staffType;

    @Column(name = "id_number", length = 50)
    private String idNumber;

    @Column(name = "lawyer_license_number", length = 50)
    private String lawyerLicenseNumber;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "responsibility", length = 255)
    private String responsibility;

    @Column(name = "appointment_date")
    private java.time.LocalDate appointmentDate;

    @Column(name = "user_id")
    private Long userId;
}

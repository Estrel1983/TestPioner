package com.example.testTask.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "PHONE_DATA", uniqueConstraints = @UniqueConstraint(columnNames = "PHONE"))
public class PhoneData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
    private Users user;

    @Pattern(regexp = "\\d{11,13}")
    @Column(name = "PHONE", length = 13, nullable = false)
    private String phone;
}

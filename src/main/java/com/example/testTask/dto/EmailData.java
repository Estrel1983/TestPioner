package com.example.testTask.dto;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "EMAIL_DATA", uniqueConstraints = @UniqueConstraint(columnNames = "EMAIL"))
public class EmailData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
    private Users user;

    @Column(name = "EMAIL", length = 200)
    private String email;
}

package com.example.testTask.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ACCOUNT")
@org.hibernate.annotations.Check(constraints = "BALANCE >= 0")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
    private Users user;

    @Column(name = "BALANCE")
    private BigDecimal balance;
}

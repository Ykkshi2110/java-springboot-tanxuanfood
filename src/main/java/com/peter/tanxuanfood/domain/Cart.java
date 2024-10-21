package com.peter.tanxuanfood.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.peter.tanxuanfood.convert.util.SecurityUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long sum;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"cart"})
    private Set<CartDetail> cartDetails;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;


    @PrePersist
    private void handleBeforeCreate() {
        this.createdBy = SecurityUtil
                .getCurrentUserLogin()
                .orElse("");
        this.createdAt = Instant.now();
    }

    @PreUpdate
    private void handleBeforeUpdate() {
        this.createdBy = SecurityUtil
                .getCurrentUserLogin()
                .orElse("");
        this.updatedAt = Instant.now();
    }
}
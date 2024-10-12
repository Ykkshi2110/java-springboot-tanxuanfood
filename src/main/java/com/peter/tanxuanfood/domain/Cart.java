package com.peter.tanxuanfood.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    private long sum;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private Set<CartDetail> cartDetails = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    private void handleBeforeCreate(){
        this.createdAt = Instant.now();
    }

    @PreUpdate
    private void handleBeforeUpdate(){
        this.updatedAt = Instant.now();
    }
}
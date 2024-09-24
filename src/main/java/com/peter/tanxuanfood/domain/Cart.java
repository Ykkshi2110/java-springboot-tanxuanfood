package com.peter.tanxuanfood.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.awt.image.ImageProducer;
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

    @OneToMany(mappedBy = "cart")
    private Set<CartDetail> cartDetails;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
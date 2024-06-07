package ru.xplago.catservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "cat_cat")
public class Cat extends BaseEntity {
    @ManyToOne(targetEntity = Owner.class)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToOne(targetEntity = Owner.class)
    @JoinColumn(name = "tenant_id")
    private Owner tenant;

    private String name;

    private Timestamp birthday;

    private String avatarId;
}
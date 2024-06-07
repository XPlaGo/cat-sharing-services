package ru.xplago.catservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "cat_owner")
public class Owner extends BaseEntity {
    @Column(unique = true, nullable = false)
    private Long userId;

    @OneToMany(targetEntity = Cat.class, mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Cat> cats;

    @OneToMany(targetEntity = Cat.class, mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<Cat> rentedCats;
}

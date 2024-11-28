package com.drawit.drawit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Item")
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String target;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private Integer cost;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Purchase> purchases;


}

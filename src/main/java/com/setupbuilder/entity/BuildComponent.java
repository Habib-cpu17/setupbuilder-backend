package com.setupbuilder.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "build_components",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_build_component",
                columnNames = {"build_id", "component_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildComponent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "build_id", nullable = false)
    private Build build;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    /** Snapshot of the price when the component was added — for historical accuracy. */
    @Column(name = "price_at_time_of_build", precision = 10, scale = 2)
    private BigDecimal priceAtTimeOfBuild;
}
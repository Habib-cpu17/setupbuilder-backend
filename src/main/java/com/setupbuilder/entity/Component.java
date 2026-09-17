package com.setupbuilder.entity;

import com.setupbuilder.entity.enums.ComponentCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "components", indexes = {
        @Index(name = "idx_component_category", columnList = "category"),
        @Index(name = "idx_component_brand", columnList = "brand")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Component extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ComponentCategory category;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String model;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * Flexible spec map: e.g. {"socket": "AM5", "cores": "8", "tdp": "105"}
     * Used later for compatibility warnings and filtering.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "component_specs",
            joinColumns = @JoinColumn(name = "component_id")
    )
    @MapKeyColumn(name = "spec_key", length = 100)
    @Column(name = "spec_value", length = 500)
    @Builder.Default
    private Map<String, String> specs = new HashMap<>();

    @Column(name = "fallback_price", precision = 10, scale = 2)
    private BigDecimal fallbackPrice;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
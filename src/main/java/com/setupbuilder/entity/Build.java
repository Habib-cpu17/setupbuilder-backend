package com.setupbuilder.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "builds", indexes = {
        @Index(name = "idx_build_public", columnList = "is_public"),
        @Index(name = "idx_build_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Build extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean curated = false;

    /** Lower = shows first on the landing page. Null when not curated. */
    @Column(name = "curated_rank")
    private Integer curatedRank;

    /** Cached total — recalculated by the service layer whenever components change. */
    @Column(name = "total_price", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "build", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BuildComponent> components = new ArrayList<>();

    @OneToMany(mappedBy = "build", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}
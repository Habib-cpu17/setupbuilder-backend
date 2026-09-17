package com.setupbuilder.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_comment_build", columnList = "build_id"),
        @Index(name = "idx_comment_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    @Column(nullable = false, length = 2000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "build_id", nullable = false)
    private Build build;
}
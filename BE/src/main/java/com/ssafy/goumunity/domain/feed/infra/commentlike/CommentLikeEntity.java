package com.ssafy.goumunity.domain.feed.infra.commentlike;

import com.ssafy.goumunity.domain.feed.domain.CommentLike;
import com.ssafy.goumunity.domain.feed.infra.comment.CommentEntity;
import com.ssafy.goumunity.domain.user.infra.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_like")
public class CommentLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_like_id")
    private Long commentLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private CommentEntity commentEntity;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant createdAt;

    @Column(
            name = "updated_at",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Instant updatedAt;

    public CommentLike to() {
        return CommentLike.builder()
                .commentLikeId(commentLikeId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static CommentLikeEntity from(CommentLike commentLike) {
        CommentLikeEntityBuilder commentLikeEntityBuilder =
                CommentLikeEntity.builder().commentLikeId(commentLike.getCommentLikeId());

        if (commentLike.getCreatedAt() != null)
            commentLikeEntityBuilder.createdAt(commentLike.getCreatedAt());
        if (commentLike.getUpdatedAt() != null)
            commentLikeEntityBuilder.updatedAt(commentLike.getUpdatedAt());

        return commentLikeEntityBuilder.build();
    }
}
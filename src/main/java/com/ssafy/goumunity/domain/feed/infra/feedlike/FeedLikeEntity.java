package com.ssafy.goumunity.domain.feed.infra.feedlike;

import com.ssafy.goumunity.domain.feed.domain.FeedLike;
import com.ssafy.goumunity.domain.feed.infra.feed.FeedEntity;
import com.ssafy.goumunity.domain.user.infra.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feed_like")
public class FeedLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private FeedEntity feedEntity;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public FeedLike to() {
        return FeedLike.builder()
                .id(id)
                .userId(userEntity.getId())
                .feedId(feedEntity.getId())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static FeedLikeEntity from(FeedLike feedLike) {
        return FeedLikeEntity.builder()
                .id(feedLike.getId())
                .userEntity(UserEntity.userEntityOnlyWithId(feedLike.getUserId()))
                .feedEntity(FeedEntity.feedEntityOnlyWithId(feedLike.getFeedId()))
                .createdAt(feedLike.getCreatedAt())
                .updatedAt(feedLike.getUpdatedAt())
                .build();
    }
}

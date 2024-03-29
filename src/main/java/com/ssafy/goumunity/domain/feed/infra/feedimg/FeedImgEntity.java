package com.ssafy.goumunity.domain.feed.infra.feedimg;

import com.ssafy.goumunity.domain.feed.domain.FeedImg;
import com.ssafy.goumunity.domain.feed.infra.feed.FeedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feed_img")
public class FeedImgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_img_id")
    private Long id;

    @Column(name = "img_src")
    private String imgSrc;

    @NotNull
    @Column(name = "sequence")
    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private FeedEntity feedEntity;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public FeedImg to() {
        return FeedImg.builder()
                .id(id)
                .imgSrc(imgSrc)
                .sequence(sequence)
                .feedId(feedEntity.getId())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static FeedImgEntity from(FeedImg feedImg) {

        return FeedImgEntity.builder()
                .id(feedImg.getId())
                .feedEntity(FeedEntity.feedEntityOnlyWithId(feedImg.getFeedId()))
                .imgSrc(feedImg.getImgSrc())
                .sequence(feedImg.getSequence())
                .createdAt(feedImg.getCreatedAt())
                .updatedAt(feedImg.getUpdatedAt())
                .build();
    }
}

package com.ssafy.goumunity.domain.feed.controller.response;

import com.querydsl.core.annotations.QueryProjection;
import com.ssafy.goumunity.domain.feed.infra.comment.CommentEntity;
import com.ssafy.goumunity.domain.user.controller.response.UserResponse;
import java.time.Instant;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class CommentResponse {
    private Long id;
    private String content;
    private Long feedId;
    private UserResponse user;
    private Instant createdAt;
    private Instant updatedAt;

    private Long replyCount;
    private Long likeCount;
    private Boolean iLikeThat;

    @QueryProjection
    public CommentResponse(
            CommentEntity comment, Long replyCount, Long likeCount, Boolean iLikeThat) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.feedId = comment.getFeedEntity().getId();
        this.user = UserResponse.from(comment.getUserEntity().toModel());
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.replyCount = replyCount;
        this.likeCount = likeCount;
        this.iLikeThat = iLikeThat;
    }
}

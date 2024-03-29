package com.ssafy.goumunity.domain.feed.infra.feedscrap;

import com.ssafy.goumunity.domain.feed.domain.FeedScrap;
import com.ssafy.goumunity.domain.feed.service.post.FeedScrapRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeedScrapRepositoryImpl implements FeedScrapRepository {

    private final FeedScrapJpaRepository feedScrapJpaRepository;

    @Override
    public void create(FeedScrap feedScrap) {
        feedScrapJpaRepository.save(FeedScrapEntity.from(feedScrap));
    }

    @Override
    public void delete(Long feedScrapId) {
        feedScrapJpaRepository.deleteById(feedScrapId);
    }

    @Override
    public Optional<FeedScrap> findOneByUserIdAndFeedId(FeedScrap feedScrap) {
        return feedScrapJpaRepository
                .findByUserEntityIdAndFeedEntityId(feedScrap.getUserId(), feedScrap.getFeedId())
                .map(FeedScrapEntity::to);
    }

    @Override
    public boolean existByUserIdAndFeedId(Long userId, Long feedId) {
        return feedScrapJpaRepository.existsByUserEntityIdAndFeedEntityId(userId, feedId);
    }
}

package com.ssafy.goumunity.domain.chat.infra.hashtag;

import com.ssafy.goumunity.domain.chat.domain.Hashtag;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "hashtag")
@Entity
public class HashtagEntity {

    @Id
    @Column(name = "hashtag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 10)
    private String name;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public static HashtagEntity from(Hashtag hashtag) {
        return HashtagEntity.builder()
                .id(hashtag.getId())
                .name(hashtag.getName())
                .createdAt(hashtag.getCreatedAt())
                .updatedAt(hashtag.getUpdatedAt())
                .build();
    }

    public Hashtag to() {
        return Hashtag.builder()
                .id(this.getId())
                .name(this.getName())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    public static HashtagEntity hashtagEntityOnlyWithId(Long id) {
        return HashtagEntity.builder().id(id).build();
    }
}

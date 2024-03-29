package com.ssafy.goumunity.domain.chat.controller.response;

import com.ssafy.goumunity.domain.chat.infra.chatroom.ChatRoomEntity;
import com.ssafy.goumunity.domain.region.infra.RegionEntity;
import java.util.List;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomDetailResponse {

    private Long chatRoomId;
    private String title;
    private String imgSrc;
    private Integer capability;
    private Integer currentUserCount;

    private List<ChatRoomHashtagResponse> hashtags;

    private ChatRoomUserResponse host;
    private Boolean isHost;
    private List<ChatRoomUserResponse> members;
    private ChatRoomRegionResponse region;

    public ChatRoomDetailResponse(ChatRoomEntity chatRoom, Long currentUserId) {
        this.chatRoomId = chatRoom.getId();
        this.title = chatRoom.getTitle();
        this.imgSrc = chatRoom.getImgSrc();
        this.capability = chatRoom.getCapability();
        this.host =
                chatRoom.getHost() == null
                        ? null
                        : new ChatRoomUserResponse(chatRoom.getHost(), currentUserId);
        this.isHost = (host != null && host.getIsCurrentUser());
        this.members =
                chatRoom.getUserChatRooms().stream()
                        .map(ucr -> new ChatRoomUserResponse(ucr.getUser(), currentUserId))
                        .toList();
        this.currentUserCount = members.size();
        this.hashtags =
                chatRoom.getChatRoomHashtags().stream().map(ChatRoomHashtagResponse::from).toList();
        this.region = new ChatRoomRegionResponse(chatRoom.getRegion());
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ChatRoomRegionResponse {
        private Long regionId;
        private String si;
        private String gungu;

        public ChatRoomRegionResponse(RegionEntity region) {
            this.regionId = region.getRegionId();
            this.si = region.getSi();
            this.gungu = region.getGungu();
        }
    }
}

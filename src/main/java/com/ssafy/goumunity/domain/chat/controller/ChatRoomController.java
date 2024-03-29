package com.ssafy.goumunity.domain.chat.controller;

import com.ssafy.goumunity.common.util.SliceResponse;
import com.ssafy.goumunity.domain.chat.controller.request.ChatRoomRequest;
import com.ssafy.goumunity.domain.chat.controller.response.ChatRoomDetailResponse;
import com.ssafy.goumunity.domain.chat.controller.response.ChatRoomSearchResponse;
import com.ssafy.goumunity.domain.chat.controller.response.ChatRoomUserResponse;
import com.ssafy.goumunity.domain.chat.controller.response.MyChatRoomResponse;
import com.ssafy.goumunity.domain.chat.service.ChatRoomService;
import com.ssafy.goumunity.domain.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/chat-rooms")
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<Long> createChatRoom(
            @RequestPart("data") @Valid ChatRoomRequest.Create dto,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatRoomService.createChatRoom(dto, multipartFile, user));
    }

    @PostMapping("/{chatRoomId}")
    public ResponseEntity<Void> connectChatRoom(
            @PathVariable Long chatRoomId, @AuthenticationPrincipal User user) {
        chatRoomService.connectChatRoom(chatRoomId, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<Void> exitChatRoom(
            @PathVariable Long chatRoomId, @AuthenticationPrincipal User user) {
        chatRoomService.exitChatRoom(chatRoomId, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<SliceResponse<ChatRoomSearchResponse>> searchChatRoom(
            @RequestParam String keyword, @RequestParam Long time, Pageable pageable) {
        Slice<ChatRoomSearchResponse> response =
                chatRoomService.searchChatRoom(keyword, time, pageable);
        return ResponseEntity.ok(SliceResponse.from(response.getContent(), response.hasNext()));
    }

    @PatchMapping("/{chatRoomId}/disconnect")
    public ResponseEntity<Void> disconnect(
            @PathVariable Long chatRoomId, @AuthenticationPrincipal User user) {
        chatRoomService.disconnectChatRoom(chatRoomId, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatRoomId}/users")
    public ResponseEntity<SliceResponse<ChatRoomUserResponse>> findChatRoomUsers(
            @PathVariable Long chatRoomId,
            Pageable pageable,
            @RequestParam Long time,
            @AuthenticationPrincipal User user) {
        Slice<ChatRoomUserResponse> responses =
                chatRoomService.findChatRoomUsers(chatRoomId, pageable, time, user);
        return ResponseEntity.ok(SliceResponse.from(responses.getContent(), responses.hasNext()));
    }

    @PatchMapping("/{chatRoomId}")
    public ResponseEntity<Void> modifyChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal User user,
            @RequestPart("data") ChatRoomRequest.Modify dto,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile) {
        chatRoomService.modifyChatRoom(chatRoomId, user, dto, multipartFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<MyChatRoomResponse> findOneByChatRoomId(
            @PathVariable Long chatRoomId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatRoomService.findOneMyChatRoomByChatRoomId(chatRoomId, user));
    }

    @GetMapping("/{chatRoomId}/detail")
    public ResponseEntity<ChatRoomDetailResponse> findDetailByChatRoomId(
            @PathVariable Long chatRoomId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatRoomService.findDetailByChatRoomId(chatRoomId, user));
    }
}

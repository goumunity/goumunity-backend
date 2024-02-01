package com.ssafy.goumunity.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ssafy.goumunity.domain.chat.controller.response.Message;
import com.ssafy.goumunity.domain.chat.domain.Chat;
import com.ssafy.goumunity.domain.chat.exception.ChatErrorCode;
import com.ssafy.goumunity.domain.chat.exception.ChatException;
import com.ssafy.goumunity.domain.chat.infra.ChatType;
import com.ssafy.goumunity.domain.chat.service.port.ChatRepository;
import com.ssafy.goumunity.domain.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock ChatRepository chatRepository;

    @Mock ChatRoomService chatRoomService;

    @InjectMocks ChatServiceImpl chatService;

    @Test
    void 채팅_저장_테스트_성공() throws Exception {
        // given
        Long chatRoomId = 1L;
        Message.Request message =
                Message.Request.builder().content("hello").chatType(ChatType.MESSAGE).build();
        User user = User.builder().id(2L).build();

        Chat chat = Chat.create(message, chatRoomId, user);
        given(chatRoomService.verifySendChat(any(), any())).willReturn(true);
        // when
        chatService.saveChat(chatRoomId, message, user);
        // then
        verify(chatRepository).save(chat);
    }

    @Test
    void 채팅_저장_테스트_실패_채팅을_보낼_수_없는_경우() throws Exception {
        // given
        Long chatRoomId = 1L;
        Message.Request message =
                Message.Request.builder().content("hello").chatType(ChatType.MESSAGE).build();
        User user = User.builder().id(2L).build();

        given(chatRoomService.verifySendChat(any(), any())).willReturn(false);
        // when // then
        assertThatThrownBy(() -> chatService.saveChat(chatRoomId, message, user))
                .isInstanceOf(ChatException.class)
                .hasFieldOrPropertyWithValue("errorCode", ChatErrorCode.CANT_SEND_MESSAGE);
    }
}
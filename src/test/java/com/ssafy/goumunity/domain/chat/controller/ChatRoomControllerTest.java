package com.ssafy.goumunity.domain.chat.controller;

import static com.ssafy.goumunity.common.exception.GlobalErrorCode.BIND_ERROR;
import static com.ssafy.goumunity.common.exception.GlobalErrorCode.FORBIDDEN;
import static com.ssafy.goumunity.domain.chat.exception.ChatErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.goumunity.common.exception.CustomException;
import com.ssafy.goumunity.common.exception.GlobalErrorCode;
import com.ssafy.goumunity.common.exception.GlobalExceptionHandler;
import com.ssafy.goumunity.domain.chat.controller.request.ChatRoomRequest;
import com.ssafy.goumunity.domain.chat.controller.response.*;
import com.ssafy.goumunity.domain.chat.exception.ChatException;
import com.ssafy.goumunity.domain.chat.infra.chatroom.ChatRoomEntity;
import com.ssafy.goumunity.domain.chat.infra.chatroom.UserChatRoomEntity;
import com.ssafy.goumunity.domain.chat.infra.hashtag.ChatRoomHashtagEntity;
import com.ssafy.goumunity.domain.chat.infra.hashtag.HashtagEntity;
import com.ssafy.goumunity.domain.chat.service.ChatRoomService;
import com.ssafy.goumunity.domain.region.infra.RegionEntity;
import com.ssafy.goumunity.domain.user.infra.UserEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(controllers = {ChatRoomController.class, GlobalExceptionHandler.class})
class ChatRoomControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean ChatRoomService chatRoomService;

    @InjectMocks ChatRoomController controller;

    private static final String CHAT_ROOM_API_PREFIX = "/api/chat-rooms";

    private ObjectMapper mapper = new ObjectMapper();

    @WithMockUser
    @Test
    void 채팅방생성테스트() throws Exception {
        // given
        ChatRoomRequest.Create dto =
                ChatRoomRequest.Create.builder()
                        .title("거지방")
                        .capability(10)
                        .regionId(1L)
                        .hashtags(
                                List.of(
                                        ChatRoomRequest.HashtagRequest.builder().name("1L").build(),
                                        ChatRoomRequest.HashtagRequest.builder().name("2L").build(),
                                        ChatRoomRequest.HashtagRequest.builder().name("3L").build()))
                        .build();

        MockPart data =
                new MockPart("data", "", mapper.writeValueAsBytes(dto), MediaType.APPLICATION_JSON);
        MockMultipartFile image = new MockMultipartFile("image", new byte[0]);
        // when
        mockMvc
                .perform(multipart(CHAT_ROOM_API_PREFIX).file(image).part(data).with(csrf()))
                // then
                .andExpectAll(status().isCreated(), content().string("0"))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방생성테스트_실패_해시태그_사이즈_초과() throws Exception {
        // given
        ChatRoomRequest.Create dto =
                ChatRoomRequest.Create.builder()
                        .title("거지방")
                        .capability(10)
                        .regionId(1L)
                        .hashtags(
                                List.of(
                                        ChatRoomRequest.HashtagRequest.builder().name("1L").build(),
                                        ChatRoomRequest.HashtagRequest.builder().name("2L").build(),
                                        ChatRoomRequest.HashtagRequest.builder().name("3L").build(),
                                        ChatRoomRequest.HashtagRequest.builder().name("3L").build(),
                                        ChatRoomRequest.HashtagRequest.builder().name("3L").build(),
                                        ChatRoomRequest.HashtagRequest.builder().name("3L").build(),
                                        ChatRoomRequest.HashtagRequest.builder().name("3L").build()))
                        .build();

        MockPart data =
                new MockPart("data", "", mapper.writeValueAsBytes(dto), MediaType.APPLICATION_JSON);
        MockMultipartFile image = new MockMultipartFile("image", new byte[0]);
        // when
        mockMvc
                .perform(multipart(CHAT_ROOM_API_PREFIX).file(image).part(data).with(csrf()))
                // then
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방생성테스트_실패_이미지가_아닌_파일_업로드() throws Exception {
        // given
        ChatRoomRequest.Create dto =
                ChatRoomRequest.Create.builder()
                        .title("거지방")
                        .capability(10)
                        .regionId(1L)
                        .hashtags(
                                List.of(
                                        ChatRoomRequest.HashtagRequest.builder().name("1L").build(),
                                        ChatRoomRequest.HashtagRequest.builder().name("2L").build(),
                                        ChatRoomRequest.HashtagRequest.builder().name("3L").build()))
                        .build();

        MockPart data =
                new MockPart("data", "", mapper.writeValueAsBytes(dto), MediaType.APPLICATION_JSON);
        MockMultipartFile image = new MockMultipartFile("image", new byte[0]);

        doThrow(new CustomException(GlobalErrorCode.FILE_IS_NOT_IMAGE_TYPE))
                .when(chatRoomService)
                .createChatRoom(any(), any(), any());

        // when
        mockMvc
                .perform(multipart(CHAT_ROOM_API_PREFIX).file(image).part(data).with(csrf()))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorName").value(GlobalErrorCode.FILE_IS_NOT_IMAGE_TYPE.name()))
                .andExpect(
                        jsonPath("$.errorMessage")
                                .value(GlobalErrorCode.FILE_IS_NOT_IMAGE_TYPE.getErrorMessage()))
                .andExpect(jsonPath("$.path").value(CHAT_ROOM_API_PREFIX))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방생성테스트_실패_존재하지_않는_해시태그가_담긴_경우() throws Exception {
        // given
        ChatRoomRequest.Create dto =
                ChatRoomRequest.Create.builder()
                        .title("거지방")
                        .capability(10)
                        .regionId(1L)
                        .hashtags(
                                List.of(
                                        ChatRoomRequest.HashtagRequest.builder().name("1L").build(),
                                        ChatRoomRequest.HashtagRequest.builder().name("2L").build(),
                                        ChatRoomRequest.HashtagRequest.builder().name("3L").build()))
                        .build();

        MockPart data =
                new MockPart("data", "", mapper.writeValueAsBytes(dto), MediaType.APPLICATION_JSON);
        MockMultipartFile image = new MockMultipartFile("image", new byte[0]);

        doThrow(new ChatException(HASHTAG_NOT_FOUND))
                .when(chatRoomService)
                .createChatRoom(any(), any(), any());

        // when
        mockMvc
                .perform(multipart(CHAT_ROOM_API_PREFIX).file(image).part(data).with(csrf()))
                // then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorName").value(HASHTAG_NOT_FOUND.name()))
                .andExpect(jsonPath("$.errorMessage").value(HASHTAG_NOT_FOUND.getErrorMessage()))
                .andExpect(jsonPath("$.path").value(CHAT_ROOM_API_PREFIX))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방_나가기_테스트_성공() throws Exception {
        // given
        Long chatRoomId = 1L;
        // when // then
        String deleteChatRoomUrl = CHAT_ROOM_API_PREFIX + "/" + chatRoomId;
        mockMvc
                .perform(delete(deleteChatRoomUrl).with(csrf()))
                .andExpectAll(status().isOk())
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방_나가기_테스트_실패_채팅방이_존재하지_않는_경우() throws Exception {
        // given
        Long chatRoomId = 1L;
        // when // then
        String deleteChatRoomUrl = CHAT_ROOM_API_PREFIX + "/" + chatRoomId;

        doThrow(new ChatException(CHAT_ROOM_NOT_FOUND))
                .when(chatRoomService)
                .exitChatRoom(any(), any());
        mockMvc
                .perform(delete(deleteChatRoomUrl).with(csrf()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.errorName").value(CHAT_ROOM_NOT_FOUND.getErrorName()),
                        jsonPath("$.errorMessage").value(CHAT_ROOM_NOT_FOUND.getErrorMessage()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방_나가기_테스트_실패_회원이_채팅방에_속하지_않는_경우() throws Exception {
        // given
        Long chatRoomId = 1L;
        // when // then
        String deleteChatRoomUrl = CHAT_ROOM_API_PREFIX + "/" + chatRoomId;

        doThrow(new CustomException(FORBIDDEN)).when(chatRoomService).exitChatRoom(any(), any());

        mockMvc
                .perform(delete(deleteChatRoomUrl).with(csrf()))
                .andExpectAll(
                        status().isForbidden(),
                        jsonPath("$.errorName").value(FORBIDDEN.getErrorName()),
                        jsonPath("$.errorMessage").value(FORBIDDEN.getErrorMessage()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방_나가기_테스트_실패_방장이_나가려는_경우() throws Exception {
        // given
        Long chatRoomId = 1L;
        // when // then
        String deleteChatRoomUrl = CHAT_ROOM_API_PREFIX + "/" + chatRoomId;

        doThrow(new CustomException(HOST_CANT_OUT)).when(chatRoomService).exitChatRoom(any(), any());

        mockMvc
                .perform(delete(deleteChatRoomUrl).with(csrf()))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.errorName").value(HOST_CANT_OUT.getErrorName()),
                        jsonPath("$.errorMessage").value(HOST_CANT_OUT.getErrorMessage()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방_검색_테스트() throws Exception {
        // given
        String keyword = "거지방";
        Long time = 10000L;
        int page = 0;
        int size = 12;

        List<ChatRoomSearchResponse> contents = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            contents.add(
                    ChatRoomSearchResponse.builder()
                            .title("거거지방")
                            .chatRoomId((long) i)
                            .imgSrc("/img.jpg")
                            .capability(10)
                            .currentUserCount(5)
                            .hashtags(
                                    List.of(
                                            ChatRoomHashtagResponse.builder().name("20대").build(),
                                            ChatRoomHashtagResponse.builder().name("거지방").build(),
                                            ChatRoomHashtagResponse.builder().name("관악구").build()))
                            .build());
        }
        given(chatRoomService.searchChatRoom(keyword, time, PageRequest.of(page, size)))
                .willReturn(new SliceImpl<>(contents, PageRequest.of(page, size), false));
        // when
        mockMvc
                .perform(
                        get(CHAT_ROOM_API_PREFIX + "/search")
                                .queryParam("keyword", keyword)
                                .queryParam("time", Long.toString(time))
                                .queryParam("page", String.valueOf(page))
                                .queryParam("size", String.valueOf(size)))
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.hasNext").value(false),
                        jsonPath("$.contents.length()").value(2))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 거지방_종료_테스트_성공() throws Exception {
        // given
        Long chatRoomId = 1L;
        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId + "/disconnect";
        // when
        mockMvc
                .perform(patch(url).with(csrf()))
                // then
                .andExpectAll(status().isOk())
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 거지방_종료_테스트_실패_채팅방이_존재하지_않는_경우() throws Exception {
        // given
        Long chatRoomId = 1L;
        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId + "/disconnect";
        doThrow(new ChatException(CHAT_ROOM_NOT_FOUND))
                .when(chatRoomService)
                .disconnectChatRoom(any(), any());
        // when
        mockMvc
                .perform(patch(url).with(csrf()))
                // then
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.errorMessage").value(CHAT_ROOM_NOT_FOUND.getErrorMessage()),
                        jsonPath("$.errorName").value(CHAT_ROOM_NOT_FOUND.getErrorName()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 거지방_종료_테스트_실패_회원이_거지방에_참가하지_않은_경우() throws Exception {
        // given
        Long chatRoomId = 1L;
        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId + "/disconnect";
        doThrow(new CustomException(FORBIDDEN)).when(chatRoomService).disconnectChatRoom(any(), any());
        // when
        mockMvc
                .perform(patch(url).with(csrf()))
                // then
                .andExpectAll(
                        status().isForbidden(),
                        jsonPath("$.errorMessage").value(FORBIDDEN.getErrorMessage()),
                        jsonPath("$.errorName").value(FORBIDDEN.getErrorName()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 거지방_참가자_조회_테스트_성공() throws Exception {
        // given
        Long chatRoomId = 1L;
        Long time = 1000000000L;
        int page = 0;
        int size = 12;

        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId + "/users";

        given(chatRoomService.findChatRoomUsers(any(), any(), any(), any()))
                .willReturn(
                        new SliceImpl<>(
                                List.of(
                                        ChatRoomUserResponse.builder().build(),
                                        ChatRoomUserResponse.builder().build(),
                                        ChatRoomUserResponse.builder().build()),
                                PageRequest.of(page, size),
                                false));
        // when
        mockMvc
                .perform(
                        get(url)
                                .queryParam("page", String.valueOf(page))
                                .queryParam("size", String.valueOf(size))
                                .queryParam("time", String.valueOf(time))
                                .with(csrf()))
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.hasNext").value(false),
                        jsonPath("$.contents.length()").value(3))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 거지방_참가자_조회_테스트_실패_채팅방_접근_권한이_없는_경우() throws Exception {
        // given
        Long chatRoomId = 1L;
        Long time = 1000000000L;
        int page = 0;
        int size = 12;

        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId + "/users";

        given(chatRoomService.findChatRoomUsers(any(), any(), any(), any()))
                .willThrow(new CustomException(FORBIDDEN));
        // when
        mockMvc
                .perform(
                        get(url)
                                .queryParam("page", String.valueOf(page))
                                .queryParam("size", String.valueOf(size))
                                .queryParam("time", String.valueOf(time))
                                .with(csrf()))
                // then
                .andExpectAll(
                        status().isForbidden(),
                        jsonPath("$.errorName").value(FORBIDDEN.getErrorName()),
                        jsonPath("$.errorMessage").value(FORBIDDEN.getErrorMessage()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 거지방_수정_테스트_성공() throws Exception {
        // given

        Long chatRoomId = 1L;

        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId;
        ChatRoomRequest.Modify modify =
                ChatRoomRequest.Modify.builder().title("거지거지거지방").capability(20).build();

        MockPart data =
                new MockPart("data", "", mapper.writeValueAsBytes(modify), MediaType.APPLICATION_JSON);
        MockMultipartFile image = new MockMultipartFile("image", new byte[0]);

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(url);
        builder.with(
                new RequestPostProcessor() {
                    @Override
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        request.setMethod("PATCH");
                        return request;
                    }
                });
        // when
        mockMvc
                .perform(builder.part(data).file(image).with(csrf()))
                // then
                .andExpectAll(status().isOk())
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 거지방_수정_테스트_실패_채팅방이_존재하지_않는_경우() throws Exception {
        // given

        Long chatRoomId = 1L;

        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId;
        ChatRoomRequest.Modify modify =
                ChatRoomRequest.Modify.builder().title("거지거지거지방").capability(20).build();

        MockPart data =
                new MockPart("data", "", mapper.writeValueAsBytes(modify), MediaType.APPLICATION_JSON);
        MockMultipartFile image = new MockMultipartFile("image", new byte[0]);

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(url);
        builder.with(
                new RequestPostProcessor() {
                    @Override
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        request.setMethod("PATCH");
                        return request;
                    }
                });

        doThrow(new ChatException(CHAT_ROOM_NOT_FOUND))
                .when(chatRoomService)
                .modifyChatRoom(any(), any(), any(), any());
        // when
        mockMvc
                .perform(builder.part(data).file(image).with(csrf()))
                // then
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.errorMessage").value(CHAT_ROOM_NOT_FOUND.getErrorMessage()),
                        jsonPath("$.errorName").value(CHAT_ROOM_NOT_FOUND.getErrorName()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 거지방_수정_테스트_실패_회원이_왕초가_아닌_경우() throws Exception {
        // given

        Long chatRoomId = 1L;

        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId;
        ChatRoomRequest.Modify modify =
                ChatRoomRequest.Modify.builder().title("거지거지거지방").capability(20).build();

        MockPart data =
                new MockPart("data", "", mapper.writeValueAsBytes(modify), MediaType.APPLICATION_JSON);
        MockMultipartFile image = new MockMultipartFile("image", new byte[0]);

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(url);
        builder.with(
                new RequestPostProcessor() {
                    @Override
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        request.setMethod("PATCH");
                        return request;
                    }
                });

        doThrow(new CustomException(FORBIDDEN))
                .when(chatRoomService)
                .modifyChatRoom(any(), any(), any(), any());
        // when
        mockMvc
                .perform(builder.part(data).file(image).with(csrf()))
                // then
                .andExpectAll(
                        status().isForbidden(),
                        jsonPath("$.errorMessage").value(FORBIDDEN.getErrorMessage()),
                        jsonPath("$.errorName").value(FORBIDDEN.getErrorName()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 거지방_수정_테스트_실패_이미지소스와_멀티파트파일이_같이_올라온_경우() throws Exception {
        // given

        Long chatRoomId = 1L;

        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId;
        ChatRoomRequest.Modify modify =
                ChatRoomRequest.Modify.builder().title("거지거지거지방").capability(20).build();

        MockPart data =
                new MockPart("data", "", mapper.writeValueAsBytes(modify), MediaType.APPLICATION_JSON);
        MockMultipartFile image = new MockMultipartFile("image", new byte[0]);

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(url);
        builder.with(
                new RequestPostProcessor() {
                    @Override
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        request.setMethod("PATCH");
                        return request;
                    }
                });

        doThrow(new CustomException(BIND_ERROR))
                .when(chatRoomService)
                .modifyChatRoom(any(), any(), any(), any());
        // when
        mockMvc
                .perform(builder.part(data).file(image).with(csrf()))
                // then
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.errorMessage").value(BIND_ERROR.getErrorMessage()),
                        jsonPath("$.errorName").value(BIND_ERROR.getErrorName()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방_목록_단건_조회_테스트_성공() throws Exception {
        // given
        Long chatRoomId = 1L;
        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId;

        given(chatRoomService.findOneMyChatRoomByChatRoomId(any(), any()))
                .willReturn(
                        MyChatRoomResponse.builder()
                                .title("거지")
                                .chatRoomId(chatRoomId)
                                .imgSrc("")
                                .currentUserCount(10)
                                .unReadMessageCount(100L)
                                .hashtags(new ArrayList<>())
                                .build());
        // when // then
        mockMvc
                .perform(get(url).with(csrf()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.title").value("거지"),
                        jsonPath("$.currentUserCount").value(10),
                        jsonPath("$.unReadMessageCount").value(100L),
                        jsonPath("$.chatRoomId").value(1))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방_목록_단건_조회_테스트_실패_채팅방이_없는_경우() throws Exception {
        // given
        Long chatRoomId = 1L;
        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId;

        given(chatRoomService.findOneMyChatRoomByChatRoomId(any(), any()))
                .willThrow(new ChatException(CHAT_ROOM_NOT_FOUND));
        // when // then
        mockMvc
                .perform(get(url).with(csrf()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.errorName").value(CHAT_ROOM_NOT_FOUND.getErrorName()),
                        jsonPath("$.errorMessage").value(CHAT_ROOM_NOT_FOUND.getErrorMessage()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방_목록_단건_조회_테스트_실패_유저가_채팅방에_속하지_않는_경우() throws Exception {
        // given
        Long chatRoomId = 1L;
        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId;

        given(chatRoomService.findOneMyChatRoomByChatRoomId(any(), any()))
                .willThrow(new CustomException(FORBIDDEN));
        // when // then
        mockMvc
                .perform(get(url).with(csrf()))
                .andExpectAll(
                        status().isForbidden(),
                        jsonPath("$.errorName").value(FORBIDDEN.getErrorName()),
                        jsonPath("$.errorMessage").value(FORBIDDEN.getErrorMessage()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방_상세_조회_테스트_성공() throws Exception {
        Long chatRoomId = 1L;
        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId + "/detail";
        UserEntity users =
                UserEntity.builder()
                        .id(1L)
                        .nickname("1234")
                        .email("ssafy@gmail.com")
                        .password("1234")
                        .build();

        UserEntity users2 =
                UserEntity.builder()
                        .id(2L)
                        .nickname("짭수")
                        .email("ssafy@gmail.com")
                        .password("1234")
                        .build();

        HashtagEntity h1 = HashtagEntity.builder().name("20대").build();
        HashtagEntity h2 = HashtagEntity.builder().name("관악구").build();
        HashtagEntity h3 = HashtagEntity.builder().name("10만원 미만").build();
        ChatRoomEntity chatRoom = null;
        ChatRoomHashtagEntity crh1 =
                ChatRoomHashtagEntity.builder().chatRoom(chatRoom).hashtag(h1).sequence(1).build();
        ChatRoomHashtagEntity crh2 =
                ChatRoomHashtagEntity.builder().chatRoom(chatRoom).hashtag(h2).sequence(2).build();
        ChatRoomHashtagEntity crh3 =
                ChatRoomHashtagEntity.builder().chatRoom(chatRoom).hashtag(h3).sequence(3).build();
        UserChatRoomEntity ucr =
                UserChatRoomEntity.builder()
                        .chatRoom(chatRoom)
                        .user(users)
                        .lastAccessTime(Instant.ofEpochMilli(100L))
                        .build();
        UserChatRoomEntity uc2 =
                UserChatRoomEntity.builder()
                        .chatRoom(chatRoom)
                        .user(users2)
                        .lastAccessTime(Instant.ofEpochMilli(100L))
                        .build();

        chatRoom =
                ChatRoomEntity.builder()
                        .id(1L)
                        .title("거지방")
                        .capability(10)
                        .host(users)
                        .chatRoomHashtags(List.of(crh1, crh2, crh3))
                        .userChatRooms(List.of(ucr, uc2))
                        .createdAt(Instant.ofEpochMilli(1000L))
                        .region(RegionEntity.builder().regionId(1L).si("서울").gungu("서초구").build())
                        .build();

        given(chatRoomService.findDetailByChatRoomId(any(), any()))
                .willReturn(new ChatRoomDetailResponse(chatRoom, 2L));
        // when // then
        mockMvc
                .perform(get(url).with(csrf()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.chatRoomId").value(1L),
                        jsonPath("$.title").value("거지방"),
                        jsonPath("$.imgSrc").doesNotExist(),
                        jsonPath("$.capability").value(10),
                        jsonPath("$.currentUserCount").value(2),
                        jsonPath("$.hashtags.length()").value(3),
                        jsonPath("$.host.userId").value(1L),
                        jsonPath("$.host.nickname").value("1234"),
                        jsonPath("$.host.profileImageSrc").doesNotExist(),
                        jsonPath("$.host.isCurrentUser").value(false),
                        jsonPath("$.isHost").value(false),
                        jsonPath("$.members.length()").value(2))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방_상세_조회_테스트_실패_채팅방이_없는_경우() throws Exception {
        // given
        Long chatRoomId = 1L;
        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId + "/detail";

        given(chatRoomService.findDetailByChatRoomId(any(), any()))
                .willThrow(new ChatException(CHAT_ROOM_NOT_FOUND));
        // when // then
        mockMvc
                .perform(get(url).with(csrf()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.errorName").value(CHAT_ROOM_NOT_FOUND.getErrorName()),
                        jsonPath("$.errorMessage").value(CHAT_ROOM_NOT_FOUND.getErrorMessage()))
                .andDo(print());
    }

    @WithMockUser
    @Test
    void 채팅방_상세_조회_테스트_실패_유저가_채팅방에_속하지_않는_경우() throws Exception {
        // given
        Long chatRoomId = 1L;
        String url = CHAT_ROOM_API_PREFIX + "/" + chatRoomId + "/detail";

        given(chatRoomService.findDetailByChatRoomId(any(), any()))
                .willThrow(new CustomException(FORBIDDEN));
        // when // then
        mockMvc
                .perform(get(url).with(csrf()))
                .andExpectAll(
                        status().isForbidden(),
                        jsonPath("$.errorName").value(FORBIDDEN.getErrorName()),
                        jsonPath("$.errorMessage").value(FORBIDDEN.getErrorMessage()))
                .andDo(print());
    }
}

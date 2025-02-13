package jsbh.Jusangbokhap.chat.chatroom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

	@InjectMocks
	private ChatRoomService chatRoomService;

	@Mock
	private ChatRoomRepository chatRoomRepository;

	@DisplayName("두 사용자가 참여자로 생성된 채팅방이 존재하면 새로운 방이 만들어지지 않는다.")
	@Test
	void testGetOrCreateChatRoom_ExistingRoom() {
		Long user1 = 1L;
		Long user2 = 2L;
		ChatRoom existingChatRoom = new ChatRoom(user1, user2);

		when(chatRoomRepository.findByUsers(user1, user2)).thenReturn(Optional.of(existingChatRoom));

		// when
		ChatRoom chatRoom = chatRoomService.getOrCreateChatRoom(user1, user2);

		// then
		assertNotNull(chatRoom);
		assertEquals(user1, chatRoom.getParticipant1Id());
		assertEquals(user2, chatRoom.getParticipant2Id());
		verify(chatRoomRepository, never()).save(any(ChatRoom.class));
	}

	@DisplayName("두 사용자가 참여자로 생성된 채팅방이 없다면 새로운 채팅방이 만들어진다.")
	@Test
	void testGetOrCreateChatRoom_NewRoom() {
	    // given
		Long user1 = 1L;
		Long user2 = 2L;

		when(chatRoomRepository.findByUsers(user1, user2)).thenReturn(Optional.empty());
		when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		ChatRoom chatRoom = chatRoomService.getOrCreateChatRoom(user1, user2);

		// then
		assertNotNull(chatRoom);
		assertEquals(user1, chatRoom.getParticipant1Id());
		assertEquals(user2, chatRoom.getParticipant2Id());
		verify(chatRoomRepository).save(any(ChatRoom.class));
	}

	@DisplayName("사용자가 참여한 채팅방이 있을 때 사용자 아이디로 조회하면 참여 중인 채팅방들이 조회된다.")
	@Test
	public void testFindChatRooms_ReturnsChatRooms() {
		// given
		Long userId = 1L;
		List<ChatRoom> chatRooms = List.of(new ChatRoom(1L, 2L), new ChatRoom(1L, 3L));

		when(chatRoomRepository.findByUserId(userId)).thenReturn(chatRooms);

		// when
		List<ChatRoom> result = chatRoomService.findChatRooms(userId);

		// then
		assertNotNull(result);
		assertEquals(2, result.size());
	}

	@DisplayName("사용자가 참여한 채팅방이 없을 때 사용자 아이디로 조회하면 빈 리스트가 반환된다.")
	@Test
	public void testFindChatRooms_ReturnsEmptyList() {
		// given
		Long userId = 1L;

		when(chatRoomRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

		// when
		List<ChatRoom> result = chatRoomService.findChatRooms(userId);

		// then
		assertNotNull(result);
		assertEquals(0, result.size());
	}
}

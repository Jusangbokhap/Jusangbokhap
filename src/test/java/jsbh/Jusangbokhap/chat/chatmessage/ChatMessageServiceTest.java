package jsbh.Jusangbokhap.chat.chatmessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceTest {

	@InjectMocks
	private ChatMessageService chatMessageService;

	@Mock
	private ChatMessageRepository chatMessageRepository;

	@DisplayName("채팅방에 저장된 메시지가 리스트로 반환된다.")
	@Test
	public void testGetMessages_ReturnsMessages() {
		// given
		Long roomId = 1L;
		List<ChatMessage> messages = List.of(
			new ChatMessage(roomId, 1L, 2L, "Hello", new Date(), false),
			new ChatMessage(roomId, 2L, 1L, "Hi", new Date(), false)
		);

		when(chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId)).thenReturn(messages);

		// when
		List<ChatMessage> result = chatMessageService.getMessages(roomId);

		// then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Hello", result.get(0).getMessage());
		assertEquals("Hi", result.get(1).getMessage());
	}

	@DisplayName("채팅방에 저장된 메시지가 없다면 빈 리스트가 반환된다.")
	@Test
	public void testGetMessages_ReturnsEmptyList() {
		// given
		Long roomId = 1L;
		when(chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId)).thenReturn(Collections.emptyList());

		// when
		List<ChatMessage> result = chatMessageService.getMessages(roomId);

		// then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
}
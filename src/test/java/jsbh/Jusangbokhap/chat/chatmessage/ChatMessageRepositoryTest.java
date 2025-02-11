package jsbh.Jusangbokhap.chat.chatmessage;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatMessageRepositoryTest {

	@Autowired
	private ChatMessageRepository chatMessageRepository;

	@Test
	public void testSaveAndFindMessages() {
		// given
		ChatMessage message = ChatMessage.builder()
			.roomId(1L)
			.senderId(1L)
			.receiverId(2L)
			.message("Hello, World!")
			.timestamp(new Date())
			.build();
		chatMessageRepository.save(message);

		// when
		List<ChatMessage> messages = chatMessageRepository.findByRoomId(1L);

		// then
		assertThat(messages).isNotEmpty();
		assertThat(messages.get(0).getMessage()).isEqualTo("Hello, World!");
		assertThat(messages.get(0).getRoomId()).isEqualTo(1L);
	}
}

package jsbh.Jusangbokhap.chat.chatmessage;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import jsbh.Jusangbokhap.chat.MongoTestConfig;

@DataMongoTest
@Import(MongoTestConfig.class)
@ContextConfiguration(classes = MongoTestConfig.class)
class ChatMessageRepositoryTest {

	@Autowired
	private ChatMessageRepository chatMessageRepository;

	@AfterEach
	void cleanUp() {
		chatMessageRepository.deleteAll();
	}

	@DisplayName("채팅 메시지를 저장할 수 있다.")
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

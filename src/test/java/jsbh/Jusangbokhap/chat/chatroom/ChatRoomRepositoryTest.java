package jsbh.Jusangbokhap.chat.chatroom;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ChatRoomRepositoryTest {

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Test
	public void testChatRoomCreation() {
		ChatRoom room = ChatRoom.builder()
			.participant1Id(1L)
			.participant2Id(2L)
			.build();

		chatRoomRepository.save(room);
		Assertions.assertThat(chatRoomRepository.findByUsers(1L, 2L)).isPresent();
		Assertions.assertThat(chatRoomRepository.findByUsers(2L, 1L)).isPresent();
	}
}
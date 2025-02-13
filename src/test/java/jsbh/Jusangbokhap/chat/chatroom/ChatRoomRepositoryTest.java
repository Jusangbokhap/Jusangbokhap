package jsbh.Jusangbokhap.chat.chatroom;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatRoomRepositoryTest {

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@DisplayName("두 참여자로 구성된 채팅방이 생성된다.")
	@Test
	public void testCreateAndFindChatRoom() {
		ChatRoom room = ChatRoom.builder()
			.participant1Id(1L)
			.participant2Id(2L)
			.build();

		chatRoomRepository.save(room);

		Assertions.assertThat(chatRoomRepository.findByUsers(1L, 2L)).isPresent();
		Assertions.assertThat(chatRoomRepository.findByUsers(2L, 1L)).isPresent();
	}
}

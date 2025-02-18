package jsbh.Jusangbokhap.api.chat.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import jsbh.Jusangbokhap.domain.chat.entity.jpa.ChatRoom;
import jsbh.Jusangbokhap.domain.chat.repository.jpa.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;
	// private final UserRepository userRepository;

	// 채팅방 생성
	public ChatRoom getOrCreateChatRoom(Long senderId, Long receiverId) {
		Long user1 = Math.min(senderId, receiverId);
		Long user2 = Math.max(receiverId, senderId);

		log.info("Searching for chat room between user {} and user {}", user1, user2);

		Optional<ChatRoom> chatRoom = chatRoomRepository.findByUsers(user1, user2);

		return chatRoom.orElseGet(() -> {
			log.info("No existing chat room found. Creating a new chat room for users {} and {}", user1, user2);
			ChatRoom newChatRoom = ChatRoom.builder()
				.participant1Id(user1)
				.participant2Id(user2)
				.build();
			return chatRoomRepository.save(newChatRoom);
		});
	}

	public List<ChatRoom> findChatRooms(Long userId) {
		log.info("Fetching chat rooms for user {}", userId);
		return chatRoomRepository.findByUserId(userId);
	}
}

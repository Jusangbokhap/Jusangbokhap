package jsbh.Jusangbokhap.chat.chatroom;

import org.springframework.stereotype.Service;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;

	public ChatRoom getOrCreateChatRoom(Long senderId, Long receiverId) {
		Long user1 = Math.min(senderId, receiverId);
		Long user2 = Math.max(receiverId, senderId);

		Optional<ChatRoom> chatRoom = chatRoomRepository.findByUsers(user1, user2);

		return chatRoom.orElseGet(() -> {
			ChatRoom newChatRoom = ChatRoom.builder()
				.userId1(user1)
				.userId2(user2)
				.build();
			return chatRoomRepository.save(newChatRoom);
		});
	}
}

package jsbh.Jusangbokhap.chat.chatroom;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;
	// private final UserRepository userRepository;

	// 채팅방 생성
	public ChatRoom getOrCreateChatRoom(Long senderId, Long receiverId) {

		Long user1 = Math.min(senderId, receiverId);
		Long user2 = Math.max(receiverId, senderId);

		// TODO: user 확인
		// userRepository.findById(user1).orElseThrow(() -> new IllegalArgumentException());
		// userRepository.findById(user2).orElseThrow(() -> new IllegalArgumentException());

		Optional<ChatRoom> chatRoom = chatRoomRepository.findByUsers(user1, user2);

		return chatRoom.orElseGet(() -> {
			ChatRoom newChatRoom = ChatRoom.builder()
				.participant1Id(user1)
				.participant2Id(user2)
				.build();
			return chatRoomRepository.save(newChatRoom);
		});
	}

	public List<ChatRoom> findChatRooms(Long userId) {
		return chatRoomRepository.findByUserId(userId);
	}
}

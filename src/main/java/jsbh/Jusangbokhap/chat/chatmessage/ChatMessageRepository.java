package jsbh.Jusangbokhap.chat.chatmessage;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
	List<ChatMessage> findByRoomId(String roomId);
}
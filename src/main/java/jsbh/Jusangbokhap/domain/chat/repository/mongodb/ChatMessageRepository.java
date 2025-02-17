package jsbh.Jusangbokhap.domain.chat.repository.mongodb;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import jsbh.Jusangbokhap.domain.chat.entity.mongodb.ChatMessage;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
	List<ChatMessage> findByRoomId(Long roomId);
	List<ChatMessage> findByRoomIdOrderByTimestampAsc(Long roomId);
	List<ChatMessage> findByRoomIdAndReceiverIdAndReadFalse(Long roomId, Long receiverId);
}
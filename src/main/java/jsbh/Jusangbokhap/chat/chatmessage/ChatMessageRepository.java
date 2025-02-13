package jsbh.Jusangbokhap.chat.chatmessage;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
	List<ChatMessage> findByRoomId(Long roomId);
	List<ChatMessage> findByRoomIdOrderByTimestampAsc(Long roomId);

	@Query("{'roomId': ?0, 'receiverId': ?1, 'read': false, '_id': { $gt: ?2 } }")
	List<ChatMessage> findUnreadMessagesAfter(Long roomId, Long receiverId, ObjectId lastReadMessageId);

	ChatMessage findTopByRoomIdOrderByTimestampDesc(Long roomId);

	// 채팅방 내 처음으로 읽지 않은 메시지 찾기 (가장 오래된 읽지 않은 메시지)
	@Query(value = "{ 'roomId': ?0, 'read': false }", sort = "{ 'timestamp': 1 }")
	ChatMessage findFirstByRoomIdAndReadFalseOrderByTimestampAsc(Long roomId);

	// 처음으로 읽지 않은 메시지 이후 모든 메시지 읽음 처리
	@Query("{ 'roomId': ?0, 'messageId': { $gte: ?1 }, 'read': false }")
	List<ChatMessage> findByRoomIdAndMessageIdGreaterThanEqualAndReadFalse(Long roomId, String firstUnreadMessageId);
}
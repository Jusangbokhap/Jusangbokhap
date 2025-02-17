package jsbh.Jusangbokhap.api.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsbh.Jusangbokhap.domain.chat.repository.mongodb.ChatMessageRepository;
import jsbh.Jusangbokhap.domain.chat.entity.mongodb.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

	private final ChatMessageRepository chatMessageRepository;
	private final ObjectMapper objectMapper;

	public List<ChatMessage> getMessages(Long roomId) {
		return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
	}

	// 메시지 전달
	public void deliverMessage(ChatMessage chatMessage, WebSocketSession receiverSession) throws Exception {
		// 읽음 상태 업데이트
		ChatMessage updatedMessage = markMessageAsRead(chatMessage);

		// 메시지 직렬화 후 전송
		String messageJson = objectMapper.writeValueAsString(updatedMessage);
		receiverSession.sendMessage(new TextMessage(messageJson));
	}

	// 특정 방(roomId)에서 사용자가 읽지 않은 메시지들을 읽음 처리
	public void markAllMessagesAsRead(Long roomId, Long userId) {
		List<ChatMessage> unreadMessages = chatMessageRepository.findByRoomIdAndReceiverIdAndReadFalse(roomId, userId);

		if (!unreadMessages.isEmpty()) {
			unreadMessages.forEach(message -> message.updateReadStatus(true));
			chatMessageRepository.saveAll(unreadMessages);
			log.info("Updated {} unread messages as read for user {} in room {}", unreadMessages.size(), userId, roomId);
		} else {
			log.info("No unread messages for user {} in room {}", userId, roomId);
		}
	}

	// 메시지 단건에 대해 읽음 상태 업데이트 후 저장
	public ChatMessage markMessageAsRead(ChatMessage chatMessage) {
		chatMessage.updateReadStatus(true);
		return chatMessageRepository.save(chatMessage);
	}
}

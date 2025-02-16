package jsbh.Jusangbokhap.chat;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsbh.Jusangbokhap.chat.chatmessage.ChatMessage;
import jsbh.Jusangbokhap.chat.chatmessage.ChatMessageRepository;
import jsbh.Jusangbokhap.chat.chatmessage.ChatMessageRequest;
import jsbh.Jusangbokhap.chat.chatroom.ChatRoomRepository;
import jsbh.Jusangbokhap.chat.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatRoomService chatRoomService;

	// 세션에 있는 모든 사용자 저장
	private static final Map<Long, WebSocketSession> sessions = new HashMap<>();
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		Long userId = getUserIdFromSession(session);
		Long roomId = getRoomIdFromSession(session);

		if (userId != null && roomId != null) {
			sessions.put(userId, session);
			log.info("User {} enter room {}", userId, roomId);
			markMessagesAsRead(roomId, userId);
		}
	}

	private void markMessagesAsRead(Long roomId, Long userId) {
		ChatMessage lastMessage = chatMessageRepository.findTopByRoomIdOrderByTimestampDesc(roomId);

		// 마지막 메시지를 현재 입장한 사용자가 보냈다면 읽음상태 업데이트 하지 않음
		if (lastMessage != null && lastMessage.getSenderId().equals(userId)) {
			log.info("User {} is the last sender. No need to update read status.", userId);
			return;
		}

		// 마지막 메시지를 현재 입장한 사용자가 받은거라면 읽지 않은 메시지 전부 읽음으로 업데이트
		ChatMessage firstUnreadMessage = chatMessageRepository.findFirstByRoomIdAndReadFalseOrderByTimestampAsc(
			roomId);
		if (firstUnreadMessage == null) {
			log.info("No unread messages found for user {} in room {}", userId, roomId);
			return; // 모든 메시지가 이미 읽힘
		}

		List<ChatMessage> unreadMessages = chatMessageRepository.findByRoomIdAndMessageIdGreaterThanEqualAndReadFalse(
			roomId, firstUnreadMessage.getId());

		if (!unreadMessages.isEmpty()) {
			unreadMessages.forEach(message -> message.updateReadStatus(true));
			chatMessageRepository.saveAll(unreadMessages);

			log.info("Updated {} unread messages as read for user {} in room {}", unreadMessages.size(), userId, roomId);
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		ChatMessageRequest dto = objectMapper.readValue(message.getPayload(), ChatMessageRequest.class);

		ChatMessage chatMessage = ChatMessage.toEntity(dto);

		log.info("Received from {} to {}: {}", chatMessage.getSenderId(), chatMessage.getReceiverId(),
			chatMessage.getMessage());

		// mongoDB에 메시지 저장
		chatMessageRepository.save(chatMessage);

		// 상대방에게 메시지 전송
		sendMessageToUser(chatMessage);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		Long userId = getUserIdFromSession(session);
		sessions.remove(userId);
	}

	// 특정 사용자에게 메시지 보내기

	private void sendMessageToUser(ChatMessage chatMessage) throws Exception {
		WebSocketSession receiverSession = sessions.get(chatMessage.getReceiverId());
		if (receiverSession != null && receiverSession.isOpen()) {
			chatMessage.updateReadStatus(true);
			chatMessageRepository.save(chatMessage);

			String messageJson = objectMapper.writeValueAsString(chatMessage);
			receiverSession.sendMessage(new TextMessage(messageJson));
		}
	}

	private Long getRoomIdFromSession(WebSocketSession session) {
		String query = session.getUri().getQuery();
		Map<String, String> params = parseQueryParams(query);
		return params.containsKey("roomId") ? Long.parseLong(params.get("roomId")) : null;
	}

	// 사용자 ID 가져오기
	private Long getUserIdFromSession(WebSocketSession session) {
		// String userId = (String)session.getAttributes().get("userId");

		String query = session.getUri().getQuery();
		Map<String, String> params = parseQueryParams(query);
		return params.containsKey("userId") ? Long.parseLong(params.get("userId")) : null;
	}

	private Map<String, String> parseQueryParams(String query) {
		Map<String, String> map = new HashMap<>();
		if (query != null) {
			for (String param : query.split("&")) {
				String[] pair = param.split("=");
				if (pair.length == 2) {
					map.put(pair[0], pair[1]);
				}
			}
		}
		return map;
	}
}

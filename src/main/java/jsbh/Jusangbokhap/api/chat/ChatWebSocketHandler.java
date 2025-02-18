package jsbh.Jusangbokhap.api.chat;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsbh.Jusangbokhap.api.chat.service.ChatMessageService;
import jsbh.Jusangbokhap.domain.chat.entity.mongodb.ChatMessage;
import jsbh.Jusangbokhap.domain.chat.repository.mongodb.ChatMessageRepository;
import jsbh.Jusangbokhap.api.chat.dto.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatMessageService chatMessageService;

	// 세션에 있는 모든 사용자 저장
	private static final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		Long userId = getUserIdFromSession(session);
		Long roomId = getRoomIdFromSession(session);

		if (userId != null && roomId != null) {
			sessions.put(userId, session);
			log.info("User {} enter room {}", userId, roomId);
			chatMessageService.markAllMessagesAsRead(roomId, userId);
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		ChatMessageRequest chatMessageRequest = objectMapper.readValue(message.getPayload(), ChatMessageRequest.class);
		ChatMessage chatMessage = ChatMessage.toEntity(chatMessageRequest);

		log.info("Received from {} to {}: {}", chatMessage.getSenderId(), chatMessage.getReceiverId(),
				chatMessage.getMessage());

		ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

		WebSocketSession receiverSession = sessions.get(savedMessage.getReceiverId());
		if (receiverSession != null && receiverSession.isOpen()) {
			chatMessageService.deliverMessage(savedMessage, receiverSession);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		Long userId = getUserIdFromSession(session);
		if (userId != null) {
			sessions.remove(userId);
		}

		if (session.isOpen()) {
			try {
				session.close();
			} catch (Exception e) {
				log.error("Error while closing WebSocket session for user {}", userId, e);
			}
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

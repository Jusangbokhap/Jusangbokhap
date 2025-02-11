package jsbh.Jusangbokhap.chat;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsbh.Jusangbokhap.chat.chatmessage.ChatMessage;
import jsbh.Jusangbokhap.chat.chatroom.ChatRoom;
import jsbh.Jusangbokhap.chat.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

	private static final Map<String, WebSocketSession> sessions = new HashMap<>();

	private final ChatRoomService chatRoomService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		String userId = getUserIdFromSession(session);
		if (userId != null) {
			sessions.put(userId, session);
			log.info("User Connected: {}", userId);
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		ChatMessage chatMessage = new ObjectMapper().readValue(payload, ChatMessage.class);
		log.info("\uD83D\uDCE9 Received from {} to {}: {}", chatMessage.getSender(), chatMessage.getReceiver(),
			chatMessage.getMessage());

		Long senderId = Long.valueOf(chatMessage.getSender());
		Long receiverId = Long.valueOf(chatMessage.getReceiver());

		ChatRoom chatRoom = chatRoomService.getOrCreateChatRoom(senderId, receiverId);

		// 상대방에게 메시지 전송
		sendMessageToUser(chatMessage.getReceiver(), payload);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		String userId = getUserIdFromSession(session);
		sessions.remove(userId);
	}

	// 세션에서 사용자 ID 가져오기
	private String getUserIdFromSession(WebSocketSession session) {
		String query = Objects.requireNonNull(session.getUri()).getQuery();
		if (query != null && query.contains("userId=")) {
			return query.split("=")[1];
		}
		return null;
	}

	// 특정 사용자에게 메시지 보내기
	private void sendMessageToUser(String userId, String message) throws Exception {
		WebSocketSession userSession = sessions.get(userId);
		if (userSession != null && userSession.isOpen()) {
			userSession.sendMessage(new TextMessage(message));
		}
	}

	private String extractReceiverId(String payload) {
		return payload.split(":")[0];
	}

	private String extractMessageContent(String payload) {
		return payload.split(":")[1];
	}
}
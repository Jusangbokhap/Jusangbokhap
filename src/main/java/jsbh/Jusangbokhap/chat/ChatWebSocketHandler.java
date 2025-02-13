package jsbh.Jusangbokhap.chat;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsbh.Jusangbokhap.chat.chatmessage.ChatMessage;
import jsbh.Jusangbokhap.chat.chatmessage.ChatMessageRepository;
import jsbh.Jusangbokhap.chat.chatmessage.ChatMessageRequest;
import jsbh.Jusangbokhap.chat.chatroom.ChatRoom;
import jsbh.Jusangbokhap.chat.chatroom.ChatRoomRepository;
import jsbh.Jusangbokhap.chat.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatRoomService chatRoomService;

	// 세션에 있는 모든 사용자 저장>??
	private static final Map<Long, WebSocketSession> sessions = new HashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		Long userId = getUserIdFromSession(session);
		if (userId != null) {
			sessions.put(userId, session);
			log.info("User Connected: {}", userId);
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		ChatMessageRequest dto = objectMapper.readValue(message.getPayload(), ChatMessageRequest.class);

		Long roomId = chatRoomService.getOrCreateChatRoom(dto.getSenderId(), dto.getReceiverId()).getId();

		ChatMessage chatMessage = ChatMessage.toEntity(roomId, dto);
		// mongoDB에 메시지 저장
		chatMessageRepository.save(chatMessage);

		log.info("Received from {} to {}: {}", chatMessage.getSenderId(), chatMessage.getReceiverId(),
			chatMessage.getMessage());

		// 상대방에게 메시지 전송
		sendMessageToUser(chatMessage.getReceiverId(), chatMessage.getMessage());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		Long userId = getUserIdFromSession(session);
		sessions.remove(userId);
	}

	// 특정 사용자에게 메시지 보내기
	private void sendMessageToUser(Long receiverId, String message) throws Exception {
		WebSocketSession userSession = sessions.get(receiverId);
		if (userSession != null && userSession.isOpen()) {
			userSession.sendMessage(new TextMessage(message));
		}
	}

	// 세션에서 사용자 ID 가져오기
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

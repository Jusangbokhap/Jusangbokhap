package jsbh.Jusangbokhap.chat.chatroom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketChatTest {

	private final String WEBSOCKET_URI = "ws://localhost:8080/chat";

	@Test
	public void testWebSocketConnection() throws Exception {
		WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		CompletableFuture<StompSession> futureSession = stompClient.connectAsync(WEBSOCKET_URI, new StompSessionHandlerAdapter() {
			@Override
			public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
				System.out.println("✅ Connected to WebSocket");
			}
		});

		StompSession session = futureSession.get(3, TimeUnit.SECONDS);
		assertThat(session.isConnected()).isTrue();
		session.disconnect();
	}
}
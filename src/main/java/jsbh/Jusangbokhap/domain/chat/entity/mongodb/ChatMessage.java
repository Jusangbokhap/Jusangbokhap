package jsbh.Jusangbokhap.domain.chat.entity.mongodb;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jsbh.Jusangbokhap.api.chat.dto.ChatMessageRequest;
import lombok.*;

@Document(collection = "chat_messages")
@Getter
@NoArgsConstructor
public class ChatMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;

	@Indexed
	private Long roomId;

	private Long senderId;

	private Long receiverId;

	private String message;

	private Date timestamp;

	private boolean read;

	@Builder
	public ChatMessage(Long roomId, Long senderId, Long receiverId, String message, Date timestamp,
					   boolean read) {
		this.roomId = roomId;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.message = message;
		this.timestamp = timestamp;
		this.read = read;
	}

	public static ChatMessage toEntity(ChatMessageRequest dto) {
		return ChatMessage.builder()
				.roomId(dto.getRoomId())
				.senderId(dto.getSenderId())
				.receiverId(dto.getReceiverId())
				.message(dto.getMessage())
				.timestamp(Date.from(Instant.now()))
				.read(false)
				.build();
	}

	public void updateReadStatus(boolean isRead) {
		this.read = isRead;
	}
}

package jsbh.Jusangbokhap.chat.chatmessage;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection = "chat_messages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

	@Id
	private String id;

	private String roomId;
	private String sender;
	private String receiver;
	private String message;
}
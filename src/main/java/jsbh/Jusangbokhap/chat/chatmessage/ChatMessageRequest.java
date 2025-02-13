package jsbh.Jusangbokhap.chat.chatmessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
	private Long senderId;
	private Long receiverId;
	private String message;
}

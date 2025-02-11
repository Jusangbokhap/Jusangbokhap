package jsbh.Jusangbokhap.chat;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import jsbh.Jusangbokhap.chat.chatmessage.ChatMessage;
import jsbh.Jusangbokhap.chat.chatmessage.ChatMessageRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatMessageRepository chatMessageRepository;

	@GetMapping("/messages/{roomId}")
	public List<ChatMessage> getMessages(@PathVariable String roomId) {
		return chatMessageRepository.findByRoomId(roomId);
	}
}
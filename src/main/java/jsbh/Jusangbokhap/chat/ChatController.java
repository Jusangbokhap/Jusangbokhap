package jsbh.Jusangbokhap.chat;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import jsbh.Jusangbokhap.chat.chatmessage.ChatMessage;
import jsbh.Jusangbokhap.chat.chatmessage.ChatMessageService;
import jsbh.Jusangbokhap.chat.chatroom.ChatRoom;
import jsbh.Jusangbokhap.chat.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService chatRoomService;
	private final ChatMessageService chatMessageService;

	@PostMapping("/start")
	public void startChat(@RequestParam Long hostId, @RequestParam Long guestId) {
		// TODO: request 사용자 Id JWT or session 기반으로 변경
		chatRoomService.getOrCreateChatRoom(guestId, hostId);
	}

	@GetMapping("/rooms/{userId}")
	public List<ChatRoom> getUserChatRooms(@PathVariable Long userId) {
		return chatRoomService.findChatRooms(userId);
	}

	@GetMapping("/{roomId}")
	public List<ChatMessage> getMessages(@PathVariable Long roomId) {
		return chatMessageService.getMessages(roomId);
	}
}

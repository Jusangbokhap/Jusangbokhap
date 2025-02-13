package jsbh.Jusangbokhap.chat.chatmessage;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

	private final ChatMessageRepository chatMessageRepository;

	public List<ChatMessage> getMessages(Long roomId) {
		return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
	}
}

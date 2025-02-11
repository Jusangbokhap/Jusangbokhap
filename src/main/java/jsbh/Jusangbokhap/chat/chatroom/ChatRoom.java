package jsbh.Jusangbokhap.chat.chatroom;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatroom", uniqueConstraints = @UniqueConstraint(columnNames = {"userId1", "userId2"}))
public class ChatRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId1;
	private Long userId2;

	@Builder
	private ChatRoom(Long userId1, Long userId2) {
		this.userId1 = userId1;
		this.userId2 = userId2;
	}
}
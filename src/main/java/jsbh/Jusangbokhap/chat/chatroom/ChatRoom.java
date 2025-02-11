package jsbh.Jusangbokhap.chat.chatroom;

import jakarta.persistence.*;
import jsbh.Jusangbokhap.domain.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatroom", uniqueConstraints = @UniqueConstraint(columnNames = {"participant1Id", "participant2Id"}))
public class ChatRoom extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long participant1Id;

	private Long participant2Id;

	@Builder
	public ChatRoom(Long participant1Id, Long participant2Id) {
		this.participant1Id = participant1Id;
		this.participant2Id = participant2Id;
	}
}
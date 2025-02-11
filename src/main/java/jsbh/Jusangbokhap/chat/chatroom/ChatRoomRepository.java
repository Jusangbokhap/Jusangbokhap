package jsbh.Jusangbokhap.chat.chatroom;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	@Query("SELECT c FROM ChatRoom c WHERE (c.userId1 = :userId1 AND c.userId2 = :userId2) OR (c.userId1 = :userId2 AND c.userId2 = :userId1)")
	Optional<ChatRoom> findByUsers(@Param("userId1") Long user1, @Param("userId2") Long user2);
}
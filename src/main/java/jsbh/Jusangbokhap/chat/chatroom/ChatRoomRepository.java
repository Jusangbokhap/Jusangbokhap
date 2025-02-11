package jsbh.Jusangbokhap.chat.chatroom;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	@Query("SELECT c FROM ChatRoom c WHERE (c.participant1Id = :participant1Id AND c.participant2Id = :participant2Id) OR (c.participant1Id = :participant2Id AND c.participant2Id = :participant1Id)")
	Optional<ChatRoom> findByUsers(@Param("participant1Id") Long participant1Id, @Param("participant2Id") Long participant2Id);

	@Query("SELECT c FROM ChatRoom c WHERE c.participant1Id = :userId OR c.participant2Id = :userId")
	List<ChatRoom> findByUserId(@Param("userId") Long userId);

}
package jsbh.Jusangbokhap.domain.user.repository;

import jsbh.Jusangbokhap.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}

package jsbh.Jusangbokhap.domain.availableDate.repository;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AvailableDateRepository {

    private final EntityManager em;

    public Optional<AvailableDate> findByAvailableDateId(Long availableDateId) {
        return Optional.ofNullable(em.find(AvailableDate.class, availableDateId));
    }
}

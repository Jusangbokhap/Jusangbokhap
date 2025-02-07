package jsbh.Jusangbokhap.domain.accommodation.repository;

import jakarta.persistence.EntityManager;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccommodationRepository {

    private final EntityManager em;

    public Accommodation save(Accommodation accommodation) {
        em.persist(accommodation);
        return accommodation;
    }
}

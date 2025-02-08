package jsbh.Jusangbokhap.domain.accommodation.repository;

import jakarta.persistence.EntityManager;
import java.util.Optional;
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

    public Optional<Accommodation> findByAccommodationId(Long accommodationId) {
        return Optional.ofNullable(em.find(Accommodation.class, accommodationId));
    }

    public void delete(Accommodation accommodation) {
        em.remove(accommodation);
    }
}

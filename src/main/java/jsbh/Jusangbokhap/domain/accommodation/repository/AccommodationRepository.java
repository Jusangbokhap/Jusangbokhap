package jsbh.Jusangbokhap.domain.accommodation.repository;

import static jsbh.Jusangbokhap.domain.accommodation.QAccommodation.accommodation;
import static jsbh.Jusangbokhap.domain.availableDate.QAvailableDate.availableDate;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDateStatus;
import org.springframework.stereotype.Repository;

@Repository
public class AccommodationRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public AccommodationRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

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

    public List<Accommodation> findAvailableAccommodations(Predicate predicate) {
        return queryFactory
                .selectFrom(accommodation)
                .leftJoin(accommodation.availableDates, availableDate)
                .fetchJoin()
                .distinct()
                .where(availableDate.status.eq(AvailableDateStatus.AVAILABLE), predicate)
                .fetch();
    }
}

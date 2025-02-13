package jsbh.Jusangbokhap.domain.facility.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import jsbh.Jusangbokhap.domain.facility.Facility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FacilityRepository {

    private final EntityManager em;

    public List<Facility> findByAccommodationId(Long id) {
        return em.createQuery("SELECT f FROM Facility f where f.accommodation.id = :id", Facility.class)
                .setParameter("id", id)
                .getResultList();
    }

    @Transactional
    public void saveAll(List<Facility> facilities) {
        for (Facility facility : facilities) {
            em.persist(facility);
        }
    }
}

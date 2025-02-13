package jsbh.Jusangbokhap.domain.facility;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import jsbh.Jusangbokhap.domain.BaseEntity;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Facility extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Enumerated(EnumType.STRING)
    private FacilityCategory category;

    private Integer facilityCount;

    public void updateFacility(Integer facilityCount) {
        this.facilityCount = facilityCount;
        this.setUpdatedAt(LocalDateTime.now());
    }

}

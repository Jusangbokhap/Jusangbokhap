package jsbh.Jusangbokhap.domain.accommodation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import jsbh.Jusangbokhap.domain.BaseEntity;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDate;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDates;
import jsbh.Jusangbokhap.domain.reservation.Reservation;
import jsbh.Jusangbokhap.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Accommodation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accommodationId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String businessName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "accommodationAddress_id")
    private AccommodationAddress address;

    @Column(nullable = false)
    @Embedded
    private AccommodationPrice accommodationPrice;

    @Enumerated(EnumType.STRING)
    private AccommodationType accommodationType;

    @Column
    private String description;

    @Embedded
    private AccommodationCapacity maxGuests;

    @Column
    private String imageUrl;

    //TODO User Service 개발 완료 시 nullable = false 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = true)
    private User host;

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailableDate> availableDates = new ArrayList<>();

    public void updateDetails(
                            String title,
                            String sido,
                            String sigungu,
                            String eupmyeondong,
                            String detail,
                            Double longitude,
                            Double latitude,
                            String description,
                            Integer price,
                            String accommodationType,
                            Integer guests) {

        if (title != null && !title.isEmpty()) {
            this.title = title;
        }

        if (description != null && !description.isEmpty()) {
            this.description = description;
        }

        if (accommodationType != null) {
            this.accommodationType = AccommodationType.valueOf(accommodationType);
        }

        address.updateAddress(sido, sigungu, eupmyeondong, detail, longitude, latitude);
        accommodationPrice.updatePrice(price);
        maxGuests.updateMaxGuests(guests);
    }

    public AvailableDates getAvailableDates() {
        return new AvailableDates(this.availableDates);
    }

    public void addAvailableDate(AvailableDate newAvailableDate) {
        getAvailableDates().add(newAvailableDate, this);
    }

    public void updateAvailableDate(AvailableDate updatedDate) {
        getAvailableDates().update(updatedDate);
    }

}

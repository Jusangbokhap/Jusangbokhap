package jsbh.Jusangbokhap.domain.accommodation;

import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = true)
    private User host;

    @Builder.Default
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailableDate> availableDates = new ArrayList<>();

    public void updateDetails(
            String title,
            AccommodationAddress newAddress,
            String description,
            AccommodationPrice newPrice,
            AccommodationType accommodationType,
            AccommodationCapacity newGuests) {

        if (title != null && !title.isEmpty()) {
            this.title = title;
        }

        if (description != null && !description.isEmpty()) {
            this.description = description;
        }

        if (accommodationType != null) {
            this.accommodationType = accommodationType;
        }

        if (newAddress != null) {
            this.address = newAddress;
        }

        if (newPrice != null) {
            this.accommodationPrice = newPrice;
        }

        if (newGuests != null) {
            this.maxGuests = newGuests;
        }
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
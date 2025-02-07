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
import java.util.ArrayList;
import java.util.List;
import jsbh.Jusangbokhap.domain.BaseEntity;
import jsbh.Jusangbokhap.domain.reservation.Reservation;
import jsbh.Jusangbokhap.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Accommodation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accommodationId;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    private AccommodationType accommodationType;

    @Column
    private String description;

    @Embedded
    private Personnel maxGuests;

    @Column
    private String imageUrl;

    //TODO User Service 개발 완료 시 nullable = false 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = true)
    private User host;

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @Builder
    public Accommodation(String address, int price, AccommodationType accommodationType,
                         String description, Personnel maxGuests, String imageUrl, User host) {
        this.address = address;
        this.price = price;
        this.accommodationType = accommodationType;
        this.description = description;
        this.maxGuests = maxGuests;
        this.imageUrl = imageUrl;
        this.host = host;
        this.reservations = new ArrayList<>();
    }

}

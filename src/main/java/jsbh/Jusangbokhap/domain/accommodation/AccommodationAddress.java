package jsbh.Jusangbokhap.domain.accommodation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AccommodationAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accommodation_address_id")
    private Long id;

    private String sido;

    private String sigungu;

    private String eupmyeondong;

    private String detail;

    @Embedded
    private AccommodationCoordinate coordinate;

    public String getFullAddress() {
        return sido + " " + sigungu + " " + eupmyeondong + " " + detail;
    }

    public void updateAddress(String sido,
                              String sigungu,
                              String eupmyeondong,
                              String detail,
                              Double longitude,
                              Double latitude) {

        if (sido != null && !sido.trim().isEmpty()) {
            this.sido = sido;
        }

        if (sigungu != null && !sigungu.trim().isEmpty()) {
            this.sigungu = sigungu;
        }

        if (eupmyeondong != null && !eupmyeondong.trim().isEmpty()) {
            this.eupmyeondong = eupmyeondong;
        }

        if (detail != null && !detail.trim().isEmpty()) {
            this.detail = detail;
        }

        if (longitude != null && latitude != null) {
            this.coordinate.updateCoordinate(longitude,latitude);
        }
    }
}

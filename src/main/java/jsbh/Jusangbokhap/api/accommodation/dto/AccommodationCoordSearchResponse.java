package jsbh.Jusangbokhap.api.accommodation.dto;

import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccommodationCoordSearchResponse {

    private String businessName;
    private String title;
    private String address;
    private Integer price;
    private String accommodationType;

    @Builder
    private AccommodationCoordSearchResponse(String businessName, String title, String address,
                                             Integer price, String accommodationType) {
        this.businessName = businessName;
        this.title = title;
        this.address = address;
        this.price = price;
        this.accommodationType = accommodationType;
    }

    public static AccommodationCoordSearchResponse of(Accommodation accommodation) {
        return AccommodationCoordSearchResponse.builder()
                .businessName(accommodation.getBusinessName())
                .title(accommodation.getTitle())
                .address(accommodation.getAddress().getFullAddress())
                .price(accommodation.getAccommodationPrice().getPrice())
                .accommodationType(accommodation.getAccommodationType().name())
                .build();
    }

}

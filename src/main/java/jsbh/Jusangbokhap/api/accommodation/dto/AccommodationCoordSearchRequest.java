package jsbh.Jusangbokhap.api.accommodation.dto;

import lombok.Getter;

@Getter
public class AccommodationCoordSearchRequest {

    private Double longitude;
    private Double latitude;
    private Double radius;

}

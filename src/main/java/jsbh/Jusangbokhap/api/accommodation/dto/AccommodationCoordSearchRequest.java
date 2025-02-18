package jsbh.Jusangbokhap.api.accommodation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccommodationCoordSearchRequest {

    @NotNull
    private Double longitude;
    @NotNull
    private Double latitude;
    @NotNull
    private Double radius;
    private Long lastAccommodationId;
//    private int pageSize;

}

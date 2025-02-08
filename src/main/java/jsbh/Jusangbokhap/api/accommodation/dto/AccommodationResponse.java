package jsbh.Jusangbokhap.api.accommodation.dto;

import java.util.List;
import jsbh.Jusangbokhap.api.availableDate.dto.AvailableDateResponse;

public interface AccommodationResponse {
    record Create(
            Long accommodationId
    ) implements AccommodationResponse {
    }

    record Read(
            String address,
            String description,
            Integer price,
            String accommodationType,
            Integer personnel,
            Long userId,
            List<AvailableDateResponse> availableDates
    ) implements AccommodationResponse {
    }

    record Update(
            Long accommodationId
    ) implements AccommodationResponse {
    }

    record Delete(
            Long accommodationId
    ) implements AccommodationResponse {
    }
}

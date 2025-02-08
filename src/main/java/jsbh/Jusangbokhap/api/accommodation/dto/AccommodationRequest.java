package jsbh.Jusangbokhap.api.accommodation.dto;

import java.util.List;
import jsbh.Jusangbokhap.api.availableDate.dto.AvailableDateRequest;

public interface AccommodationRequest {
    record Create(
            String address,
            String description,
            Integer price,
            String accommodationType,
            Integer personnel,
            Integer userId,
            List<AvailableDateRequest> availableDates
    ) implements AccommodationRequest{
    }

    record Update(
            String address,
            String description,
            Integer price,
            String accommodationType,
            Integer personnel
    ) implements AccommodationRequest{
    }
}

package jsbh.Jusangbokhap.api.accommodation.dto;

import java.util.List;
import java.util.Set;
import jsbh.Jusangbokhap.api.availableDate.dto.AvailableDateRequest;

public interface AccommodationRequest {
    record Register(
            String address,
            String description,
            Integer price,
            String accommodationType,
            Integer personnel,
            Integer userId,
            List<AvailableDateRequest> availableDates
    ) implements AccommodationRequest{
    }
}

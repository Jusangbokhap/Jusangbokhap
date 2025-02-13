package jsbh.Jusangbokhap.api.accommodation.dto;

import java.util.List;
import jsbh.Jusangbokhap.api.availableDate.dto.AvailableDateResponse;

public interface AccommodationResponse {
    record Create(
            Long accommodationId
    ) implements AccommodationResponse {
    }

    record Read(
            String businessName,
            String title,
            String address,
            Double x,
            Double y,
            String description,
            Integer price,
            String accommodationType,
            Integer guests,
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

    record Search(
            Long accommodationId,
            String businessName,
            String title,
            String address,
            Integer AccommodationPrice,
            Long totalPrice
    ) implements AccommodationResponse {
    }

    record Address(
            Double x,
            Double y
    )implements AccommodationResponse{
    }

    record Facil(
            String name,
            Integer counter
    )implements AccommodationResponse{
    }
}
